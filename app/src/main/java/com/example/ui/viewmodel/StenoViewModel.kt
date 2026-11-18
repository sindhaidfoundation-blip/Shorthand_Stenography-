package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.CadenceAcousticEngine
import com.example.audio.CadenceAcousticState
import com.example.audio.CourtAudioPlayer
import com.example.audio.CourtAudioState
import com.example.audio.DictationTtsEngine
import com.example.audio.TtsState
import com.example.data.DawnFeedType
import com.example.data.DawnRssFetcher
import com.example.data.PakistanLegalCorpus
import com.example.data.PitmanJsonLexiconRepository
import com.example.data.SettingsPreferences
import com.example.data.StenoUserPreferences
import com.example.domain.model.AudioSourceType
import com.example.domain.model.DictationMode
import com.example.domain.model.LegalCasePassage
import com.example.domain.model.PassageHighlight
import com.example.domain.model.PassageTab
import com.example.domain.model.PitmanJsonDatabaseSnapshot
import com.example.domain.model.StenoAnalysis
import com.example.domain.model.StenoCategory
import com.example.domain.model.StenoPosition
import com.example.domain.model.StenoStrokeSpec
import com.example.domain.model.SymbolExerciseGroup
import com.example.domain.model.SymbolsSubMode
import com.example.domain.model.VoiceCadenceProfile
import com.example.domain.pitman.PitmanAnalyzer
import com.example.export.StenoPdfExporter
import com.example.ui.theme.ThemeAppearance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

data class StenoUiState(
    val activePassage: LegalCasePassage = PakistanLegalCorpus.cases.first(),
    val corpusCases: List<LegalCasePassage> = PakistanLegalCorpus.cases,
    val analysis: StenoAnalysis = PitmanAnalyzer.analyze(PakistanLegalCorpus.cases.first().text),
    val dictationMode: DictationMode = DictationMode.DRAFT_TTS,
    val activeTab: PassageTab = PassageTab.TEXT,
    val customCitation: String = "Custom Draft • High Court",
    val customCourt: String = "Appellate Dictation",
    val customText: String = "",
    val selectedCorpusFilter: String = "All",
    val corpusSearchQuery: String = "",
    val activeTooltipHighlight: PassageHighlight? = null,
    val inspectedSpec: StenoStrokeSpec? = null,
    val showProfileSheet: Boolean = false,
    val showBookmarksSheet: Boolean = false,
    val isExportingAudio: Boolean = false,
    val snackbarMessage: String? = null,
    val prefs: StenoUserPreferences = StenoUserPreferences(),
    val ttsState: TtsState = TtsState(),
    val audioState: CourtAudioState = CourtAudioState(),
    val cadenceState: CadenceAcousticState = CadenceAcousticState(),
    val liveDawnPassages: List<LegalCasePassage> = emptyList(),
    val isFetchingDawn: Boolean = false,
    val selectedSymbolGroup: SymbolExerciseGroup = SymbolExerciseGroup.ALL,
    val symbolSearchQuery: String = "",
    val symbolsSubMode: SymbolsSubMode = SymbolsSubMode.WORKBOOK,
    val jsonLexiconSnapshot: PitmanJsonDatabaseSnapshot = PitmanJsonDatabaseSnapshot(),
    val jsonLookupQuery: String = "",
    val jsonDomainFilter: String = "All"
) {
    val bookmarkedPassages: List<LegalCasePassage>
        get() {
            val pool = (corpusCases + liveDawnPassages).distinctBy { it.id }
            val corpusBookmarked = pool.filter { it.id in prefs.bookmarkedIds }
            val customBookmarked = prefs.customBookmarks.filter { it.id in prefs.bookmarkedIds }
            return customBookmarked + corpusBookmarked
        }

    val isActivePassageBookmarked: Boolean
        get() = activePassage.id in prefs.bookmarkedIds
}

private data class JsonLexiconSlice(
    val subMode: SymbolsSubMode,
    val snapshot: PitmanJsonDatabaseSnapshot,
    val query: String,
    val domainFilter: String
)

class StenoViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = SettingsPreferences(application)
    private val jsonLexiconRepo = PitmanJsonLexiconRepository(application)
    private val cadenceEngine = CadenceAcousticEngine(scope = viewModelScope)

    private var progressionJob: Job? = null

    private val ttsEngine = DictationTtsEngine(
        context = application,
        scope = viewModelScope,
        onWordMilestone = { wordIndex ->
            if (_prefs.value.quarterMinuteChime && wordIndex > 0 && wordIndex % 25 == 0) {
                cadenceEngine.playQuarterMinuteChime()
            }
        },
        onFinishedPassage = { wordsCount ->
            progressionJob?.cancel()
            cadenceEngine.setAmbientMaskingActive(false)
            viewModelScope.launch {
                prefsRepo.addDictatedWords(wordsCount)
            }
        }
    )

    private val courtAudioPlayer = CourtAudioPlayer(
        context = application,
        scope = viewModelScope
    )

    private val _activePassage = MutableStateFlow(PakistanLegalCorpus.cases.first())
    private val _dictationMode = MutableStateFlow(DictationMode.DRAFT_TTS)
    private val _activeTab = MutableStateFlow(PassageTab.TEXT)
    private val _customCitation = MutableStateFlow("Custom Draft • PLD Practice")
    private val _customCourt = MutableStateFlow("Appellate Stenography")
    private val _customText = MutableStateFlow(PakistanLegalCorpus.draftTemplates.first().second)
    private val _selectedCorpusFilter = MutableStateFlow("All")
    private val _corpusSearchQuery = MutableStateFlow("")
    private val _activeTooltipHighlight = MutableStateFlow<PassageHighlight?>(null)
    private val _inspectedSpec = MutableStateFlow<StenoStrokeSpec?>(null)
    private val _showProfileSheet = MutableStateFlow(false)
    private val _showBookmarksSheet = MutableStateFlow(false)
    private val _isExportingAudio = MutableStateFlow(false)
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    private val _liveDawnPassages = MutableStateFlow<List<LegalCasePassage>>(emptyList())
    private val _isFetchingDawn = MutableStateFlow(false)
    private val _selectedSymbolGroup = MutableStateFlow(SymbolExerciseGroup.ALL)
    private val _symbolSearchQuery = MutableStateFlow("")
    private val _symbolsSubMode = MutableStateFlow(SymbolsSubMode.WORKBOOK)
    private val _jsonLexiconSnapshot = MutableStateFlow(PitmanJsonDatabaseSnapshot())
    private val _jsonLookupQuery = MutableStateFlow("")
    private val _jsonDomainFilter = MutableStateFlow("All")

    init {
        viewModelScope.launch {
            runCatching { jsonLexiconRepo.loadDatabase() }
                .onSuccess { _jsonLexiconSnapshot.value = it }
        }
    }

    private val jsonLexiconSliceFlow = combine(
        _symbolsSubMode,
        _jsonLexiconSnapshot,
        _jsonLookupQuery,
        _jsonDomainFilter
    ) { mode, snapshot, query, domain ->
        JsonLexiconSlice(
            subMode = mode,
            snapshot = snapshot,
            query = query,
            domainFilter = domain
        )
    }

    private val _prefs = prefsRepo.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = StenoUserPreferences()
    )

    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<StenoUiState> = combine(
        _activePassage,
        _dictationMode,
        _activeTab,
        _customCitation,
        _customCourt,
        _customText,
        _selectedCorpusFilter,
        _corpusSearchQuery,
        _activeTooltipHighlight,
        _inspectedSpec,
        _showProfileSheet,
        _showBookmarksSheet,
        _isExportingAudio,
        _snackbarMessage,
        _prefs,
        ttsEngine.state,
        courtAudioPlayer.state,
        cadenceEngine.state,
        _liveDawnPassages,
        _isFetchingDawn,
        _selectedSymbolGroup,
        _symbolSearchQuery,
        jsonLexiconSliceFlow
    ) { values ->
        val passage = values[0] as LegalCasePassage
        val mode = values[1] as DictationMode
        val tab = values[2] as PassageTab
        val citation = values[3] as String
        val court = values[4] as String
        val draftText = values[5] as String
        val filter = values[6] as String
        val query = values[7] as String
        val tooltipHighlight = values[8] as PassageHighlight?
        val inspected = values[9] as StenoStrokeSpec?
        val profileOpen = values[10] as Boolean
        val bookmarksOpen = values[11] as Boolean
        val exporting = values[12] as Boolean
        val snackbar = values[13] as String?
        val prefs = values[14] as StenoUserPreferences
        val tts = values[15] as TtsState
        val audio = values[16] as CourtAudioState
        val cadence = values[17] as CadenceAcousticState
        val liveDawn = values[18] as List<LegalCasePassage>
        val fetchingDawn = values[19] as Boolean
        val symbolGroup = values[20] as SymbolExerciseGroup
        val symbolQuery = values[21] as String
        val lexiconSlice = values[22] as JsonLexiconSlice

        StenoUiState(
            activePassage = passage,
            corpusCases = PakistanLegalCorpus.cases,
            analysis = PitmanAnalyzer.analyze(passage.text),
            dictationMode = mode,
            activeTab = tab,
            customCitation = citation,
            customCourt = court,
            customText = draftText,
            selectedCorpusFilter = filter,
            corpusSearchQuery = query,
            activeTooltipHighlight = tooltipHighlight,
            inspectedSpec = inspected,
            showProfileSheet = profileOpen,
            showBookmarksSheet = bookmarksOpen,
            isExportingAudio = exporting,
            snackbarMessage = snackbar,
            prefs = prefs,
            ttsState = tts,
            audioState = audio,
            cadenceState = cadence,
            liveDawnPassages = liveDawn,
            isFetchingDawn = fetchingDawn,
            selectedSymbolGroup = symbolGroup,
            symbolSearchQuery = symbolQuery,
            symbolsSubMode = lexiconSlice.subMode,
            jsonLexiconSnapshot = lexiconSlice.snapshot,
            jsonLookupQuery = lexiconSlice.query,
            jsonDomainFilter = lexiconSlice.domainFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StenoUiState()
    )

    fun setDictationMode(mode: DictationMode) {
        _dictationMode.value = mode
        if (mode == DictationMode.COURT_AUDIO) {
            ttsEngine.pause()
            progressionJob?.cancel()
            cadenceEngine.setAmbientMaskingActive(false)
            viewModelScope.launch {
                prefsRepo.setAudioSource(AudioSourceType.AUDIO_UPLOAD)
            }
        } else if (_prefs.value.audioSource == AudioSourceType.AUDIO_UPLOAD) {
            viewModelScope.launch {
                prefsRepo.setAudioSource(AudioSourceType.SUPREME_COURT_ARCHIVE)
            }
        }
    }

    fun selectAudioSource(source: AudioSourceType) {
        viewModelScope.launch {
            prefsRepo.setAudioSource(source)
        }
        if (source == AudioSourceType.AUDIO_UPLOAD) {
            _dictationMode.value = DictationMode.COURT_AUDIO
            ttsEngine.pause()
            progressionJob?.cancel()
            cadenceEngine.setAmbientMaskingActive(false)
        } else {
            _dictationMode.value = DictationMode.DRAFT_TTS
        }
    }

    fun selectCadenceProfile(profile: VoiceCadenceProfile) {
        viewModelScope.launch {
            prefsRepo.setCadenceProfile(profile)
            if (ttsEngine.state.value.isSpeaking) {
                ttsEngine.pause()
                speakCurrentPassage(resumeFromStart = false, overrideWpm = _prefs.value.targetWpm)
            }
        }
    }

    fun setActiveTab(tab: PassageTab) {
        _activeTab.value = tab
        if (tab != PassageTab.TEXT) {
            _activeTooltipHighlight.value = null
        }
    }

    fun selectCorpusCase(casePassage: LegalCasePassage) {
        ttsEngine.stop()
        progressionJob?.cancel()
        cadenceEngine.setAmbientMaskingActive(false)
        _activeTooltipHighlight.value = null
        _activePassage.value = casePassage
        _activeTab.value = PassageTab.TEXT
        _dictationMode.value = DictationMode.DRAFT_TTS
    }

    fun updateCustomCitation(value: String) {
        _customCitation.value = value
    }

    fun updateCustomCourt(value: String) {
        _customCourt.value = value
    }

    fun updateCustomText(value: String) {
        _customText.value = value
    }

    fun appendVoiceTranscript(transcript: String) {
        val clean = transcript.trim()
        if (clean.isEmpty()) return
        val existing = _customText.value.trim()
        _customText.value = if (existing.isEmpty()) clean else "$existing $clean"
        applyCustomDraftToStudio(startSpeaking = false)
        showMessage("Voice dictation appended to passage")
    }

    fun applyDraftTemplate(title: String, body: String) {
        _customCitation.value = "Draft • $title"
        _customText.value = body
    }

    fun applyCustomDraftToStudio(startSpeaking: Boolean = false) {
        val text = _customText.value.trim()
        if (text.isEmpty()) {
            showMessage("Enter or dictate passage text first")
            return
        }
        ttsEngine.stop()
        progressionJob?.cancel()
        cadenceEngine.setAmbientMaskingActive(false)
        val citation = _customCitation.value.trim().ifEmpty { "Custom Passage Draft" }
        val court = _customCourt.value.trim().ifEmpty { "User Dictation Studio" }
        val stableId = "custom_${(citation + text).hashCode()}"
        val customPassage = LegalCasePassage(
            id = stableId,
            citation = citation,
            court = court,
            category = "Custom Draft",
            subtitle = "User-Authored Legal Passage • Live Pitman Steno Outline Calibration",
            isVerified = false,
            text = text,
            summary = "Custom judicial stenography draft containing ${PitmanAnalyzer.analyze(text).wordCount} words calibrated for Pitman outline recognition and high-speed WPM pacing.",
            keyPrinciples = listOf(
                "Live automated detection of Pitman grammalogues, legal contractions, and intersections.",
                "Instant WPM timing calibration for 60, 80, 100, 120, 140, and 160 words per minute."
            ),
            isCustom = true
        )
        _activePassage.value = customPassage
        _activeTab.value = PassageTab.TEXT
        _dictationMode.value = DictationMode.DRAFT_TTS
        if (startSpeaking) {
            speakCurrentPassage(resumeFromStart = true, overrideWpm = _prefs.value.targetWpm)
        }
    }

    fun toggleTtsPlayPause() {
        val currentTts = ttsEngine.state.value
        if (currentTts.isSpeaking) {
            ttsEngine.pause()
            progressionJob?.cancel()
            cadenceEngine.setAmbientMaskingActive(false)
        } else {
            speakCurrentPassage(resumeFromStart = false, overrideWpm = _prefs.value.targetWpm)
        }
    }

    fun generateAndStartDictationExercise() {
        if (_dictationMode.value == DictationMode.COURT_AUDIO && courtAudioPlayer.state.value.isLoaded) {
            courtAudioPlayer.seekTo(0)
            if (!courtAudioPlayer.state.value.isPlaying) {
                courtAudioPlayer.togglePlayPause()
            }
            showMessage("Started courtroom audio exercise @ ${_prefs.value.targetWpm} WPM")
        } else {
            _dictationMode.value = DictationMode.DRAFT_TTS
            speakCurrentPassage(resumeFromStart = true, overrideWpm = _prefs.value.targetWpm)
            if (_prefs.value.quarterMinuteChime) {
                cadenceEngine.playQuarterMinuteChime()
            }
            showMessage("Dictation exercise started @ ${_prefs.value.targetWpm} WPM")
        }
    }

    fun restartTts() {
        speakCurrentPassage(resumeFromStart = true, overrideWpm = _prefs.value.targetWpm)
    }

    fun seekDictationBySeconds(deltaSeconds: Int) {
        if (_dictationMode.value == DictationMode.COURT_AUDIO && courtAudioPlayer.state.value.isLoaded) {
            val current = courtAudioPlayer.state.value.positionMs
            courtAudioPlayer.seekTo(current + deltaSeconds * 1000)
        } else {
            val prefs = _prefs.value
            ttsEngine.seekBySeconds(
                text = _activePassage.value.text,
                deltaSeconds = deltaSeconds,
                wpm = prefs.targetWpm,
                pitch = prefs.ttsPitch,
                profile = prefs.cadenceProfile,
                pitchPreserve = prefs.pitchPreserve
            )
        }
    }

    fun toggleUnifiedPlayerPlayPause() {
        if (_dictationMode.value == DictationMode.COURT_AUDIO && courtAudioPlayer.state.value.isLoaded) {
            courtAudioPlayer.togglePlayPause()
        } else {
            toggleTtsPlayPause()
        }
    }

    private fun speakCurrentPassage(resumeFromStart: Boolean, overrideWpm: Int) {
        val prefs = _prefs.value
        ttsEngine.speakPassage(
            text = _activePassage.value.text,
            wpm = overrideWpm,
            pitch = prefs.ttsPitch,
            profile = prefs.cadenceProfile,
            pitchPreserve = prefs.pitchPreserve,
            resumeFromStart = resumeFromStart
        )
        if (prefs.courtroomAmbientMasking) {
            cadenceEngine.setAmbientMaskingActive(true)
        }
        startSpeedProgressionLadderIfEnabled()
    }

    private fun startSpeedProgressionLadderIfEnabled() {
        progressionJob?.cancel()
        if (!_prefs.value.speedProgressionLadder) return
        progressionJob = viewModelScope.launch {
            while (isActive) {
                delay(60_000L)
                if (!ttsEngine.state.value.isSpeaking || !_prefs.value.speedProgressionLadder) break
                val nextWpm = (_prefs.value.targetWpm + 10).coerceAtMost(180)
                if (nextWpm != _prefs.value.targetWpm) {
                    prefsRepo.setTargetWpm(nextWpm)
                    ttsEngine.pause()
                    val prefs = _prefs.value
                    ttsEngine.speakPassage(
                        text = _activePassage.value.text,
                        wpm = nextWpm,
                        pitch = prefs.ttsPitch,
                        profile = prefs.cadenceProfile,
                        pitchPreserve = prefs.pitchPreserve,
                        resumeFromStart = false
                    )
                    showMessage("Pyramid Ladder: stepped up to $nextWpm WPM")
                }
            }
        }
    }

    fun toggleFiveSecondCadenceTest() {
        cadenceEngine.toggleFiveSecondCadenceTest(_prefs.value.targetWpm)
    }

    fun stopTts() {
        ttsEngine.stop()
        progressionJob?.cancel()
        cadenceEngine.setAmbientMaskingActive(false)
    }

    fun pronounceStem(spec: StenoStrokeSpec) {
        _inspectedSpec.value = spec
        ttsEngine.speakSingleTerm(spec.term, _prefs.value.ttsPitch)
    }

    fun pronounceTermOnly(term: String) {
        ttsEngine.speakSingleTerm(term, _prefs.value.ttsPitch)
    }

    fun openPassageTooltip(highlight: PassageHighlight) {
        val current = _activeTooltipHighlight.value
        _activeTooltipHighlight.value = if (
            current != null &&
            current.startIndex == highlight.startIndex &&
            current.endIndex == highlight.endIndex
        ) {
            null
        } else {
            highlight
        }
    }

    fun dismissPassageTooltip() {
        _activeTooltipHighlight.value = null
    }

    fun expandTooltipToInspector() {
        val spec = _activeTooltipHighlight.value?.spec ?: return
        _activeTooltipHighlight.value = null
        _inspectedSpec.value = spec
    }

    fun dismissInspector() {
        _inspectedSpec.value = null
    }

    fun inspectSpec(spec: StenoStrokeSpec) {
        _inspectedSpec.value = spec
    }

    fun setSelectedSymbolGroup(group: SymbolExerciseGroup) {
        _selectedSymbolGroup.value = group
    }

    fun setSymbolSearchQuery(query: String) {
        _symbolSearchQuery.value = query
    }

    fun setSymbolsSubMode(mode: SymbolsSubMode) {
        _symbolsSubMode.value = mode
    }

    fun setJsonLookupQuery(query: String) {
        _jsonLookupQuery.value = query
    }

    fun setJsonDomainFilter(domain: String) {
        _jsonDomainFilter.value = domain
    }

    fun addJsonLexiconEntry(
        term: String,
        strokeFormula: String,
        position: StenoPosition,
        category: StenoCategory,
        courtDomain: String,
        theoryPrinciple: String,
        ruleNote: String,
        legalDefinition: String,
        glyphKey: String
    ) {
        viewModelScope.launch {
            runCatching {
                jsonLexiconRepo.addCustomEntry(
                    term = term,
                    strokeFormula = strokeFormula,
                    position = position,
                    category = category,
                    courtDomain = courtDomain,
                    theoryPrinciple = theoryPrinciple,
                    ruleNote = ruleNote,
                    legalDefinition = legalDefinition,
                    glyphKey = glyphKey
                )
            }.onSuccess { updated ->
                _jsonLexiconSnapshot.value = updated
                showMessage("Saved \"$term\" to local Pitman JSON DB")
            }.onFailure {
                showMessage("Unable to write to local JSON database")
            }
        }
    }

    fun deleteJsonLexiconEntry(entryId: String) {
        viewModelScope.launch {
            runCatching { jsonLexiconRepo.deleteEntry(entryId) }
                .onSuccess { updated ->
                    _jsonLexiconSnapshot.value = updated
                    showMessage("Removed custom outline from JSON DB")
                }
        }
    }

    fun resetJsonLexiconToSeed() {
        viewModelScope.launch {
            runCatching { jsonLexiconRepo.resetToDefaultSeed() }
                .onSuccess { updated ->
                    _jsonLexiconSnapshot.value = updated
                    showMessage("Restored default Legal JSON Lexicon")
                }
        }
    }

    fun fetchDawnFeed(opinionOnly: Boolean) {
        if (_isFetchingDawn.value) return
        val feedType = if (opinionOnly) DawnFeedType.OPINION else DawnFeedType.NEWS
        _isFetchingDawn.value = true
        viewModelScope.launch {
            val result = DawnRssFetcher.fetchFeed(feedType)
            _isFetchingDawn.value = false
            result.onSuccess { fetched ->
                val merged = (fetched + _liveDawnPassages.value).distinctBy { it.id }
                _liveDawnPassages.value = merged
                _selectedCorpusFilter.value = feedType.category
                showMessage("Loaded ${fetched.size} live ${feedType.category} articles")
            }.onFailure { err ->
                showMessage("Offline or Dawn RSS unavailable (${err.message ?: "network error"}). Showing curated Dawn archive.")
                _selectedCorpusFilter.value = feedType.category
            }
        }
    }

    fun setProfileSheetVisible(visible: Boolean) {
        _showProfileSheet.value = visible
    }

    fun setBookmarksSheetVisible(visible: Boolean) {
        _showBookmarksSheet.value = visible
    }

    fun toggleBookmark(passage: LegalCasePassage) {
        viewModelScope.launch {
            val added = prefsRepo.toggleBookmark(passage)
            showMessage(
                if (added) "Saved \"${passage.citation}\" to Legal Library"
                else "Removed \"${passage.citation}\" from Legal Library"
            )
        }
    }

    fun removeBookmark(caseId: String) {
        viewModelScope.launch {
            prefsRepo.removeBookmark(caseId)
            showMessage("Removed from Legal Library")
        }
    }

    fun setTargetWpm(wpm: Int) {
        val clamped = wpm.coerceIn(60, 180)
        viewModelScope.launch {
            prefsRepo.setTargetWpm(clamped)
            if (ttsEngine.state.value.isSpeaking) {
                ttsEngine.pause()
                speakCurrentPassage(resumeFromStart = false, overrideWpm = clamped)
            }
            if (_dictationMode.value == DictationMode.COURT_AUDIO && courtAudioPlayer.state.value.isLoaded) {
                courtAudioPlayer.setSpeed((clamped / 100f).coerceIn(0.65f, 1.75f))
            }
        }
    }

    fun setKaraokeSync(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setKaraokeSync(enabled)
        }
    }

    fun setTtsPitch(pitch: Float) {
        viewModelScope.launch {
            prefsRepo.setTtsPitch(pitch)
        }
    }

    fun setQuarterMinuteChime(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setQuarterMinuteChime(enabled)
        }
    }

    fun setCourtroomAmbientMasking(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setCourtroomAmbientMasking(enabled)
            if (ttsEngine.state.value.isSpeaking) {
                cadenceEngine.setAmbientMaskingActive(enabled)
            } else if (!enabled) {
                cadenceEngine.setAmbientMaskingActive(false)
            }
        }
    }

    fun setSpeedProgressionLadder(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setSpeedProgressionLadder(enabled)
            if (!enabled) {
                progressionJob?.cancel()
            } else if (ttsEngine.state.value.isSpeaking) {
                startSpeedProgressionLadderIfEnabled()
            }
        }
    }

    fun togglePitchPreserve() {
        viewModelScope.launch {
            val next = !_prefs.value.pitchPreserve
            prefsRepo.setPitchPreserve(next)
            showMessage(if (next) "Pitch Preserve enabled" else "Natural vocal pitch enabled")
        }
    }

    fun setThemeAppearance(appearance: ThemeAppearance) {
        viewModelScope.launch {
            prefsRepo.setThemeAppearance(appearance)
        }
    }

    fun setHighContrastMode(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setHighContrastMode(enabled)
        }
    }

    fun exportActivePassagePdf() {
        val ctx = getApplication<Application>()
        val result = StenoPdfExporter.exportAndShare(
            context = ctx,
            passage = _activePassage.value,
            analysis = PitmanAnalyzer.analyze(_activePassage.value.text),
            targetWpm = _prefs.value.targetWpm
        )
        result.onSuccess { file ->
            showMessage("Exported ${file.name}")
        }.onFailure {
            showMessage("Unable to export Steno PDF on this device")
        }
    }

    fun setCorpusFilter(filter: String) {
        _selectedCorpusFilter.value = filter
    }

    fun setCorpusSearchQuery(query: String) {
        _corpusSearchQuery.value = query
    }

    fun loadCourtAudioUri(uri: Uri) {
        val ctx = getApplication<Application>()
        var fileName = "Courtroom Audio Recording"
        ctx.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIdx >= 0 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIdx) ?: fileName
            }
        }
        courtAudioPlayer.loadUri(uri, fileName)
        _dictationMode.value = DictationMode.COURT_AUDIO
        viewModelScope.launch {
            prefsRepo.setAudioSource(AudioSourceType.AUDIO_UPLOAD)
        }
    }

    fun synthesizeJudgmentToCourtAudio() {
        val ctx = getApplication<Application>()
        val passage = _activePassage.value
        val outFile = File(ctx.cacheDir, "court_dictation_${passage.id}.wav")
        _isExportingAudio.value = true
        ttsEngine.synthesizeToWav(
            text = passage.text,
            wpm = _prefs.value.targetWpm,
            pitch = _prefs.value.ttsPitch,
            outputFile = outFile
        ) { success ->
            _isExportingAudio.value = false
            if (success) {
                courtAudioPlayer.loadFile(
                    file = outFile,
                    label = "${passage.citation} (${_prefs.value.targetWpm} WPM).wav"
                )
                showMessage("Generated court audio track ready to play")
            } else {
                showMessage("Device TTS could not export WAV audio")
            }
        }
    }

    fun toggleCourtAudioPlayPause() {
        courtAudioPlayer.togglePlayPause()
    }

    fun seekCourtAudio(positionMs: Int) {
        courtAudioPlayer.seekTo(positionMs)
    }

    fun setCourtAudioSpeed(speed: Float) {
        courtAudioPlayer.setSpeed(speed)
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        progressionJob?.cancel()
        cadenceEngine.release()
        ttsEngine.release()
        courtAudioPlayer.release()
    }
}

package com.example.audio

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.domain.model.VoiceCadenceProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

data class TtsState(
    val isReady: Boolean = false,
    val isUnavailable: Boolean = false,
    val isSpeaking: Boolean = false,
    val isPaused: Boolean = false,
    val activeRange: IntRange? = null,
    val currentWordIndex: Int = 0,
    val totalWords: Int = 0,
    val progressFraction: Float = 0f,
    val statusMessage: String? = null
)

class DictationTtsEngine(
    context: Context,
    private val scope: CoroutineScope,
    private val onWordMilestone: (wordIndex: Int) -> Unit = {},
    private val onFinishedPassage: (Int) -> Unit
) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var currentText: String = ""
    private var currentWordRanges: List<IntRange> = emptyList()
    private var currentResumeOffset: Int = 0
    private var currentWpm: Int = 100
    private var currentPitch: Float = 1.0f
    private var currentProfile: VoiceCadenceProfile = VoiceCadenceProfile.COMMONWEALTH_PAKISTAN
    private var lastSpokenCharIndex: Int = 0
    private var lastReportedMilestoneWord: Int = 0
    private var rangeCallbackSeen: Boolean = false
    private var fallbackJob: Job? = null

    private val _state = MutableStateFlow(TtsState())
    val state: StateFlow<TtsState> = _state.asStateFlow()

    init {
        tts = try {
            TextToSpeech(appContext, this)
        } catch (_: Throwable) {
            _state.value = _state.value.copy(
                isReady = false,
                isUnavailable = true,
                statusMessage = null
            )
            null
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val engine = tts ?: return
                applyLocaleForProfile(engine, currentProfile)
                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        if (utteranceId?.startsWith("PASSAGE_") == true) {
                            _state.value = _state.value.copy(
                                isSpeaking = true,
                                isPaused = false,
                                statusMessage = null
                            )
                        }
                    }

                    override fun onRangeStart(
                        utteranceId: String?,
                        start: Int,
                        end: Int,
                        frame: Int
                    ) {
                        if (utteranceId?.startsWith("PASSAGE_") == true) {
                            rangeCallbackSeen = true
                            fallbackJob?.cancel()
                            val absStart = (currentResumeOffset + start).coerceIn(0, currentText.length)
                            val absEnd = (currentResumeOffset + end).coerceIn(absStart, currentText.length)
                            updateProgressForCharIndex(absStart, absEnd)
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        if (utteranceId?.startsWith("PASSAGE_") == true) {
                            fallbackJob?.cancel()
                            val words = currentText.trim().split(Regex("\\s+")).count { it.isNotBlank() }
                            _state.value = _state.value.copy(
                                isSpeaking = false,
                                isPaused = false,
                                activeRange = null,
                                currentWordIndex = words,
                                progressFraction = if (words > 0) 1f else 0f
                            )
                            currentResumeOffset = 0
                            lastSpokenCharIndex = 0
                            lastReportedMilestoneWord = 0
                            scope.launch(Dispatchers.Main) {
                                onFinishedPassage(words)
                            }
                        } else if (utteranceId?.startsWith("EXPORT_") == true) {
                            _state.value = _state.value.copy(statusMessage = "Audio exported")
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        handleTtsError(utteranceId)
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        handleTtsError(utteranceId)
                    }
                })
                _state.value = _state.value.copy(isReady = true, isUnavailable = false)
            } catch (_: Throwable) {
                _state.value = _state.value.copy(
                    isReady = false,
                    isUnavailable = true,
                    statusMessage = null
                )
            }
        } else {
            _state.value = _state.value.copy(
                isReady = false,
                isUnavailable = true,
                statusMessage = null
            )
        }
    }

    private fun applyLocaleForProfile(engine: TextToSpeech, profile: VoiceCadenceProfile) {
        val targetLocale = when (profile) {
            VoiceCadenceProfile.COMMONWEALTH_PAKISTAN -> Locale.UK
            VoiceCadenceProfile.US_COURT_STENOMASK -> Locale.US
            VoiceCadenceProfile.NEUTRAL_PACER -> Locale.UK
        }
        val res = engine.setLanguage(targetLocale)
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            engine.setLanguage(Locale.US)
        }
    }

    private fun updateProgressForCharIndex(absStart: Int, absEnd: Int) {
        lastSpokenCharIndex = absStart
        val total = currentWordRanges.size
        val wordIdx = currentWordRanges.indexOfFirst { absStart in it }.let { idx ->
            if (idx >= 0) idx + 1 else currentWordRanges.count { it.first <= absStart }
        }.coerceIn(0, total)
        val fraction = if (total > 0) (wordIdx.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f

        _state.value = _state.value.copy(
            activeRange = absStart until absEnd,
            currentWordIndex = wordIdx,
            totalWords = total,
            progressFraction = fraction
        )

        if (wordIdx > 0 && wordIdx != lastReportedMilestoneWord) {
            lastReportedMilestoneWord = wordIdx
            scope.launch(Dispatchers.Main) {
                onWordMilestone(wordIdx)
            }
        }
    }

    private fun handleTtsError(utteranceId: String?) {
        fallbackJob?.cancel()
        if (utteranceId?.startsWith("PASSAGE_") == true) {
            _state.value = _state.value.copy(
                isSpeaking = false,
                isPaused = false,
                activeRange = null,
                statusMessage = "Dictation playback interrupted"
            )
        }
    }

    fun speakPassage(
        text: String,
        wpm: Int,
        pitch: Float,
        profile: VoiceCadenceProfile = VoiceCadenceProfile.COMMONWEALTH_PAKISTAN,
        pitchPreserve: Boolean = true,
        resumeFromStart: Boolean = false
    ) {
        val engine = tts
        val clean = text.trim()
        if (clean.isEmpty()) return

        currentText = clean
        currentWordRanges = Regex("\\S+").findAll(clean).map { it.range }.toList()
        currentWpm = wpm
        currentPitch = if (pitchPreserve) 1.0f else pitch
        currentProfile = profile

        if (engine == null || !_state.value.isReady) {
            _state.value = _state.value.copy(
                totalWords = currentWordRanges.size,
                statusMessage = "Speech synthesizer not ready on this device"
            )
            return
        }

        val offset = if (resumeFromStart || (!_state.value.isPaused && lastSpokenCharIndex == 0)) {
            lastReportedMilestoneWord = 0
            0
        } else {
            lastSpokenCharIndex.coerceIn(0, clean.length)
        }
        currentResumeOffset = offset
        val slice = clean.substring(offset)
        if (slice.isBlank()) {
            currentResumeOffset = 0
            lastSpokenCharIndex = 0
            lastReportedMilestoneWord = 0
            return
        }

        // Baseline ~135 WPM = 1.0x speechRate; adjust slightly for Neutral Strict Pacer
        val baseline = if (profile == VoiceCadenceProfile.NEUTRAL_PACER) 132f else 135f
        val rate = (wpm / baseline).coerceIn(0.45f, 1.85f)
        try {
            applyLocaleForProfile(engine, profile)
            engine.setSpeechRate(rate)
            engine.setPitch(currentPitch)

            rangeCallbackSeen = false
            val utteranceId = "PASSAGE_${UUID.randomUUID()}"
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }
            engine.speak(slice, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            val startWordIdx = currentWordRanges.count { it.first < offset }
            _state.value = _state.value.copy(
                isSpeaking = true,
                isPaused = false,
                totalWords = currentWordRanges.size,
                currentWordIndex = startWordIdx,
                progressFraction = if (currentWordRanges.isNotEmpty()) {
                    startWordIdx.toFloat() / currentWordRanges.size
                } else 0f,
                statusMessage = null
            )
            startFallbackKaraokeTicker(slice, offset, wpm)
        } catch (_: Throwable) {
            _state.value = _state.value.copy(
                isSpeaking = false,
                isPaused = false,
                statusMessage = "Speech synthesizer unavailable on this device"
            )
        }
    }

    fun seekBySeconds(
        text: String,
        deltaSeconds: Int,
        wpm: Int,
        pitch: Float,
        profile: VoiceCadenceProfile,
        pitchPreserve: Boolean
    ) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        if (currentText != clean || currentWordRanges.isEmpty()) {
            currentText = clean
            currentWordRanges = Regex("\\S+").findAll(clean).map { it.range }.toList()
        }
        if (currentWordRanges.isEmpty()) return

        val wordsDelta = ((deltaSeconds * wpm) / 60.0).roundToInt()
        val currentIdx = _state.value.currentWordIndex.coerceIn(0, currentWordRanges.lastIndex)
        val targetWordIdx = (currentIdx + wordsDelta).coerceIn(0, currentWordRanges.lastIndex)
        val targetRange = currentWordRanges[targetWordIdx]
        val wasSpeaking = _state.value.isSpeaking

        fallbackJob?.cancel()
        try {
            tts?.stop()
        } catch (_: Throwable) {
        }

        lastSpokenCharIndex = targetRange.first
        currentResumeOffset = targetRange.first
        _state.value = _state.value.copy(
            currentWordIndex = targetWordIdx,
            totalWords = currentWordRanges.size,
            progressFraction = targetWordIdx.toFloat() / currentWordRanges.size,
            activeRange = targetRange,
            isPaused = !wasSpeaking
        )

        if (wasSpeaking) {
            speakPassage(
                text = clean,
                wpm = wpm,
                pitch = pitch,
                profile = profile,
                pitchPreserve = pitchPreserve,
                resumeFromStart = false
            )
        }
    }

    private fun startFallbackKaraokeTicker(slice: String, baseOffset: Int, wpm: Int) {
        fallbackJob?.cancel()
        val matches = Regex("\\S+").findAll(slice).toList()
        if (matches.isEmpty()) return
        val msPerWord = ((60_000.0 / wpm.coerceAtLeast(40))).toLong()

        fallbackJob = scope.launch {
            delay(300L)
            if (rangeCallbackSeen) return@launch
            for (match in matches) {
                if (rangeCallbackSeen || !_state.value.isSpeaking) break
                val start = baseOffset + match.range.first
                val end = baseOffset + match.range.last + 1
                updateProgressForCharIndex(start, end)
                delay(msPerWord)
            }
        }
    }

    fun pause() {
        fallbackJob?.cancel()
        try {
            tts?.stop()
        } catch (_: Throwable) {
        }
        if (_state.value.isSpeaking) {
            _state.value = _state.value.copy(
                isSpeaking = false,
                isPaused = true
            )
        }
    }

    fun stop() {
        fallbackJob?.cancel()
        try {
            tts?.stop()
        } catch (_: Throwable) {
        }
        currentResumeOffset = 0
        lastSpokenCharIndex = 0
        lastReportedMilestoneWord = 0
        _state.value = _state.value.copy(
            isSpeaking = false,
            isPaused = false,
            activeRange = null,
            currentWordIndex = 0,
            progressFraction = 0f
        )
    }

    fun speakSingleTerm(term: String, pitch: Float = 1.0f) {
        val engine = tts ?: return
        if (!_state.value.isReady) return
        fallbackJob?.cancel()
        try {
            engine.setSpeechRate(0.85f)
            engine.setPitch(pitch)
            val utteranceId = "TERM_${UUID.randomUUID()}"
            engine.speak(term, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (_: Throwable) {
        }
    }

    fun synthesizeToWav(
        text: String,
        wpm: Int,
        pitch: Float,
        outputFile: File,
        onComplete: (Boolean) -> Unit
    ) {
        val engine = tts
        if (engine == null || !_state.value.isReady || text.isBlank()) {
            onComplete(false)
            return
        }
        val result = try {
            val rate = (wpm / 135f).coerceIn(0.45f, 1.85f)
            engine.setSpeechRate(rate)
            engine.setPitch(pitch)
            val utteranceId = "EXPORT_${UUID.randomUUID()}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                engine.synthesizeToFile(text, Bundle(), outputFile, utteranceId)
            } else {
                @Suppress("DEPRECATION")
                TextToSpeech.ERROR
            }
        } catch (_: Throwable) {
            TextToSpeech.ERROR
        }

        if (result == TextToSpeech.SUCCESS) {
            scope.launch {
                repeat(40) {
                    delay(200)
                    if (outputFile.exists() && outputFile.length() > 44) {
                        onComplete(true)
                        return@launch
                    }
                }
                onComplete(outputFile.exists() && outputFile.length() > 44)
            }
        } else {
            onComplete(false)
        }
    }

    fun release() {
        fallbackJob?.cancel()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Throwable) {
        }
        tts = null
    }
}

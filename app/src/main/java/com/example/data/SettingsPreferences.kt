package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AudioSourceType
import com.example.domain.model.LegalCasePassage
import com.example.domain.model.VoiceCadenceProfile
import com.example.ui.theme.ThemeAppearance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.stenoDataStore: DataStore<Preferences> by preferencesDataStore(name = "pitman_steno_prefs")

data class StenoUserPreferences(
    val targetWpm: Int = 100,
    val karaokeSync: Boolean = true,
    val ttsPitch: Float = 1.0f,
    val totalWordsDictated: Int = 0,
    val bookmarkedIds: Set<String> = emptySet(),
    val customBookmarks: List<LegalCasePassage> = emptyList(),
    val audioSource: AudioSourceType = AudioSourceType.SUPREME_COURT_ARCHIVE,
    val cadenceProfile: VoiceCadenceProfile = VoiceCadenceProfile.COMMONWEALTH_PAKISTAN,
    val quarterMinuteChime: Boolean = true,
    val courtroomAmbientMasking: Boolean = false,
    val speedProgressionLadder: Boolean = true,
    val pitchPreserve: Boolean = true,
    val themeAppearance: ThemeAppearance = ThemeAppearance.SYSTEM,
    val highContrastMode: Boolean = false
)

class SettingsPreferences(private val context: Context) {

    private object Keys {
        val TARGET_WPM = intPreferencesKey("target_wpm")
        val KARAOKE_SYNC = booleanPreferencesKey("karaoke_sync")
        val TTS_PITCH = floatPreferencesKey("tts_pitch")
        val TOTAL_WORDS_DICTATED = intPreferencesKey("total_words_dictated")
        val BOOKMARKED_IDS = stringSetPreferencesKey("bookmarked_case_ids")
        val CUSTOM_BOOKMARKS_JSON = stringPreferencesKey("custom_bookmarks_json")
        val AUDIO_SOURCE = stringPreferencesKey("audio_source")
        val CADENCE_PROFILE = stringPreferencesKey("cadence_profile")
        val QUARTER_MINUTE_CHIME = booleanPreferencesKey("quarter_minute_chime")
        val COURTROOM_AMBIENT_MASKING = booleanPreferencesKey("courtroom_ambient_masking")
        val SPEED_PROGRESSION_LADDER = booleanPreferencesKey("speed_progression_ladder")
        val PITCH_PRESERVE = booleanPreferencesKey("pitch_preserve")
        val THEME_APPEARANCE = stringPreferencesKey("theme_appearance")
        val HIGH_CONTRAST_MODE = booleanPreferencesKey("high_contrast_mode")
    }

    val preferencesFlow: Flow<StenoUserPreferences> = context.stenoDataStore.data.map { prefs ->
        val customJson = prefs[Keys.CUSTOM_BOOKMARKS_JSON].orEmpty()
        val sourceName = prefs[Keys.AUDIO_SOURCE]
        val cadenceName = prefs[Keys.CADENCE_PROFILE]
        val themeName = prefs[Keys.THEME_APPEARANCE]
        StenoUserPreferences(
            targetWpm = (prefs[Keys.TARGET_WPM] ?: 100).coerceIn(60, 180),
            karaokeSync = prefs[Keys.KARAOKE_SYNC] ?: true,
            ttsPitch = prefs[Keys.TTS_PITCH] ?: 1.0f,
            totalWordsDictated = prefs[Keys.TOTAL_WORDS_DICTATED] ?: 0,
            bookmarkedIds = prefs[Keys.BOOKMARKED_IDS] ?: emptySet(),
            customBookmarks = decodeCustomBookmarks(customJson),
            audioSource = AudioSourceType.entries.firstOrNull { it.name == sourceName }
                ?: AudioSourceType.SUPREME_COURT_ARCHIVE,
            cadenceProfile = VoiceCadenceProfile.entries.firstOrNull { it.name == cadenceName }
                ?: VoiceCadenceProfile.COMMONWEALTH_PAKISTAN,
            quarterMinuteChime = prefs[Keys.QUARTER_MINUTE_CHIME] ?: true,
            courtroomAmbientMasking = prefs[Keys.COURTROOM_AMBIENT_MASKING] ?: false,
            speedProgressionLadder = prefs[Keys.SPEED_PROGRESSION_LADDER] ?: true,
            pitchPreserve = prefs[Keys.PITCH_PRESERVE] ?: true,
            themeAppearance = ThemeAppearance.entries.firstOrNull { it.name == themeName }
                ?: ThemeAppearance.SYSTEM,
            highContrastMode = prefs[Keys.HIGH_CONTRAST_MODE] ?: false
        )
    }

    suspend fun setTargetWpm(wpm: Int) {
        context.stenoDataStore.edit { it[Keys.TARGET_WPM] = wpm.coerceIn(60, 180) }
    }

    suspend fun setKaraokeSync(enabled: Boolean) {
        context.stenoDataStore.edit { it[Keys.KARAOKE_SYNC] = enabled }
    }

    suspend fun setTtsPitch(pitch: Float) {
        context.stenoDataStore.edit { it[Keys.TTS_PITCH] = pitch.coerceIn(0.75f, 1.3f) }
    }

    suspend fun setAudioSource(source: AudioSourceType) {
        context.stenoDataStore.edit { it[Keys.AUDIO_SOURCE] = source.name }
    }

    suspend fun setCadenceProfile(profile: VoiceCadenceProfile) {
        context.stenoDataStore.edit { it[Keys.CADENCE_PROFILE] = profile.name }
    }

    suspend fun setQuarterMinuteChime(enabled: Boolean) {
        context.stenoDataStore.edit { it[Keys.QUARTER_MINUTE_CHIME] = enabled }
    }

    suspend fun setCourtroomAmbientMasking(enabled: Boolean) {
        context.stenoDataStore.edit { it[Keys.COURTROOM_AMBIENT_MASKING] = enabled }
    }

    suspend fun setSpeedProgressionLadder(enabled: Boolean) {
        context.stenoDataStore.edit { it[Keys.SPEED_PROGRESSION_LADDER] = enabled }
    }

    suspend fun setPitchPreserve(enabled: Boolean) {
        context.stenoDataStore.edit { it[Keys.PITCH_PRESERVE] = enabled }
    }

    suspend fun setThemeAppearance(appearance: ThemeAppearance) {
        context.stenoDataStore.edit { it[Keys.THEME_APPEARANCE] = appearance.name }
    }

    suspend fun setHighContrastMode(enabled: Boolean) {
        context.stenoDataStore.edit { it[Keys.HIGH_CONTRAST_MODE] = enabled }
    }

    suspend fun addDictatedWords(count: Int) {
        if (count <= 0) return
        context.stenoDataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_WORDS_DICTATED] ?: 0
            prefs[Keys.TOTAL_WORDS_DICTATED] = current + count
        }
    }

    suspend fun toggleBookmark(passage: LegalCasePassage): Boolean {
        var added = false
        context.stenoDataStore.edit { prefs ->
            val currentIds = prefs[Keys.BOOKMARKED_IDS]?.toMutableSet() ?: mutableSetOf()
            val currentCustom = decodeCustomBookmarks(prefs[Keys.CUSTOM_BOOKMARKS_JSON].orEmpty()).toMutableList()

            if (passage.id in currentIds) {
                currentIds.remove(passage.id)
                currentCustom.removeAll { it.id == passage.id }
                added = false
            } else {
                currentIds.add(passage.id)
                if (passage.isCustom && currentCustom.none { it.id == passage.id }) {
                    currentCustom.add(0, passage)
                }
                added = true
            }
            prefs[Keys.BOOKMARKED_IDS] = currentIds
            prefs[Keys.CUSTOM_BOOKMARKS_JSON] = encodeCustomBookmarks(currentCustom)
        }
        return added
    }

    suspend fun removeBookmark(caseId: String) {
        context.stenoDataStore.edit { prefs ->
            val currentIds = prefs[Keys.BOOKMARKED_IDS]?.toMutableSet() ?: mutableSetOf()
            currentIds.remove(caseId)
            val currentCustom = decodeCustomBookmarks(prefs[Keys.CUSTOM_BOOKMARKS_JSON].orEmpty())
                .filterNot { it.id == caseId }
            prefs[Keys.BOOKMARKED_IDS] = currentIds
            prefs[Keys.CUSTOM_BOOKMARKS_JSON] = encodeCustomBookmarks(currentCustom)
        }
    }

    private fun encodeCustomBookmarks(list: List<LegalCasePassage>): String {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("citation", item.citation)
                put("court", item.court)
                put("category", item.category)
                put("subtitle", item.subtitle)
                put("text", item.text)
                put("summary", item.summary)
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun decodeCustomBookmarks(raw: String): List<LegalCasePassage> {
        if (raw.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        LegalCasePassage(
                            id = obj.getString("id"),
                            citation = obj.getString("citation"),
                            court = obj.getString("court"),
                            category = obj.optString("category", "Custom Draft"),
                            subtitle = obj.optString("subtitle", "Saved Custom Legal Draft"),
                            isVerified = false,
                            text = obj.getString("text"),
                            summary = obj.optString("summary", ""),
                            keyPrinciples = listOf(
                                "Saved local custom draft for repeated Pitman stenography practice."
                            ),
                            isCustom = true
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

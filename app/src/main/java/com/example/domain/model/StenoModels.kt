package com.example.domain.model

enum class StenoPosition(val badge: String, val ruleDescription: String) {
    FIRST("1st Pos", "Written above the stenographic line (first-place vowel)"),
    SECOND("2nd Pos", "Written resting on the stenographic line (second-place vowel)"),
    THIRD("3rd Pos", "Written through or below the stenographic line (third-place vowel)")
}

enum class StenoCategory(val label: String) {
    INTERSECTION("Intersection"),
    CONTRACTION("Contraction"),
    PHRASING("Phrasing"),
    GRAMMALOGUE("Grammalogue"),
    HALVING("Halving / Doubling")
}

data class StenoStrokeSpec(
    val term: String,
    val strokeFormula: String,
    val position: StenoPosition,
    val category: StenoCategory,
    val theoryPrinciple: String,
    val ruleNote: String,
    val legalDefinition: String,
    val glyphKey: String
)

data class PassageHighlight(
    val startIndex: Int,
    val endIndex: Int,
    val matchedText: String,
    val spec: StenoStrokeSpec
)

data class StenoAnalysis(
    val wordCount: Int,
    val charCount: Int,
    val secondsAt80Wpm: Int,
    val secondsAt100Wpm: Int,
    val secondsAt120Wpm: Int,
    val secondsAt140Wpm: Int,
    val grammalogueCount: Int,
    val grammalogueSamples: List<String>,
    val legalContractionCount: Int,
    val contractionSamples: List<String>,
    val intersectionCount: Int,
    val intersectionSamples: List<String>,
    val halvedDoubledCount: Int,
    val complexityLevel: Int,
    val complexityLabel: String,
    val highlights: List<PassageHighlight>,
    val featuredStems: List<StenoStrokeSpec>,
    val allDetectedOutlines: List<StenoStrokeSpec>
)

data class LegalCasePassage(
    val id: String,
    val citation: String,
    val court: String,
    val category: String,
    val subtitle: String,
    val isVerified: Boolean,
    val text: String,
    val summary: String,
    val keyPrinciples: List<String>,
    val isCustom: Boolean = false,
    val sourceUrl: String = ""
)

enum class DictationMode {
    DRAFT_TTS,
    COURT_AUDIO
}

enum class PassageTab {
    TEXT,
    BRIEF_SUMMARY,
    OUTLINE_ANALYSIS
}

enum class PlaybackStatus {
    IDLE,
    SPEAKING,
    PAUSED,
    UNAVAILABLE
}

enum class AudioSourceType(
    val title: String,
    val badge: String
) {
    AUDIO_UPLOAD("Audio Upload (.mp3 / .wav)", "External File"),
    SUPREME_COURT_ARCHIVE("Supreme Court Audio Archive (Courtroom #1 Synced)", "MASTER RECORD"),
    AI_JUDICIAL_DICTATOR("AI Judicial Dictator (Neural 24kHz)", "Procedural TTS")
}

enum class VoiceCadenceProfile(
    val label: String,
    val shortLabel: String
) {
    COMMONWEALTH_PAKISTAN("Commonwealth / Pakistan Superior Court Cadence", "Commonwealth"),
    US_COURT_STENOMASK("US Court Stenomask", "Stenomask"),
    NEUTRAL_PACER("Neutral Pacer (Strict WPM)", "Strict WPM")
}

object WpmBenchmarkUtils {
    val presetWpms = listOf(60, 80, 100, 120, 140, 160)

    fun tierLabel(wpm: Int): String = when {
        wpm <= 70 -> "District Court Baseline"
        wpm <= 90 -> "Sessions Court Test"
        wpm <= 110 -> "High Court Qualifying Standard"
        wpm <= 130 -> "Supreme Court Qualifying"
        wpm <= 150 -> "Parliamentary Reporter Standard"
        else -> "Championship Sprint"
    }

    fun formatCompactDuration(seconds: Int): String {
        val safe = seconds.coerceAtLeast(1)
        val mins = safe / 60
        val secs = safe % 60
        return if (mins > 0) {
            "${mins}m ${secs.toString().padStart(2, '0')}s"
        } else {
            "${secs}s"
        }
    }

    fun formatClockDuration(seconds: Int): String {
        val safe = seconds.coerceAtLeast(0)
        val mins = safe / 60
        val secs = safe % 60
        return "$mins:${secs.toString().padStart(2, '0')}"
    }
}

enum class SymbolExerciseGroup(val label: String) {
    ALL("All"),
    STRAIGHT_STROKES("Straight"),
    CURVED_STROKES("Curved"),
    CIRCLES_HOOKS("Circles & Hooks"),
    GRAMMALOGUES("Grammalogues"),
    DAWN_EDITORIAL("Dawn Editorial"),
    LEGAL_OUTLINES("Legal")
}

data class PitmanSymbolEntry(
    val id: String,
    val spec: StenoStrokeSpec,
    val group: SymbolExerciseGroup,
    val phoneticCue: String,
    val directionHint: String,
    val isHeavy: Boolean
)



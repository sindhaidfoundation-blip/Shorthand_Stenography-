package com.example.domain.model

enum class SymbolsSubMode(val label: String) {
    WORKBOOK("Symbol Drills"),
    JSON_LEXICON("Legal JSON DB")
}

data class PitmanJsonDatabaseMetadata(
    val schemaVersion: Int = 1,
    val lexiconName: String = "Pakistan Legal & Editorial Pitman Shorthand Lexicon",
    val system: String = "Pitman New Era / Superior Judiciary Standard"
)

data class PitmanJsonLexiconEntry(
    val id: String,
    val term: String,
    val aliases: List<String>,
    val category: StenoCategory,
    val position: StenoPosition,
    val strokeFormula: String,
    val phoneticKey: String,
    val theoryPrinciple: String,
    val ruleNote: String,
    val legalDefinition: String,
    val glyphKey: String,
    val courtDomain: String,
    val frequencyRank: Int = 100,
    val isUserCustom: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis()
) {
    fun toStrokeSpec(): StenoStrokeSpec = StenoStrokeSpec(
        term = term,
        strokeFormula = strokeFormula,
        position = position,
        category = category,
        theoryPrinciple = theoryPrinciple,
        ruleNote = ruleNote,
        legalDefinition = legalDefinition,
        glyphKey = glyphKey
    )
}

data class PitmanJsonDatabaseSnapshot(
    val metadata: PitmanJsonDatabaseMetadata = PitmanJsonDatabaseMetadata(),
    val entries: List<PitmanJsonLexiconEntry> = emptyList(),
    val storageFileName: String = "pitman_legal_db.json"
) {
    val customEntryCount: Int
        get() = entries.count { it.isUserCustom }
}

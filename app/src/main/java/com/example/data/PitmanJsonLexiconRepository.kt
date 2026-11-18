package com.example.data

import android.content.Context
import com.example.domain.model.PitmanJsonDatabaseMetadata
import com.example.domain.model.PitmanJsonDatabaseSnapshot
import com.example.domain.model.PitmanJsonLexiconEntry
import com.example.domain.model.StenoCategory
import com.example.domain.model.StenoPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object PitmanJsonDatabaseSerializer {

    fun parseDatabaseJson(
        rawJson: String,
        storageFileName: String = "pitman_legal_db.json"
    ): PitmanJsonDatabaseSnapshot {
        val root = JSONObject(rawJson)
        val metadata = PitmanJsonDatabaseMetadata(
            schemaVersion = root.optInt("schemaVersion", 1),
            lexiconName = root.optString(
                "lexiconName",
                "Pakistan Legal & Editorial Pitman Shorthand Lexicon"
            ),
            system = root.optString(
                "system",
                "Pitman New Era / Superior Judiciary Standard"
            )
        )
        val array = root.optJSONArray("entries") ?: JSONArray()
        val entries = buildList(array.length()) {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val aliasesJson = obj.optJSONArray("aliases")
                val aliases = buildList {
                    if (aliasesJson != null) {
                        for (j in 0 until aliasesJson.length()) {
                            val value = aliasesJson.optString(j).trim()
                            if (value.isNotEmpty()) add(value)
                        }
                    }
                }.ifEmpty { listOf(obj.optString("term")) }

                val categoryName = obj.optString("category", StenoCategory.CONTRACTION.name)
                val category = StenoCategory.entries.firstOrNull {
                    it.name.equals(categoryName, ignoreCase = true)
                } ?: StenoCategory.CONTRACTION

                val positionName = obj.optString("position", StenoPosition.SECOND.name)
                val position = StenoPosition.entries.firstOrNull {
                    it.name.equals(positionName, ignoreCase = true)
                } ?: StenoPosition.SECOND

                val term = obj.optString("term").trim()
                if (term.isEmpty()) continue

                add(
                    PitmanJsonLexiconEntry(
                        id = obj.optString("id").ifBlank { "lex_${term.lowercase().hashCode()}" },
                        term = term,
                        aliases = aliases,
                        category = category,
                        position = position,
                        strokeFormula = obj.optString("strokeFormula", term.uppercase()),
                        phoneticKey = obj.optString("phoneticKey", term.lowercase()),
                        theoryPrinciple = obj.optString("theoryPrinciple", "Pitman Outline"),
                        ruleNote = obj.optString("ruleNote", "Stored in local Pitman JSON lexicon."),
                        legalDefinition = obj.optString("legalDefinition", "Legal terminology entry."),
                        glyphKey = obj.optString("glyphKey", "learned_counsel"),
                        courtDomain = obj.optString("courtDomain", "Appellate / SC"),
                        frequencyRank = obj.optInt("frequencyRank", 100),
                        isUserCustom = obj.optBoolean("isUserCustom", false),
                        createdAtEpochMs = obj.optLong("createdAtEpochMs", 0L)
                    )
                )
            }
        }
        return PitmanJsonDatabaseSnapshot(
            metadata = metadata,
            entries = entries,
            storageFileName = storageFileName
        )
    }

    fun serializeDatabaseJson(snapshot: PitmanJsonDatabaseSnapshot): String {
        val root = JSONObject().apply {
            put("schemaVersion", snapshot.metadata.schemaVersion)
            put("lexiconName", snapshot.metadata.lexiconName)
            put("system", snapshot.metadata.system)
            val entriesArray = JSONArray()
            snapshot.entries.forEach { entry ->
                entriesArray.put(
                    JSONObject().apply {
                        put("id", entry.id)
                        put("term", entry.term)
                        put("aliases", JSONArray(entry.aliases))
                        put("category", entry.category.name)
                        put("position", entry.position.name)
                        put("strokeFormula", entry.strokeFormula)
                        put("phoneticKey", entry.phoneticKey)
                        put("theoryPrinciple", entry.theoryPrinciple)
                        put("ruleNote", entry.ruleNote)
                        put("legalDefinition", entry.legalDefinition)
                        put("glyphKey", entry.glyphKey)
                        put("courtDomain", entry.courtDomain)
                        put("frequencyRank", entry.frequencyRank)
                        put("isUserCustom", entry.isUserCustom)
                        put("createdAtEpochMs", entry.createdAtEpochMs)
                    }
                )
            }
            put("entries", entriesArray)
        }
        return root.toString(2)
    }

    fun quickLookup(
        entries: List<PitmanJsonLexiconEntry>,
        query: String,
        domainFilter: String = "All"
    ): List<PitmanJsonLexiconEntry> {
        val q = query.trim().lowercase()
        return entries
            .filter { entry ->
                val matchesDomain = when (domainFilter) {
                    "All" -> true
                    "Custom" -> entry.isUserCustom
                    else -> entry.courtDomain.contains(domainFilter, ignoreCase = true)
                }
                val matchesQuery = q.isEmpty() ||
                    entry.term.lowercase().contains(q) ||
                    entry.aliases.any { it.lowercase().contains(q) } ||
                    entry.strokeFormula.lowercase().contains(q) ||
                    entry.phoneticKey.lowercase().contains(q) ||
                    entry.legalDefinition.lowercase().contains(q) ||
                    entry.courtDomain.lowercase().contains(q)
                matchesDomain && matchesQuery
            }
            .sortedWith(
                compareBy<PitmanJsonLexiconEntry>(
                    { if (q.isNotEmpty() && it.term.lowercase().startsWith(q)) 0 else 1 },
                    { if (it.isUserCustom) 0 else 1 },
                    { it.frequencyRank },
                    { it.term.lowercase() }
                )
            )
    }
}

class PitmanJsonLexiconRepository(private val context: Context) {

    companion object {
        private const val ASSET_SEED_NAME = "pitman_legal_lexicon.json"
        private const val DB_FILE_NAME = "pitman_legal_db.json"
    }

    private val dbFile: File
        get() = File(context.filesDir, DB_FILE_NAME)

    suspend fun loadDatabase(): PitmanJsonDatabaseSnapshot = withContext(Dispatchers.IO) {
        ensureSeededLocked()
        val raw = runCatching { dbFile.readText(Charsets.UTF_8) }.getOrNull()
        val parsed = raw?.let {
            runCatching { PitmanJsonDatabaseSerializer.parseDatabaseJson(it, DB_FILE_NAME) }.getOrNull()
        }
        if (parsed != null && parsed.entries.isNotEmpty()) {
            parsed
        } else {
            val seedRaw = readSeedAsset()
            writeAtomic(seedRaw)
            PitmanJsonDatabaseSerializer.parseDatabaseJson(seedRaw, DB_FILE_NAME)
        }
    }

    suspend fun addCustomEntry(
        term: String,
        strokeFormula: String,
        position: StenoPosition,
        category: StenoCategory,
        courtDomain: String,
        theoryPrinciple: String,
        ruleNote: String,
        legalDefinition: String,
        glyphKey: String
    ): PitmanJsonDatabaseSnapshot = withContext(Dispatchers.IO) {
        val current = loadDatabase()
        val cleanTerm = term.trim()
        val cleanFormula = strokeFormula.trim().ifEmpty {
            cleanTerm.uppercase().filter { it in "BCDFGHJKLMNPQRSTVWXYZ" }
                .toCharArray().joinToString("-")
                .ifEmpty { cleanTerm.uppercase() }
        }
        val phoneticKey = cleanTerm.lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
        val newEntry = PitmanJsonLexiconEntry(
            id = "custom_lex_${System.currentTimeMillis()}_${cleanTerm.hashCode()}",
            term = cleanTerm,
            aliases = listOf(cleanTerm.lowercase()),
            category = category,
            position = position,
            strokeFormula = cleanFormula,
            phoneticKey = phoneticKey,
            theoryPrinciple = theoryPrinciple.trim().ifEmpty { "Custom Legal Outline (${position.badge})" },
            ruleNote = ruleNote.trim().ifEmpty { "User-added outline stored in local JSON lexicon database." },
            legalDefinition = legalDefinition.trim().ifEmpty { "Custom legal term for Pitman courtroom dictation." },
            glyphKey = glyphKey,
            courtDomain = courtDomain.trim().ifEmpty { "Custom" },
            frequencyRank = 0,
            isUserCustom = true,
            createdAtEpochMs = System.currentTimeMillis()
        )
        val updated = current.copy(entries = listOf(newEntry) + current.entries)
        writeAtomic(PitmanJsonDatabaseSerializer.serializeDatabaseJson(updated))
        updated
    }

    suspend fun deleteEntry(entryId: String): PitmanJsonDatabaseSnapshot = withContext(Dispatchers.IO) {
        val current = loadDatabase()
        val updated = current.copy(entries = current.entries.filterNot { it.id == entryId })
        writeAtomic(PitmanJsonDatabaseSerializer.serializeDatabaseJson(updated))
        updated
    }

    suspend fun resetToDefaultSeed(): PitmanJsonDatabaseSnapshot = withContext(Dispatchers.IO) {
        val seedRaw = readSeedAsset()
        writeAtomic(seedRaw)
        PitmanJsonDatabaseSerializer.parseDatabaseJson(seedRaw, DB_FILE_NAME)
    }

    private fun ensureSeededLocked() {
        if (!dbFile.exists() || dbFile.length() == 0L) {
            writeAtomic(readSeedAsset())
        }
    }

    private fun readSeedAsset(): String {
        return context.assets.open(ASSET_SEED_NAME).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private fun writeAtomic(content: String) {
        val parent = dbFile.parentFile
        if (parent != null && !parent.exists()) {
            parent.mkdirs()
        }
        val tmp = File(dbFile.parentFile, "$DB_FILE_NAME.tmp")
        tmp.writeText(content, Charsets.UTF_8)
        if (!tmp.renameTo(dbFile)) {
            dbFile.writeText(content, Charsets.UTF_8)
            tmp.delete()
        }
    }
}

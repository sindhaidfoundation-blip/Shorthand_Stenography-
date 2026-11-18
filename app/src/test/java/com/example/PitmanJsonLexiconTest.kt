package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PitmanJsonDatabaseSerializer
import com.example.data.PitmanJsonLexiconRepository
import com.example.domain.model.StenoCategory
import com.example.domain.model.StenoPosition
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PitmanJsonLexiconTest {

    @Test
    fun loadsSeedJsonDatabaseAndSupportsQuickLookup() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbFile = File(context.filesDir, "pitman_legal_db.json")
        if (dbFile.exists()) dbFile.delete()

        val repo = PitmanJsonLexiconRepository(context)
        val snapshot = repo.loadDatabase()

        assertEquals(1, snapshot.metadata.schemaVersion)
        assertTrue("Expected at least 25 seed entries", snapshot.entries.size >= 25)
        assertTrue("Expected seeded JSON file on disk", dbFile.exists())

        // Quick lookup by legal term
        val counselHits = PitmanJsonDatabaseSerializer.quickLookup(snapshot.entries, "counsel")
        assertTrue(counselHits.any { it.term == "learned counsel" })

        // Quick lookup by Pitman stroke formula
        val hkHits = PitmanJsonDatabaseSerializer.quickLookup(snapshot.entries, "H-K")
        assertTrue(hkHits.any { it.glyphKey == "high_court" })

        // Quick lookup by domain filter
        val criminalHits = PitmanJsonDatabaseSerializer.quickLookup(
            snapshot.entries,
            query = "",
            domainFilter = "Criminal"
        )
        assertTrue(criminalHits.isNotEmpty())
        assertTrue(criminalHits.all { it.courtDomain.contains("Criminal", ignoreCase = true) })
    }

    @Test
    fun persistsCustomOutlineAndResetsCleanlyToSeed() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = PitmanJsonLexiconRepository(context)
        repo.resetToDefaultSeed()

        val afterAdd = repo.addCustomEntry(
            term = "Writ of Certiorari",
            strokeFormula = "R-T + S-R-Sh-R-R",
            position = StenoPosition.SECOND,
            category = StenoCategory.PHRASING,
            courtDomain = "Constitutional",
            theoryPrinciple = "Halved Ray-T + Phrased Certiorari",
            ruleNote = "Used in High Court Article 199 petitions.",
            legalDefinition = "Judicial writ quashing an order passed without lawful jurisdiction.",
            glyphKey = "high_court"
        )
        assertEquals(1, afterAdd.customEntryCount)

        // Re-read fresh from JSON file on disk
        val reloaded = repo.loadDatabase()
        val found = reloaded.entries.firstOrNull { it.term == "Writ of Certiorari" }
        assertTrue("Expected persisted custom entry on disk", found != null && found.isUserCustom)

        // Delete custom entry
        val afterDelete = repo.deleteEntry(found!!.id)
        assertFalse(afterDelete.entries.any { it.id == found.id })
        assertEquals(0, afterDelete.customEntryCount)
    }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PakistanLegalCorpus
import com.example.data.SettingsPreferences
import com.example.domain.model.LegalCasePassage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BookmarksPreferencesTest {

    @Test
    fun togglesAndPersistsCorpusAndCustomBookmarksLocally() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = SettingsPreferences(context)
        val corpusCase = PakistanLegalCorpus.cases.first()
        val customCase = LegalCasePassage(
            id = "custom_test_1",
            citation = "Custom Writ Petition",
            court = "Islamabad High Court",
            category = "Custom Draft",
            subtitle = "User Draft",
            isVerified = false,
            text = "Learned counsel for the petitioner submits that due process applies.",
            summary = "Test custom summary",
            keyPrinciples = listOf("Principle 1"),
            isCustom = true
        )

        prefs.removeBookmark(corpusCase.id)
        prefs.removeBookmark(customCase.id)

        val addedCorpus = prefs.toggleBookmark(corpusCase)
        val addedCustom = prefs.toggleBookmark(customCase)
        assertTrue(addedCorpus)
        assertTrue(addedCustom)

        val stateAfterAdd = prefs.preferencesFlow.first()
        assertTrue(corpusCase.id in stateAfterAdd.bookmarkedIds)
        assertTrue(customCase.id in stateAfterAdd.bookmarkedIds)
        assertEquals(1, stateAfterAdd.customBookmarks.size)
        assertEquals("Custom Writ Petition", stateAfterAdd.customBookmarks.first().citation)

        val removedCorpus = prefs.toggleBookmark(corpusCase)
        assertFalse(removedCorpus)
        prefs.removeBookmark(customCase.id)

        val stateAfterRemove = prefs.preferencesFlow.first()
        assertFalse(corpusCase.id in stateAfterRemove.bookmarkedIds)
        assertFalse(customCase.id in stateAfterRemove.bookmarkedIds)
        assertTrue(stateAfterRemove.customBookmarks.isEmpty())
    }
}

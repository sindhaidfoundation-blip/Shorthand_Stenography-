package com.example

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MainActivityUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun resourcesLoadWithoutError() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        listOf(
            R.drawable.img_pitman_crest,
            R.drawable.img_judicial_banner,
            R.drawable.pitman_launcher_fg
        ).forEach { resId ->
            val bmp = BitmapFactory.decodeResource(context.resources, resId)
            assertNotNull("Failed to decode drawable $resId", bmp)
            assertTrue("Drawable $resId too wide: ${bmp.width}", bmp.width in 1..4096)
            assertTrue("Drawable $resId too tall: ${bmp.height}", bmp.height in 1..4096)
        }
        listOf(
            R.font.source_serif_4,
            R.font.noto_sans,
            R.font.jetbrains_mono
        ).forEach { fontRes ->
            assertNotNull("Failed to load font $fontRes", ResourcesCompat.getFont(context, fontRes))
            context.resources.openRawResource(fontRes).use { stream ->
                val header = ByteArray(4)
                val read = stream.read(header)
                assertTrue("Font $fontRes header too short", read == 4)
                val isTrueType = (header[0] == 0x00.toByte() && header[1] == 0x01.toByte() && header[2] == 0x00.toByte() && header[3] == 0x00.toByte())
                val isOpenType = (header[0] == 'O'.code.toByte() && header[1] == 'T'.code.toByte() && header[2] == 'T'.code.toByte() && header[3] == 'O'.code.toByte())
                assertTrue("Font $fontRes is neither TTF nor OTF", isTrueType || isOpenType)
            }
        }
    }

    @Test
    fun launchesMainActivityAndExercisesAllScreensAndSheets() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("wpm_header_pill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passage_body_text").performScrollTo().assertIsDisplayed()

        // Toggle Court Audio mode and back
        composeTestRule.onNodeWithTag("mode_court_audio").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("pick_audio_button").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("mode_draft_tts").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Open stem inspector sheet (scroll past the floating DockedDictationBar)
        composeTestRule.onNodeWithTag("dictate_lazy_list")
            .performScrollToNode(hasTestTag("stem_card_learned_counsel"))
        composeTestRule.onNodeWithTag("dictate_lazy_list").performTouchInput {
            swipeUp(startY = centerY, endY = centerY - 120f)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("stem_card_learned_counsel").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("pronounce_outline_button").assertExists()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.waitForIdle()

        // Open and close Profile calibration sheet
        composeTestRule.onNodeWithTag("profile_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.waitForIdle()

        // Navigate across tabs
        composeTestRule.onNodeWithTag("nav_draft").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("draft_text_input").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_corpus").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("corpus_search_input").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_dictate").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("dictate_lazy_list").performScrollToIndex(0)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("passage_body_text").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun exercisesAcousticDictationSpeedControlsAndCadenceBenchmark() {
        composeTestRule.waitForIdle()

        // Scroll to Acoustic Dictation Deck WPM presets
        composeTestRule.onNodeWithTag("dictate_lazy_list")
            .performScrollToNode(hasTestTag("speed_preset_120"))
        composeTestRule.onNodeWithTag("dictate_lazy_list").performTouchInput {
            swipeUp(startY = centerY, endY = centerY - 280f)
        }
        composeTestRule.waitForIdle()

        // Select 120 WPM preset and verify tier label updates
        composeTestRule.onNodeWithTag("speed_preset_120").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("slider_wpm_display").assertExists()

        // Scroll to Live Cadence Metronome & Test Cadence Preview button
        composeTestRule.onNodeWithTag("dictate_lazy_list")
            .performScrollToNode(hasTestTag("test_cadence_preview_button"))
        composeTestRule.onNodeWithTag("dictate_lazy_list").performTouchInput {
            swipeUp(startY = centerY, endY = centerY - 280f)
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("test_cadence_preview_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("test_cadence_preview_button").performClick()
        composeTestRule.waitForIdle()

        // Exercise Save to Legal Library toggle (save and restore) and Export Steno PDF button
        composeTestRule.onNodeWithTag("dictate_lazy_list")
            .performScrollToNode(hasTestTag("save_legal_library_button"))
        composeTestRule.onNodeWithTag("dictate_lazy_list").performTouchInput {
            swipeUp(startY = centerY, endY = centerY - 280f)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("save_legal_library_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("save_legal_library_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("export_steno_pdf_button").assertIsDisplayed()
    }

    @Test
    fun exercisesSymbolsWorkbookAndDawnCorpusDictation() {
        composeTestRule.waitForIdle()

        // Navigate to Symbols tab
        composeTestRule.onNodeWithTag("nav_symbols").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("symbol_drill_card").assertExists()

        // Answer drill and advance to next symbol
        composeTestRule.onNodeWithTag("symbols_lazy_list")
            .performScrollToNode(hasTestTag("drill_option_0"))
        composeTestRule.onNodeWithTag("drill_option_0").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("drill_next_button").performClick()
        composeTestRule.waitForIdle()

        // Filter to Dawn Editorial symbols and open symbol inspector sheet
        composeTestRule.onNodeWithTag("symbols_lazy_list")
            .performScrollToNode(hasTestTag("symbol_group_DAWN_EDITORIAL"))
        composeTestRule.onNodeWithTag("symbol_group_DAWN_EDITORIAL").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("symbols_lazy_list")
            .performScrollToNode(hasTestTag("symbol_card_dawn_government"))
        composeTestRule.onNodeWithTag("symbol_card_dawn_government").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("pronounce_outline_button").assertExists()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.waitForIdle()

        // Switch to Legal JSON DB quick lookup mode inside Symbols tab
        composeTestRule.onNodeWithTag("symbols_lazy_list").performScrollToIndex(0)
        composeTestRule.onNodeWithTag("symbols_mode_JSON_LEXICON").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("json_db_status_card").assertExists()
        composeTestRule.onNodeWithTag("json_lexicon_lazy_list")
            .performScrollToNode(hasTestTag("json_entry_lex_habeas_corpus"))
        composeTestRule.onNodeWithTag("json_entry_lex_habeas_corpus").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("pronounce_outline_button").assertExists()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.waitForIdle()

        // Navigate to Corpus and filter Dawn Editorial dictation
        composeTestRule.onNodeWithTag("nav_corpus").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("corpus_lazy_list")
            .performScrollToNode(hasTestTag("dawn_feed_card"))
        composeTestRule.onNodeWithTag("dawn_feed_card").assertExists()
        composeTestRule.onNodeWithTag("corpus_filter_Dawn Editorial").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("corpus_lazy_list")
            .performScrollToNode(hasTestTag("corpus_case_dawn_ed_fiscal_reform"))
        composeTestRule.onNodeWithTag("corpus_case_dawn_ed_fiscal_reform").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("passage_body_text").performScrollTo().assertIsDisplayed()
    }
}

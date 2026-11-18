package com.example

import com.example.data.PakistanLegalCorpus
import com.example.domain.pitman.PitmanAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PitmanAnalyzerTest {

    @Test
    fun analyzesPld2023Sc542PassageAccurately() {
        val passage = PakistanLegalCorpus.cases.first()
        val analysis = PitmanAnalyzer.analyze(passage.text)

        assertTrue("Expected at least 80 words, got ${analysis.wordCount}", analysis.wordCount >= 80)
        assertTrue("Expected grammalogues > 10", analysis.grammalogueCount > 10)
        assertTrue("Expected legal contractions > 0", analysis.legalContractionCount > 0)
        assertTrue("Expected intersections > 0", analysis.intersectionCount > 0)
        assertEquals(4, analysis.featuredStems.size)
        assertTrue(
            "Expected learned counsel highlight",
            analysis.highlights.any { it.matchedText.equals("learned counsel", ignoreCase = true) }
        )
        val learnedCounsel = analysis.highlights.first {
            it.matchedText.equals("learned counsel", ignoreCase = true)
        }
        assertTrue(learnedCounsel.spec.theoryPrinciple.isNotBlank())
        assertTrue(learnedCounsel.spec.legalDefinition.isNotBlank())
        assertTrue(learnedCounsel.spec.ruleNote.isNotBlank())
    }

    @Test
    fun analyzesCustomUserDraftGracefully() {
        val custom = "The appellant respectfully submits that the tribunal lacked territorial jurisdiction."
        val analysis = PitmanAnalyzer.analyze(custom)

        assertEquals(10, analysis.wordCount)
        assertTrue(analysis.secondsAt100Wpm > 0)
        assertTrue(analysis.featuredStems.isNotEmpty())
    }
}

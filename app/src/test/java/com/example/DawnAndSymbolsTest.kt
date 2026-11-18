package com.example

import com.example.data.DawnFeedType
import com.example.data.DawnRssFetcher
import com.example.data.PakistanLegalCorpus
import com.example.domain.model.SymbolExerciseGroup
import com.example.domain.pitman.PitmanAnalyzer
import com.example.domain.pitman.PitmanSymbolCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DawnAndSymbolsTest {

    @Test
    fun parsesDawnRssXmlIntoDictationPassages() {
        val sampleXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/">
              <channel>
                <title>Dawn Opinion</title>
                <item>
                  <title>Parliamentary oversight &amp; reform</title>
                  <link>https://www.dawn.com/news/12345</link>
                  <guid>https://www.dawn.com/news/12345</guid>
                  <pubDate>Tue, 18 Nov 2026 06:15:00 +0500</pubDate>
                  <dc:creator>Editorial</dc:creator>
                  <description>&lt;p&gt;Stronger &lt;b&gt;accountability&lt;/b&gt; in parliament &amp;ldquo;cannot wait&amp;rdquo; while inflation persists.&lt;/p&gt;</description>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val parsed = DawnRssFetcher.parseRssXml(sampleXml, DawnFeedType.OPINION)
        assertEquals(1, parsed.size)
        val item = parsed.first()
        assertEquals("Dawn Editorial", item.category)
        assertEquals("DAWN Editorial • 18 Nov", item.citation)
        assertTrue(item.court.contains("Editorial"))
        assertFalse(item.text.contains("<p>"))
        assertFalse(item.text.contains("&ldquo;"))
        assertTrue(item.text.contains("Parliamentary oversight & reform"))
        assertTrue(item.text.contains("accountability"))
    }

    @Test
    fun builtInDawnPassagesAnalyzeWithEditorialHighlights() {
        val editorials = PakistanLegalCorpus.cases.filter { it.category == "Dawn Editorial" }
        val news = PakistanLegalCorpus.cases.filter { it.category == "Dawn News" }
        assertTrue("Expected Dawn Editorial passages", editorials.size >= 3)
        assertTrue("Expected Dawn News passages", news.size >= 3)

        val fiscalEditorial = editorials.first { it.id == "dawn_ed_fiscal_reform" }
        val analysis = PitmanAnalyzer.analyze(fiscalEditorial.text)
        assertTrue(analysis.wordCount >= 75)
        assertTrue(
            analysis.highlights.any { it.matchedText.contains("fiscal", ignoreCase = true) }
        )
        assertTrue(
            analysis.highlights.any { it.matchedText.contains("governments", ignoreCase = true) }
        )
    }

    @Test
    fun symbolCatalogCoversEveryGroupAndGeneratesValidDrillOptions() {
        val entries = PitmanSymbolCatalog.entries
        assertTrue("Expected at least 35 Pitman symbols", entries.size >= 35)
        assertEquals(entries.size, entries.map { it.id }.distinct().size)

        SymbolExerciseGroup.entries
            .filter { it != SymbolExerciseGroup.ALL }
            .forEach { group ->
                val items = PitmanSymbolCatalog.entriesFor(group)
                assertTrue("Group $group should not be empty", items.isNotEmpty())
            }

        entries.take(5).forEachIndexed { idx, target ->
            val options = PitmanSymbolCatalog.drillOptionsFor(target, idx)
            assertEquals(4, options.size)
            assertEquals(4, options.map { it.id }.distinct().size)
            assertTrue(options.any { it.id == target.id })
        }
    }
}

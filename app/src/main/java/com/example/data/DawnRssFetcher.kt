package com.example.data

import com.example.domain.model.LegalCasePassage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

enum class DawnFeedType(
    val url: String,
    val category: String,
    val deskLabel: String,
    val citationPrefix: String
) {
    OPINION(
        url = "https://www.dawn.com/feeds/opinion",
        category = "Dawn Editorial",
        deskLabel = "Dawn Newspaper — Opinion & Editorial",
        citationPrefix = "DAWN Editorial"
    ),
    NEWS(
        url = "https://www.dawn.com/feeds/home",
        category = "Dawn News",
        deskLabel = "Dawn Newspaper — Top Stories",
        citationPrefix = "DAWN News"
    )
}

object DawnRssFetcher {

    suspend fun fetchFeed(
        feedType: DawnFeedType,
        maxItems: Int = 20
    ): Result<List<LegalCasePassage>> = withContext(Dispatchers.IO) {
        runCatching {
            val xml = downloadUrl(feedType.url)
            val parsed = parseRssXml(xml, feedType, maxItems)
            if (parsed.isEmpty()) {
                error("No articles found in Dawn ${feedType.category} feed")
            }
            parsed
        }
    }

    private fun downloadUrl(urlString: String): String {
        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
            instanceFollowRedirects = true
            setRequestProperty(
                "User-Agent",
                "PitmanStenoTrainer/1.0 (Android; RSS Dictation Reader)"
            )
            setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml")
        }
        return try {
            val code = connection.responseCode
            if (code !in 200..299) {
                error("Dawn feed HTTP $code")
            }
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    fun parseRssXml(
        xml: String,
        feedType: DawnFeedType,
        maxItems: Int = 8
    ): List<LegalCasePassage> {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false
        }
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(InputSource(StringReader(xml.trim())))
        val nodeList = document.getElementsByTagName("item")

        val items = mutableListOf<LegalCasePassage>()
        for (i in 0 until nodeList.length) {
            if (items.size >= maxItems) break
            val element = nodeList.item(i) as? Element ?: continue

            val rawTitle = firstChildText(element, "title")
            // Dawn RSS description is a short summary; avoid storing full article bodies.
            val rawDescription = firstChildText(element, "description")
                .ifBlank { firstChildText(element, "content:encoded") }
            val pubDate = firstChildText(element, "pubDate")
            val articleUrl = firstChildText(element, "link").ifBlank {
                firstChildText(element, "guid")
            }
            val guid = firstChildText(element, "guid").ifBlank { articleUrl }
            val author = firstChildText(element, "dc:creator")
                .ifBlank { firstChildText(element, "author") }

            val cleanTitle = cleanHtml(rawTitle)
            val cleanDesc = cleanHtml(rawDescription)
            if (cleanTitle.isNotBlank() && cleanDesc.isNotBlank()) {
                val combinedBody = buildDictationProse(cleanTitle, cleanDesc)
                val shortDate = formatShortDate(pubDate)
                val citation = if (shortDate.isNotBlank()) {
                    "${feedType.citationPrefix} • $shortDate"
                } else {
                    "${feedType.citationPrefix} • Live"
                }
                val stableHash = (guid.ifBlank { cleanTitle }).hashCode()
                val courtLine = if (author.isNotBlank()) {
                    "${feedType.deskLabel} ($author)"
                } else {
                    feedType.deskLabel
                }
                items.add(
                    LegalCasePassage(
                        id = "dawn_live_${feedType.name.lowercase()}_$stableHash",
                        citation = citation,
                        court = courtLine,
                        category = feedType.category,
                        subtitle = cleanTitle,
                        isVerified = true,
                        text = combinedBody,
                        summary = cleanDesc,
                        keyPrinciples = listOf(
                            "Live Dawn Newspaper ${feedType.category} dispatch formatted for stenographic dictation.",
                            "Calibrated for journalistic vocabulary, parliamentary terms, and economic contractions."
                        ),
                        sourceUrl = articleUrl.takeIf { it.startsWith("https://www.dawn.com/") }.orEmpty()
                    )
                )
            }
        }
        return items
    }

    private fun firstChildText(parent: Element, tagName: String): String {
        val list = parent.getElementsByTagName(tagName)
        if (list.length == 0) return ""
        return list.item(0)?.textContent.orEmpty()
    }

    private fun cleanHtml(raw: String): String {
        return raw
            .replace(Regex("<[^>]+>"), " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&nbsp;", " ")
            .replace("&rsquo;", "'")
            .replace("&lsquo;", "'")
            .replace("&ldquo;", "\"")
            .replace("&rdquo;", "\"")
            .replace("&mdash;", " — ")
            .replace("&ndash;", " - ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun buildDictationProse(title: String, description: String): String {
        val cleanTitle = title.trim().trimEnd('.', '!', '?')
        val cleanDesc = description.trim()
        return if (cleanDesc.startsWith(cleanTitle, ignoreCase = true)) {
            cleanDesc
        } else {
            "$cleanTitle. $cleanDesc"
        }
    }

    private fun formatShortDate(pubDate: String): String {
        val tokens = pubDate.trim().split(Regex("\\s+"))
        return if (tokens.size >= 3) "${tokens[1]} ${tokens[2]}" else ""
    }
}

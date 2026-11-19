package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.LegalCasePassage
import com.example.domain.pitman.PitmanAnalyzer
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalStenoColors
import com.example.ui.theme.SourceSerifFontFamily
import com.example.ui.viewmodel.StenoUiState

@Composable
fun CorpusScreen(
    uiState: StenoUiState,
    onFilterChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectCase: (LegalCasePassage) -> Unit,
    onBookmarkToggle: (LegalCasePassage) -> Unit,
    onFetchDawnFeed: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    val uriHandler = LocalUriHandler.current
    val categories = listOf(
        "All",
        "Saved",
        "Dawn Editorial",
        "Dawn News",
        "Constitutional",
        "Criminal",
        "Civil",
        "Service",
        "Arbitration"
    )

    val filteredCases = remember(
        uiState.corpusCases,
        uiState.liveDawnPassages,
        uiState.prefs.customBookmarks,
        uiState.prefs.bookmarkedIds,
        uiState.selectedCorpusFilter,
        uiState.corpusSearchQuery
    ) {
        val sourcePool = if (uiState.selectedCorpusFilter == "Saved") {
            uiState.bookmarkedPassages
        } else {
            (uiState.liveDawnPassages +
                uiState.prefs.customBookmarks.filter { it.id in uiState.prefs.bookmarkedIds } +
                uiState.corpusCases).distinctBy { it.id }
        }

        sourcePool.filter { item ->
            val matchesCategory = when (uiState.selectedCorpusFilter) {
                "All", "Saved" -> true
                else -> item.category.equals(uiState.selectedCorpusFilter, ignoreCase = true)
            }
            val q = uiState.corpusSearchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                item.citation.contains(q, ignoreCase = true) ||
                item.court.contains(q, ignoreCase = true) ||
                item.subtitle.contains(q, ignoreCase = true) ||
                item.text.contains(q, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 680.dp)
            .testTag("corpus_lazy_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Judicial Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_judicial_banner),
                    contentDescription = stringResource(R.string.cd_judicial_banner),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    extendedColors.scrim.copy(alpha = 0.25f),
                                    extendedColors.onPrimaryFixed.copy(alpha = 0.90f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "PLD / SCMR / DAWN NEWSPAPER ARCHIVE",
                        fontFamily = JetBrainsMonoFontFamily,
                        style = MaterialTheme.typography.labelMedium,
                        color = extendedColors.secondaryFixed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Legal & Dawn Dictation Corpus",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = SourceSerifFontFamily,
                        color = Color.White
                    )
                    Text(
                        text = "${uiState.corpusCases.size + uiState.liveDawnPassages.size} verified excerpts • ${uiState.bookmarkedPassages.size} bookmarked",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = uiState.corpusSearchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search citation (PLD, SCMR, DAWN), court, or topic...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("corpus_search_input")
            )
        }

        // Category Filter Row (includes Saved, Dawn Editorial, Dawn News)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val selected = uiState.selectedCorpusFilter == cat
                    val labelText = if (cat == "Saved") {
                        "Saved (${uiState.bookmarkedPassages.size})"
                    } else {
                        cat
                    }
                    Surface(
                        color = if (selected) MaterialTheme.colorScheme.secondary else extendedColors.surfaceContainerHigh,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { onFilterChange(cat) }
                            .testTag("corpus_filter_$cat")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            if (cat == "Saved") {
                                Icon(
                                    imageVector = Icons.Rounded.Bookmark,
                                    contentDescription = null,
                                    tint = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Live Dawn Newspaper RSS Importer Card
        item {
            Surface(
                color = extendedColors.surfaceContainerLowest,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dawn_feed_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Newspaper,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Column {
                                Text(
                                    text = "Dawn Newspaper Dictation",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Built-in editorials + live Dawn RSS dispatches for FPSC/Court shorthand",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (uiState.isFetchingDawn) {
                            CircularProgressIndicator(
                                strokeWidth = 2.5.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onFetchDawnFeed(true) },
                            enabled = !uiState.isFetchingDawn,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("fetch_dawn_opinion_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dawn Opinion",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        OutlinedButton(
                            onClick = { onFetchDawnFeed(false) },
                            enabled = !uiState.isFetchingDawn,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("fetch_dawn_news_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dawn News",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        if (filteredCases.isEmpty()) {
            item {
                val isSavedEmpty = uiState.selectedCorpusFilter == "Saved" && uiState.corpusSearchQuery.isBlank()
                Surface(
                    color = extendedColors.surfaceContainerLow,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isSavedEmpty) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = if (isSavedEmpty) "No saved case studies yet" else "No matching law reports",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isSavedEmpty) {
                                "Tap the bookmark icon on any legal excerpt to pin it to your local practice list."
                            } else {
                                "Try clearing your search or selecting All categories."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isSavedEmpty) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Button(
                                onClick = { onFilterChange("All") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Show All Cases")
                            }
                        }
                    }
                }
            }
        } else {
            items(filteredCases, key = { it.id }) { item ->
                val isSelected = uiState.activePassage.id == item.id
                val isBookmarked = item.id in uiState.prefs.bookmarkedIds
                val itemAnalysis = remember(item.text) { PitmanAnalyzer.analyze(item.text) }

                Surface(
                    color = extendedColors.surfaceContainerLowest,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = if (isSelected) 3.dp else 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectCase(item) }
                        .testTag("corpus_case_${item.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = item.citation,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("•", color = MaterialTheme.colorScheme.outlineVariant)
                                Text(
                                    text = item.court,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "Active judgment",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else if (item.isVerified) {
                                    Icon(
                                        imageVector = Icons.Outlined.Verified,
                                        contentDescription = "Verified report",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                                IconButton(
                                    onClick = { onBookmarkToggle(item) },
                                    modifier = Modifier.testTag("corpus_bookmark_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) {
                                            Icons.Rounded.Bookmark
                                        } else {
                                            Icons.Outlined.BookmarkBorder
                                        },
                                        contentDescription = if (isBookmarked) {
                                            "Remove bookmark"
                                        } else {
                                            "Bookmark case study"
                                        },
                                        tint = if (isBookmarked) {
                                            MaterialTheme.colorScheme.secondary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }

                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = if (item.sourceUrl.isNotBlank()) {
                                "Summary: ${item.summary.ifBlank { item.subtitle }}"
                            } else {
                                item.text
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.sourceUrl.isNotBlank()) {
                            Text(
                                text = "Read original article on Dawn ↗",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .clickable {
                                        runCatching { uriHandler.openUri(item.sourceUrl) }
                                    }
                                    .padding(vertical = 6.dp)
                                    .testTag("dawn_source_link_${item.id}")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${itemAnalysis.wordCount} Words • ${itemAnalysis.secondsAt100Wpm}s @ 100 WPM",
                                fontFamily = JetBrainsMonoFontFamily,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Surface(
                                color = extendedColors.surfaceContainerLow,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Lvl ${itemAnalysis.complexityLevel} • ${item.category}",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

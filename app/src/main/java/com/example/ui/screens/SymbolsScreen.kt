package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.PitmanSymbolEntry
import com.example.domain.model.StenoCategory
import com.example.domain.model.StenoPosition
import com.example.domain.model.StenoStrokeSpec
import com.example.domain.model.SymbolExerciseGroup
import com.example.domain.model.SymbolsSubMode
import com.example.domain.pitman.PitmanSymbolCatalog
import com.example.ui.components.LegalJsonLexiconPane
import com.example.ui.components.PitmanStemCanvas
import com.example.ui.components.SymbolsSubModeSwitcher
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalStenoColors
import com.example.ui.theme.SourceSerifFontFamily
import com.example.ui.viewmodel.StenoUiState

@Composable
fun SymbolsScreen(
    uiState: StenoUiState,
    onSelectGroup: (SymbolExerciseGroup) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onInspectSymbol: (StenoStrokeSpec) -> Unit,
    onPronounceTerm: (String) -> Unit,
    onSelectSubMode: (SymbolsSubMode) -> Unit = {},
    onJsonQueryChange: (String) -> Unit = {},
    onJsonDomainFilterChange: (String) -> Unit = {},
    onAddJsonLexiconEntry: (
        term: String,
        strokeFormula: String,
        position: StenoPosition,
        category: StenoCategory,
        courtDomain: String,
        theoryPrinciple: String,
        ruleNote: String,
        legalDefinition: String,
        glyphKey: String
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onDeleteJsonLexiconEntry: (String) -> Unit = {},
    onResetJsonLexiconSeed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (uiState.symbolsSubMode == SymbolsSubMode.JSON_LEXICON) {
        LegalJsonLexiconPane(
            uiState = uiState,
            onSwitchSubMode = onSelectSubMode,
            onQueryChange = onJsonQueryChange,
            onDomainFilterChange = onJsonDomainFilterChange,
            onInspectSpec = onInspectSymbol,
            onPronounceTerm = onPronounceTerm,
            onAddCustomEntry = onAddJsonLexiconEntry,
            onDeleteEntry = onDeleteJsonLexiconEntry,
            onResetSeed = onResetJsonLexiconSeed,
            modifier = modifier
        )
        return
    }

    val extendedColors = LocalStenoColors.current
    val filteredEntries = remember(uiState.selectedSymbolGroup, uiState.symbolSearchQuery) {
        PitmanSymbolCatalog.entriesFor(uiState.selectedSymbolGroup, uiState.symbolSearchQuery)
    }

    val drillPool = remember(uiState.selectedSymbolGroup) {
        val groupItems = PitmanSymbolCatalog.entriesFor(uiState.selectedSymbolGroup)
        groupItems.ifEmpty { PitmanSymbolCatalog.entries }
    }
    var drillIndex by remember { mutableIntStateOf(0) }
    var drillStreak by remember { mutableIntStateOf(0) }
    var selectedAnswerId by remember { mutableStateOf<String?>(null) }
    var drillExpanded by remember { mutableStateOf(true) }

    val activeDrillTarget = drillPool[drillIndex % drillPool.size]
    val drillOptions = remember(activeDrillTarget.id, drillIndex) {
        PitmanSymbolCatalog.drillOptionsFor(activeDrillTarget, drillIndex)
    }

    val rows = remember(filteredEntries) { filteredEntries.chunked(2) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 680.dp)
            .testTag("symbols_lazy_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SymbolsSubModeSwitcher(
                activeMode = uiState.symbolsSubMode,
                onModeSelected = onSelectSubMode
            )
        }

        // Header Summary Banner
        item {
            Surface(
                color = extendedColors.surfaceContainerLowest,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SHORTHAND DRILL WORKBOOK",
                                fontFamily = JetBrainsMonoFontFamily,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.symbols_screen_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = SourceSerifFontFamily,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${filteredEntries.size} Symbols",
                                fontFamily = JetBrainsMonoFontFamily,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.symbols_screen_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Interactive Symbol Recognition Drill
        item {
            Surface(
                color = extendedColors.surfaceContainerLowest,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("symbol_drill_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { drillExpanded = !drillExpanded }
                            .minimumInteractiveComponentSize()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Column {
                                Text(
                                    text = "Interactive Symbol Recognition Drill",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Identify the Pitman stroke or contraction on the ruled line",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "Streak $drillStreak",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Icon(
                                imageVector = if (drillExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = if (drillExpanded) "Collapse drill" else "Expand drill",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (drillExpanded) {
                        PitmanStemCanvas(
                            spec = activeDrillTarget.spec,
                            isKaraokeHighlighted = selectedAnswerId == activeDrillTarget.id,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(98.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (activeDrillTarget.isHeavy) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    extendedColors.surfaceContainerHigh
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (activeDrillTarget.isHeavy) "HEAVY STROKE" else "LIGHT STROKE",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeDrillTarget.isHeavy) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = activeDrillTarget.spec.position.badge,
                                fontFamily = JetBrainsMonoFontFamily,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Cue: ${activeDrillTarget.directionHint}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 2x2 Options
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            drillOptions.chunked(2).forEachIndexed { rowIdx, pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    pair.forEachIndexed { colIdx, option ->
                                        val optIdx = rowIdx * 2 + colIdx
                                        val isAnswered = selectedAnswerId != null
                                        val isChosen = selectedAnswerId == option.id
                                        val isCorrectOption = option.id == activeDrillTarget.id

                                        val bgColor = when {
                                            !isAnswered -> extendedColors.surfaceContainerLow
                                            isCorrectOption -> MaterialTheme.colorScheme.secondaryContainer
                                            isChosen -> MaterialTheme.colorScheme.errorContainer
                                            else -> extendedColors.surfaceContainerLow
                                        }
                                        val textColor = when {
                                            !isAnswered -> MaterialTheme.colorScheme.primary
                                            isCorrectOption -> MaterialTheme.colorScheme.onSecondaryContainer
                                            isChosen -> MaterialTheme.colorScheme.onErrorContainer
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                        val borderColor = when {
                                            isAnswered && isCorrectOption -> MaterialTheme.colorScheme.secondary
                                            isAnswered && isChosen -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                        }

                                        Surface(
                                            color = bgColor,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, borderColor),
                                            modifier = Modifier
                                                .weight(1f)
                                                .heightIn(min = 48.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable(enabled = !isAnswered) {
                                                    selectedAnswerId = option.id
                                                    if (option.id == activeDrillTarget.id) {
                                                        drillStreak += 1
                                                    } else {
                                                        drillStreak = 0
                                                    }
                                                }
                                                .testTag("drill_option_$optIdx")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = option.spec.term,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = textColor,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                if (isAnswered && isCorrectOption) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.CheckCircle,
                                                        contentDescription = "Correct option",
                                                        tint = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (selectedAnswerId != null) {
                            val wasRight = selectedAnswerId == activeDrillTarget.id
                            Surface(
                                color = if (wasRight) {
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                } else {
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (wasRight) {
                                        "Correct! ${activeDrillTarget.spec.term} — ${activeDrillTarget.phoneticCue}"
                                    } else {
                                        "Answer: ${activeDrillTarget.spec.term} (${activeDrillTarget.phoneticCue})"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onPronounceTerm(activeDrillTarget.spec.term) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("drill_speak_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pronounce")
                            }
                            Button(
                                onClick = {
                                    selectedAnswerId = null
                                    drillIndex = (drillIndex + 1) % drillPool.size
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("drill_next_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Next Symbol")
                            }
                        }
                    }
                }
            }
        }

        // Search Field
        item {
            OutlinedTextField(
                value = uiState.symbolSearchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search stroke, hook, grammalogue, or Dawn outline...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.symbolSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("symbols_search_input")
            )
        }

        // Group Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SymbolExerciseGroup.entries.forEach { group ->
                    val selected = uiState.selectedSymbolGroup == group
                    Surface(
                        color = if (selected) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            extendedColors.surfaceContainerHigh
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .heightIn(min = 40.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable { onSelectGroup(group) }
                            .testTag("symbol_group_${group.name}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            if (group == SymbolExerciseGroup.DAWN_EDITORIAL) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (selected) {
                                        MaterialTheme.colorScheme.onSecondary
                                    } else {
                                        MaterialTheme.colorScheme.secondary
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = group.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.onSecondary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        }

        if (rows.isEmpty()) {
            item {
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
                        Text(
                            text = "No matching shorthand symbols",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Clear the search filter or choose All symbol groups.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(rows, key = { row -> row.joinToString("_") { it.id } }) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { entry ->
                        SymbolExerciseCard(
                            entry = entry,
                            onInspect = { onInspectSymbol(entry.spec) },
                            onSpeak = { onPronounceTerm(entry.spec.term) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SymbolExerciseCard(
    entry: PitmanSymbolEntry,
    onInspect: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current

    Surface(
        color = extendedColors.surfaceContainerLowest,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onInspect)
            .testTag("symbol_card_${entry.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.spec.term,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    color = if (entry.isHeavy) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        extendedColors.surfaceContainerHigh
                    },
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = if (entry.isHeavy) "HEAVY" else "LIGHT",
                        fontFamily = JetBrainsMonoFontFamily,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (entry.isHeavy) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            PitmanStemCanvas(
                spec = entry.spec,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
            )

            Text(
                text = entry.spec.strokeFormula,
                fontFamily = JetBrainsMonoFontFamily,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = entry.phoneticCue,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = entry.spec.position.badge,
                        fontFamily = JetBrainsMonoFontFamily,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier.testTag("symbol_speak_${entry.id}")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VolumeUp,
                        contentDescription = "Pronounce ${entry.spec.term}",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

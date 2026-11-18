package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.PitmanJsonDatabaseSerializer
import com.example.domain.model.PitmanJsonLexiconEntry
import com.example.domain.model.StenoCategory
import com.example.domain.model.StenoPosition
import com.example.domain.model.StenoStrokeSpec
import com.example.domain.model.SymbolsSubMode
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalStenoColors
import com.example.ui.theme.SourceSerifFontFamily
import com.example.ui.viewmodel.StenoUiState

@Composable
fun LegalJsonLexiconPane(
    uiState: StenoUiState,
    onSwitchSubMode: (SymbolsSubMode) -> Unit,
    onQueryChange: (String) -> Unit,
    onDomainFilterChange: (String) -> Unit,
    onInspectSpec: (StenoStrokeSpec) -> Unit,
    onPronounceTerm: (String) -> Unit,
    onAddCustomEntry: (
        term: String,
        strokeFormula: String,
        position: StenoPosition,
        category: StenoCategory,
        courtDomain: String,
        theoryPrinciple: String,
        ruleNote: String,
        legalDefinition: String,
        glyphKey: String
    ) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onResetSeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    val snapshot = uiState.jsonLexiconSnapshot
    val domains = listOf(
        "All",
        "Constitutional",
        "Appellate / SC",
        "Criminal",
        "Civil",
        "Dawn Editorial",
        "Custom"
    )

    val matchedEntries = remember(
        snapshot.entries,
        uiState.jsonLookupQuery,
        uiState.jsonDomainFilter
    ) {
        PitmanJsonDatabaseSerializer.quickLookup(
            entries = snapshot.entries,
            query = uiState.jsonLookupQuery,
            domainFilter = uiState.jsonDomainFilter
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 680.dp)
            .testTag("json_lexicon_lazy_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SymbolsSubModeSwitcher(
                activeMode = uiState.symbolsSubMode,
                onModeSelected = onSwitchSubMode
            )
        }

        // JSON Database Metadata & Quick Actions Card
        item {
            Surface(
                color = extendedColors.surfaceContainerLowest,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("json_db_status_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
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
                                imageVector = Icons.Outlined.Storage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Column {
                                Text(
                                    text = stringResource(R.string.json_lexicon_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = SourceSerifFontFamily,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${snapshot.storageFileName} • Schema v${snapshot.metadata.schemaVersion}",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${snapshot.entries.size} Records",
                                fontFamily = JetBrainsMonoFontFamily,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.json_lexicon_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("add_json_lexicon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add Outline (${snapshot.customEntryCount})",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        if (snapshot.customEntryCount > 0) {
                            OutlinedButton(
                                onClick = onResetSeed,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("reset_json_lexicon_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.RestartAlt,
                                    contentDescription = "Reset JSON lexicon to seed",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Lookup Input
        item {
            OutlinedTextField(
                value = uiState.jsonLookupQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("Lookup legal term, Pitman formula (P-T), or phonetic key...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.jsonLookupQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Clear lookup")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("json_lookup_search_input")
            )
        }

        // Domain Filter Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                domains.forEach { domain ->
                    val selected = uiState.jsonDomainFilter == domain
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
                            .clickable { onDomainFilterChange(domain) }
                            .testTag("json_domain_$domain")
                    ) {
                        Text(
                            text = domain,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onSecondary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }

        if (matchedEntries.isEmpty()) {
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
                            text = "No matching outlines in JSON DB",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Try a different term or add your own custom Pitman outline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(matchedEntries, key = { it.id }) { entry ->
                JsonLexiconEntryCard(
                    entry = entry,
                    onInspect = { onInspectSpec(entry.toStrokeSpec()) },
                    onSpeak = { onPronounceTerm(entry.term) },
                    onDelete = { onDeleteEntry(entry.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddLexiconEntryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { term, formula, pos, cat, domain, principle, note, def, glyph ->
                onAddCustomEntry(term, formula, pos, cat, domain, principle, note, def, glyph)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SymbolsSubModeSwitcher(
    activeMode: SymbolsSubMode,
    onModeSelected: (SymbolsSubMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    Surface(
        color = extendedColors.surfaceContainerHigh,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SymbolsSubMode.entries.forEach { mode ->
                val selected = activeMode == mode
                Surface(
                    color = if (selected) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        extendedColors.surfaceContainerHigh
                    },
                    shape = RoundedCornerShape(9.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { onModeSelected(mode) }
                        .testTag("symbols_mode_${mode.name}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mode.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
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
}

@Composable
private fun JsonLexiconEntryCard(
    entry: PitmanJsonLexiconEntry,
    onInspect: () -> Unit,
    onSpeak: () -> Unit,
    onDelete: () -> Unit
) {
    val extendedColors = LocalStenoColors.current
    val spec = remember(entry) { entry.toStrokeSpec() }

    Surface(
        color = extendedColors.surfaceContainerLowest,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (entry.isUserCustom) {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onInspect)
            .testTag("json_entry_${entry.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PitmanStemCanvas(
                spec = spec,
                modifier = Modifier
                    .width(96.dp)
                    .height(68.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.term,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = if (entry.isUserCustom) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            extendedColors.surfaceContainerHigh
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (entry.isUserCustom) "CUSTOM" else entry.position.badge,
                            fontFamily = JetBrainsMonoFontFamily,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (entry.isUserCustom) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "${entry.strokeFormula} • /${entry.phoneticKey}/",
                    fontFamily = JetBrainsMonoFontFamily,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = entry.legalDefinition,
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
                    Text(
                        text = "${entry.courtDomain} • ${entry.category.label}",
                        fontFamily = JetBrainsMonoFontFamily,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier.testTag("json_speak_${entry.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VolumeUp,
                                contentDescription = "Pronounce ${entry.term}",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (entry.isUserCustom) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.testTag("json_delete_${entry.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "Delete custom outline ${entry.term}",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddLexiconEntryDialog(
    onDismiss: () -> Unit,
    onSave: (
        term: String,
        strokeFormula: String,
        position: StenoPosition,
        category: StenoCategory,
        courtDomain: String,
        theoryPrinciple: String,
        ruleNote: String,
        legalDefinition: String,
        glyphKey: String
    ) -> Unit
) {
    var term by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }
    var legalDefinition by remember { mutableStateOf("") }
    var courtDomain by remember { mutableStateOf("Constitutional") }
    var selectedPosition by remember { mutableStateOf(StenoPosition.SECOND) }
    var selectedCategory by remember { mutableStateOf(StenoCategory.CONTRACTION) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Pitman Outline to JSON DB",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = SourceSerifFontFamily
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = term,
                    onValueChange = { term = it },
                    label = { Text("Legal Term or Phrase") },
                    placeholder = { Text("e.g. Writ of Certiorari") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_lex_term_input")
                )
                OutlinedTextField(
                    value = formula,
                    onValueChange = { formula = it },
                    label = { Text("Pitman Stroke Formula") },
                    placeholder = { Text("e.g. R-T + S-R-Sh-R-R") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_lex_formula_input")
                )
                OutlinedTextField(
                    value = legalDefinition,
                    onValueChange = { legalDefinition = it },
                    label = { Text("Legal Definition / Usage Note") },
                    placeholder = { Text("Order quashing an unlawful decision") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_lex_definition_input")
                )

                Text(
                    text = "Ruled Line Position",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StenoPosition.entries.forEach { pos ->
                        FilterChip(
                            selected = selectedPosition == pos,
                            onClick = { selectedPosition = pos },
                            label = { Text(pos.badge) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    }
                }

                Text(
                    text = "Pitman Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StenoCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = term.isNotBlank(),
                onClick = {
                    val glyph = when (selectedCategory) {
                        StenoCategory.INTERSECTION -> "learned_counsel"
                        StenoCategory.PHRASING -> "natural_justice"
                        StenoCategory.GRAMMALOGUE -> "gram_should"
                        StenoCategory.HALVING -> "dawn_reform"
                        StenoCategory.CONTRACTION -> "jurisdiction"
                    }
                    onSave(
                        term,
                        formula,
                        selectedPosition,
                        selectedCategory,
                        courtDomain,
                        "${selectedCategory.label} • ${selectedPosition.badge}",
                        "Saved locally in pitman_legal_db.json for rapid lookup.",
                        legalDefinition,
                        glyph
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.testTag("save_lex_entry_button")
            ) {
                Text("Save to JSON DB")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

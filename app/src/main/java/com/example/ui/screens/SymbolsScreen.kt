package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.model.StenoCategory
import com.example.domain.model.StenoPosition
import com.example.domain.model.StenoStrokeSpec
import com.example.domain.model.SymbolExerciseGroup
import com.example.domain.model.SymbolsSubMode
import com.example.domain.pitman.PitmanSymbolCatalog
import com.example.ui.components.PitmanStemCanvas
import com.example.ui.theme.LocalStenoColors
import com.example.ui.viewmodel.StenoUiState

/** A clean vocabulary list showing only the Pitman outline and its English word. */
@Composable
fun SymbolsScreen(
    uiState: StenoUiState,
    onSelectGroup: (SymbolExerciseGroup) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onInspectSymbol: (StenoStrokeSpec) -> Unit = {},
    onPronounceTerm: (String) -> Unit = {},
    onSelectSubMode: (SymbolsSubMode) -> Unit = {},
    onJsonQueryChange: (String) -> Unit = {},
    onJsonDomainFilterChange: (String) -> Unit = {},
    onAddJsonLexiconEntry: (
        term: String, strokeFormula: String, position: StenoPosition,
        category: StenoCategory, courtDomain: String, theoryPrinciple: String,
        ruleNote: String, legalDefinition: String, glyphKey: String
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onDeleteJsonLexiconEntry: (String) -> Unit = {},
    onResetJsonLexiconSeed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val vocabulary = PitmanSymbolCatalog.entries.filter {
        it.group == SymbolExerciseGroup.DAWN_EDITORIAL ||
            it.group == SymbolExerciseGroup.LEGAL_OUTLINES
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("symbols_vocabulary_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(vocabulary, key = { it.id }) { entry ->
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("vocabulary_entry_${entry.id}"),
                color = LocalStenoColors.current.surfaceContainerLowest,
                shape = MaterialTheme.shapes.large,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.spec.term,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    PitmanStemCanvas(
                        spec = entry.spec,
                        modifier = Modifier.fillMaxWidth().height(76.dp)
                    )
                }
            }
        }
    }
}

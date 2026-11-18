package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PakistanLegalCorpus
import com.example.domain.pitman.PitmanAnalyzer
import com.example.ui.components.PitmanStemCanvas
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalStenoColors
import com.example.ui.theme.SourceSerifFontFamily
import com.example.ui.viewmodel.StenoUiState

@Composable
fun DraftScreen(
    uiState: StenoUiState,
    onCitationChange: (String) -> Unit,
    onCourtChange: (String) -> Unit,
    onDraftTextChange: (String) -> Unit,
    onSelectTemplate: (String, String) -> Unit,
    onVoiceDictateClick: () -> Unit,
    onApplyAndDictate: () -> Unit,
    onApplyOnly: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    val liveAnalysis = remember(uiState.customText) {
        PitmanAnalyzer.analyze(uiState.customText)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 680.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Custom Passage Creator",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = SourceSerifFontFamily,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Draft appellate orders, petitions, or courtroom speeches for real-time Pitman outline parsing and WPM-calibrated dictation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Quick Legal Templates
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Quick Appellate Templates",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PakistanLegalCorpus.draftTemplates.forEach { (title, body) ->
                        Surface(
                            color = extendedColors.surfaceContainerHigh,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectTemplate(title, body) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Metadata Inputs
        item {
            Surface(
                color = extendedColors.surfaceContainerLowest,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.customCitation,
                            onValueChange = onCitationChange,
                            label = { Text("Citation / Reference") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("draft_citation_input")
                        )
                        OutlinedTextField(
                            value = uiState.customCourt,
                            onValueChange = onCourtChange,
                            label = { Text("Court / Forum") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("draft_court_input")
                        )
                    }

                    OutlinedTextField(
                        value = uiState.customText,
                        onValueChange = onDraftTextChange,
                        label = { Text("Judgment / Passage Text") },
                        placeholder = { Text("Paste or type legal dictation text here...") },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        minLines = 6,
                        maxLines = 12,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("draft_text_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onVoiceDictateClick,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Voice Input")
                        }

                        OutlinedButton(
                            onClick = { onDraftTextChange("") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = "Clear draft",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear")
                        }
                    }
                }
            }
        }

        // Live Pitman Preview Metrics
        item {
            Surface(
                color = extendedColors.surfaceContainerLow,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
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
                        Text(
                            text = "Live Pitman Steno Telemetry",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${liveAnalysis.wordCount} Words • ${liveAnalysis.secondsAt100Wpm}s @ 100 WPM",
                            fontFamily = JetBrainsMonoFontFamily,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DraftStatChip(
                            label = "Grammalogues",
                            value = liveAnalysis.grammalogueCount.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        DraftStatChip(
                            label = "Contractions",
                            value = liveAnalysis.legalContractionCount.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        DraftStatChip(
                            label = "Intersections",
                            value = liveAnalysis.intersectionCount.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (liveAnalysis.featuredStems.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            liveAnalysis.featuredStems.take(2).forEach { spec ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = spec.term,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    PitmanStemCanvas(
                                        spec = spec,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(64.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onApplyAndDictate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("apply_and_dictate_button")
                ) {
                    Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Load & Dictate @ ${uiState.prefs.targetWpm} WPM",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = onApplyOnly,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_draft_button")
                ) {
                    Text(
                        text = "Inspect Stems in Dictation Studio",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun DraftStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    Surface(
        color = extendedColors.surfaceContainerLowest,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = SourceSerifFontFamily,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

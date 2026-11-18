package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.AudioSourceType
import com.example.domain.model.DictationMode
import com.example.domain.model.PassageHighlight
import com.example.domain.model.PassageTab
import com.example.domain.model.StenoStrokeSpec
import com.example.domain.model.VoiceCadenceProfile
import com.example.ui.components.AcousticDictationDeck
import com.example.ui.components.InteractivePassageText
import com.example.ui.components.PassageTheoryTooltip
import com.example.ui.components.PitmanStemCanvas
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalStenoColors
import com.example.ui.theme.SourceSerifFontFamily
import com.example.ui.viewmodel.StenoUiState
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DictateScreen(
    uiState: StenoUiState,
    onModeSelected: (DictationMode) -> Unit,
    onTabSelected: (PassageTab) -> Unit,
    onHighlightClicked: (PassageHighlight) -> Unit,
    onDismissTooltip: () -> Unit,
    onPronounceTerm: (String) -> Unit,
    onExpandTooltipInspector: () -> Unit,
    onStemClicked: (StenoStrokeSpec) -> Unit,
    onKaraokeToggle: (Boolean) -> Unit,
    onTtsPlayPause: () -> Unit,
    onTtsRestart: () -> Unit,
    onWpmStep: (Int) -> Unit,
    onSetTargetWpm: (Int) -> Unit,
    onSelectAudioSource: (AudioSourceType) -> Unit,
    onSelectCadenceProfile: (VoiceCadenceProfile) -> Unit,
    onUnifiedPlayerPlayPause: () -> Unit,
    onSeekSeconds: (Int) -> Unit,
    onTogglePitchPreserve: () -> Unit,
    onToggleFiveSecondCadenceTest: () -> Unit,
    onToggleQuarterMinuteChime: (Boolean) -> Unit,
    onToggleAmbientMasking: (Boolean) -> Unit,
    onToggleSpeedProgression: (Boolean) -> Unit,
    onGenerateDictationExercise: () -> Unit,
    onExportPdf: () -> Unit,
    onOpenCustomEditor: () -> Unit,
    onPickAudioClick: () -> Unit,
    onSynthesizeAudioClick: () -> Unit,
    onCourtAudioPlayPause: () -> Unit,
    onCourtAudioSeek: (Int) -> Unit,
    onCourtAudioSpeed: (Float) -> Unit,
    onBookmarkToggle: () -> Unit,
    onOpenBookmarksSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    val passage = uiState.activePassage
    val analysis = uiState.analysis
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_dot")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Determine currently active word string for Karaoke Stem Sync
    val activeSpokenText = uiState.ttsState.activeRange?.let { range ->
        val safeStart = range.first.coerceIn(0, passage.text.length)
        val safeEnd = (range.last + 1).coerceIn(safeStart, passage.text.length)
        if (safeStart < safeEnd) passage.text.substring(safeStart, safeEnd).lowercase() else ""
    }.orEmpty()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .align(Alignment.TopCenter)
                .testTag("dictate_lazy_list"),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 104.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Classification & Hero Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = extendedColors.surfaceContainerHigh,
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .alpha(pulseAlpha)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary)
                                )
                                Text(
                                    text = stringResource(R.string.corpus_classification),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            color = extendedColors.surfaceContainerLow,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Gavel,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = stringResource(R.string.judicial_cadence_badge),
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = if (uiState.bookmarkedPassages.isNotEmpty()) {
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                            } else {
                                extendedColors.surfaceContainerLow
                            },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable(onClick = onOpenBookmarksSheet)
                                .testTag("open_bookmarks_chip")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.bookmarkedPassages.isNotEmpty()) {
                                        Icons.Rounded.Bookmark
                                    } else {
                                        Icons.Outlined.BookmarkBorder
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Saved (${uiState.bookmarkedPassages.size})",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.hero_title),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 27.sp,
                            lineHeight = 35.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(R.string.hero_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Quick Switch Mode Segmented Bar
                    Surface(
                        color = extendedColors.surfaceContainerHigh,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ModeSegmentChip(
                                selected = uiState.dictationMode == DictationMode.DRAFT_TTS,
                                icon = Icons.Outlined.Article,
                                label = stringResource(R.string.mode_draft_tts),
                                onClick = { onModeSelected(DictationMode.DRAFT_TTS) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mode_draft_tts")
                            )
                            ModeSegmentChip(
                                selected = uiState.dictationMode == DictationMode.COURT_AUDIO,
                                icon = Icons.Outlined.AudioFile,
                                label = stringResource(R.string.mode_upload_audio),
                                onClick = { onModeSelected(DictationMode.COURT_AUDIO) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mode_court_audio")
                            )
                        }
                    }
                }
            }

            // 2. Court Audio Studio Card (visible when Upload Court Audio is selected)
            item {
                AnimatedVisibility(
                    visible = uiState.dictationMode == DictationMode.COURT_AUDIO,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    CourtAudioStudioCard(
                        uiState = uiState,
                        onPickAudioClick = onPickAudioClick,
                        onSynthesizeAudioClick = onSynthesizeAudioClick,
                        onPlayPause = onCourtAudioPlayPause,
                        onSeek = onCourtAudioSeek,
                        onSpeedSelected = onCourtAudioSpeed
                    )
                }
            }

            // 3. Pakistan Legal Case Excerpt Card
            item {
                Surface(
                    color = extendedColors.surfaceContainerLowest,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Case Banner & Verified Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = passage.citation,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "•",
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                    Text(
                                        text = passage.court,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = passage.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    color = if (passage.isVerified) {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    } else {
                                        extendedColors.surfaceContainerHigh
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (passage.isVerified) Icons.Outlined.Verified else Icons.Outlined.EditNote,
                                            contentDescription = null,
                                            tint = if (passage.isVerified) {
                                                MaterialTheme.colorScheme.onSecondaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.secondary
                                            },
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (passage.isVerified) {
                                                stringResource(R.string.verified_law_report)
                                            } else {
                                                "Custom Draft"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (passage.isVerified) {
                                                MaterialTheme.colorScheme.onSecondaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.secondary
                                            },
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = onBookmarkToggle,
                                    modifier = Modifier.testTag("bookmark_active_passage_button")
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isActivePassageBookmarked) {
                                            Icons.Rounded.Bookmark
                                        } else {
                                            Icons.Outlined.BookmarkBorder
                                        },
                                        contentDescription = if (uiState.isActivePassageBookmarked) {
                                            "Remove passage bookmark"
                                        } else {
                                            "Bookmark passage for practice"
                                        },
                                        tint = if (uiState.isActivePassageBookmarked) {
                                            MaterialTheme.colorScheme.secondary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }

                        // Segment Tabs (Text / Brief Summary / Outline Analysis)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CaseSegmentTab(
                                    label = stringResource(R.string.tab_text_active),
                                    selected = uiState.activeTab == PassageTab.TEXT,
                                    onClick = { onTabSelected(PassageTab.TEXT) },
                                    modifier = Modifier.testTag("tab_text")
                                )
                                CaseSegmentTab(
                                    label = stringResource(R.string.tab_brief_summary),
                                    selected = uiState.activeTab == PassageTab.BRIEF_SUMMARY,
                                    onClick = { onTabSelected(PassageTab.BRIEF_SUMMARY) },
                                    modifier = Modifier.testTag("tab_summary")
                                )
                                CaseSegmentTab(
                                    label = stringResource(R.string.tab_outline_analysis),
                                    selected = uiState.activeTab == PassageTab.OUTLINE_ANALYSIS,
                                    onClick = { onTabSelected(PassageTab.OUTLINE_ANALYSIS) },
                                    modifier = Modifier.testTag("tab_outline")
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }

                        // Tab Body
                        when (uiState.activeTab) {
                            PassageTab.TEXT -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(
                                        color = extendedColors.surfaceContainerLow,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            InteractivePassageText(
                                                text = passage.text,
                                                highlights = analysis.highlights,
                                                selectedHighlight = uiState.activeTooltipHighlight,
                                                activeKaraokeRange = uiState.ttsState.activeRange,
                                                karaokeEnabled = uiState.prefs.karaokeSync,
                                                onHighlightClick = onHighlightClicked,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("passage_body_text")
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Tap highlighted terms for Pitman theory & definition",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                                Text(
                                                    text = "Edit Draft",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .clickable(onClick = onOpenCustomEditor)
                                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    AnimatedVisibility(
                                        visible = uiState.activeTooltipHighlight != null,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        uiState.activeTooltipHighlight?.let { activeHighlight ->
                                            PassageTheoryTooltip(
                                                highlight = activeHighlight,
                                                onPronounce = {
                                                    onPronounceTerm(activeHighlight.matchedText)
                                                },
                                                onExpandFullInspector = onExpandTooltipInspector,
                                                onDismiss = onDismissTooltip
                                            )
                                        }
                                    }
                                }
                            }

                            PassageTab.BRIEF_SUMMARY -> {
                                Surface(
                                    color = extendedColors.surfaceContainerLow,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "Ratio Decidendi & Legal Context",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = passage.summary,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )
                                        passage.keyPrinciples.forEachIndexed { idx, principle ->
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text(
                                                    text = "0${idx + 1}",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = principle,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            PassageTab.OUTLINE_ANALYSIS -> {
                                Surface(
                                    color = extendedColors.surfaceContainerLow,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        analysis.allDetectedOutlines.forEach { spec ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(extendedColors.surfaceContainerLowest)
                                                    .clickable { onStemClicked(spec) }
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = spec.term,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = "${spec.strokeFormula} • ${spec.category.label}",
                                                        fontFamily = JetBrainsMonoFontFamily,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.secondary
                                                    )
                                                }
                                                Surface(
                                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = spec.position.badge,
                                                        fontFamily = JetBrainsMonoFontFamily,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Passage Metrics Bar
                        val targetSeconds = max(
                            1,
                            ceil(analysis.wordCount * 60.0 / uiState.prefs.targetWpm).toInt()
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.FormatListNumbered,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "${analysis.wordCount} Words",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text("•", color = MaterialTheme.colorScheme.outlineVariant)
                                Text(
                                    text = "${analysis.charCount} Chars",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text("•", color = MaterialTheme.colorScheme.outlineVariant)
                                Text(
                                    text = "${targetSeconds}s @ ${uiState.prefs.targetWpm} WPM",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text("•", color = MaterialTheme.colorScheme.outlineVariant)
                                Text(
                                    text = "${analysis.secondsAt120Wpm}s @ 120 WPM",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FolderSpecial,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = stringResource(R.string.legal_archive_footer),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 4. Automated Pitman Stenographic Architecture
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Analytics,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(R.string.steno_architecture_title),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            color = extendedColors.surfaceContainerHigh,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.lexicon_version_badge),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 3 Metric Tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ArchitectureMetricTile(
                            count = analysis.grammalogueCount,
                            label = stringResource(R.string.metric_grammalogues),
                            sample = analysis.grammalogueSamples.joinToString(", ").ifEmpty { "the, of, is, it..." },
                            modifier = Modifier.weight(1f)
                        )
                        ArchitectureMetricTile(
                            count = analysis.legalContractionCount,
                            label = stringResource(R.string.metric_legal_contractions),
                            sample = analysis.contractionSamples.joinToString(", ").ifEmpty { "petitioner, jurisdiction" },
                            modifier = Modifier.weight(1f)
                        )
                        ArchitectureMetricTile(
                            count = analysis.intersectionCount,
                            label = stringResource(R.string.metric_intersections),
                            sample = analysis.intersectionSamples.joinToString(", ").ifEmpty { "learned counsel, court" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Stem & Rule Breakdown Badges
                    Surface(
                        color = extendedColors.surfaceContainerLowest,
                        shape = RoundedCornerShape(14.dp),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FlowRow(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = extendedColors.surfaceContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.secondary)
                                    )
                                    Text(
                                        text = analysis.complexityLabel,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                color = extendedColors.surfaceContainerLow,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Halved & Doubled: ${analysis.halvedDoubledCount} Instances (-t/-d, -ter/-der)",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Special Intersections: P-T (Petitioner), H-K (High Court)",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Ruled Pitman Steno Stems (2x2 Grid)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.ruled_stems_title),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.ruled_stems_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onKaraokeToggle(!uiState.prefs.karaokeSync) }
                                .padding(end = 6.dp)
                                .testTag("karaoke_sync_toggle")
                        ) {
                            Checkbox(
                                checked = uiState.prefs.karaokeSync,
                                onCheckedChange = onKaraokeToggle,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                            Text(
                                text = stringResource(R.string.karaoke_sync),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    val stemRows = analysis.featuredStems.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        stemRows.forEach { rowSpecs ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowSpecs.forEach { spec ->
                                    val isStemHighlighted = uiState.prefs.karaokeSync &&
                                        activeSpokenText.isNotEmpty() &&
                                        spec.term.lowercase().contains(
                                            activeSpokenText.trim('.', ',', ';', ':', '"', '\'')
                                        )
                                    StemWellCard(
                                        spec = spec,
                                        isHighlighted = isStemHighlighted,
                                        onClick = { onStemClicked(spec) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowSpecs.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // 6. Acoustic Dictation Engine, WPM Speed Controls & Live Cadence Benchmarking
            item {
                AcousticDictationDeck(
                    uiState = uiState,
                    onSelectAudioSource = onSelectAudioSource,
                    onSelectCadenceProfile = onSelectCadenceProfile,
                    onPlayerPlayPause = onUnifiedPlayerPlayPause,
                    onSeekSeconds = onSeekSeconds,
                    onTogglePitchPreserve = onTogglePitchPreserve,
                    onSetTargetWpm = onSetTargetWpm,
                    onToggleFiveSecondCadenceTest = onToggleFiveSecondCadenceTest,
                    onToggleQuarterMinuteChime = onToggleQuarterMinuteChime,
                    onToggleAmbientMasking = onToggleAmbientMasking,
                    onToggleSpeedProgression = onToggleSpeedProgression,
                    onGenerateDictationExercise = onGenerateDictationExercise,
                    onToggleBookmark = onBookmarkToggle,
                    onExportPdf = onExportPdf
                )
            }
        }

        // Docked TTS Dictation Transport Bar (floats cleanly above NavigationBar)
        if (uiState.dictationMode == DictationMode.DRAFT_TTS) {
            DockedDictationBar(
                isSpeaking = uiState.ttsState.isSpeaking,
                isPaused = uiState.ttsState.isPaused,
                targetWpm = uiState.prefs.targetWpm,
                onPlayPause = onTtsPlayPause,
                onRestart = onTtsRestart,
                onDecreaseWpm = { onWpmStep(-10) },
                onIncreaseWpm = { onWpmStep(10) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .widthIn(max = 680.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ModeSegmentChip(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    Surface(
        color = if (selected) extendedColors.surfaceContainerLowest else androidx.compose.ui.graphics.Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = if (selected) 1.dp else 0.dp,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CaseSegmentTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) extendedColors.surfaceContainer
                else androidx.compose.ui.graphics.Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ArchitectureMetricTile(
    count: Int,
    label: String,
    sample: String,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    Surface(
        color = extendedColors.surfaceContainerLowest,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = count.toString(),
                fontFamily = SourceSerifFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = sample,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StemWellCard(
    spec: StenoStrokeSpec,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    Surface(
        color = extendedColors.surfaceContainerLowest,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = if (isHighlighted) 3.dp else 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("stem_card_${spec.glyphKey}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = spec.term,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = spec.position.badge,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            PitmanStemCanvas(
                spec = spec,
                isKaraokeHighlighted = isHighlighted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = spec.strokeFormula,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = spec.category.label,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun DockedDictationBar(
    isSpeaking: Boolean,
    isPaused: Boolean,
    targetWpm: Int,
    onPlayPause: () -> Unit,
    onRestart: () -> Unit,
    onDecreaseWpm: () -> Unit,
    onIncreaseWpm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Play/Pause + Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onPlayPause)
                        .testTag("tts_play_pause_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isSpeaking) "Pause dictation" else "Start dictation",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }

                Column {
                    Text(
                        text = when {
                            isSpeaking -> "Dictating Live"
                            isPaused -> "Dictation Paused"
                            else -> "TTS Dictation Ready"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Calibrated Judicial Cadence",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1
                    )
                }
            }

            // Right: WPM Stepper + Restart
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onDecreaseWpm,
                    modifier = Modifier.testTag("wpm_decrease_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "Decrease WPM",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = "$targetWpm WPM",
                    fontFamily = JetBrainsMonoFontFamily,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
                IconButton(
                    onClick = onIncreaseWpm,
                    modifier = Modifier.testTag("wpm_increase_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Increase WPM",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(
                    onClick = onRestart,
                    modifier = Modifier.testTag("tts_restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Replay,
                        contentDescription = "Restart dictation",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CourtAudioStudioCard(
    uiState: StenoUiState,
    onPickAudioClick: () -> Unit,
    onSynthesizeAudioClick: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedSelected: (Float) -> Unit
) {
    val extendedColors = LocalStenoColors.current
    val audio = uiState.audioState
    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)

    Surface(
        color = extendedColors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Courtroom Audio Playback",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = audio.sourceLabel ?: "Load an oral hearing recording or generate a practice track",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPickAudioClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("pick_audio_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick Audio", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = onSynthesizeAudioClick,
                    enabled = !uiState.isExportingAudio,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("export_wav_button")
                ) {
                    if (uiState.isExportingAudio) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Render WAV", style = MaterialTheme.typography.labelLarge)
                }
            }

            if (audio.isLoaded) {
                Surface(
                    color = extendedColors.surfaceContainerLow,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .clickable(onClick = onPlayPause)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (audio.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                        contentDescription = "Play or pause court audio",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Slider(
                                    value = audio.positionMs.toFloat(),
                                    onValueChange = { onSeek(it.toInt()) },
                                    valueRange = 0f..(audio.durationMs.coerceAtLeast(1).toFloat()),
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.secondary,
                                        activeTrackColor = MaterialTheme.colorScheme.secondary
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = formatDurationMs(audio.positionMs),
                                        fontFamily = JetBrainsMonoFontFamily,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = formatDurationMs(audio.durationMs),
                                        fontFamily = JetBrainsMonoFontFamily,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            speeds.forEach { spd ->
                                val active = audio.playbackSpeed == spd
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (active) MaterialTheme.colorScheme.secondaryContainer
                                            else extendedColors.surfaceContainerLowest
                                        )
                                        .clickable { onSpeedSelected(spd) }
                                        .padding(vertical = 7.dp)
                                ) {
                                    Text(
                                        text = "${spd}x",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                        color = if (active) MaterialTheme.colorScheme.onSecondaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDurationMs(ms: Int): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val mins = totalSec / 60
    val secs = totalSec % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}

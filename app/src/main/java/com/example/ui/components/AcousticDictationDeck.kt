package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.NoiseAware
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.Forward5
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Replay5
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.CadenceTestStatus
import com.example.domain.model.AudioSourceType
import com.example.domain.model.DictationMode
import com.example.domain.model.VoiceCadenceProfile
import com.example.domain.model.WpmBenchmarkUtils
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalStenoColors
import com.example.ui.theme.SourceSerifFontFamily
import com.example.ui.viewmodel.StenoUiState
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AcousticDictationDeck(
    uiState: StenoUiState,
    onSelectAudioSource: (AudioSourceType) -> Unit,
    onSelectCadenceProfile: (VoiceCadenceProfile) -> Unit,
    onPlayerPlayPause: () -> Unit,
    onSeekSeconds: (Int) -> Unit,
    onTogglePitchPreserve: () -> Unit,
    onSetTargetWpm: (Int) -> Unit,
    onToggleFiveSecondCadenceTest: () -> Unit,
    onToggleQuarterMinuteChime: (Boolean) -> Unit,
    onToggleAmbientMasking: (Boolean) -> Unit,
    onToggleSpeedProgression: (Boolean) -> Unit,
    onGenerateDictationExercise: () -> Unit,
    onToggleBookmark: () -> Unit,
    onExportPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    val prefs = uiState.prefs
    val targetWpm = prefs.targetWpm
    val wordCount = uiState.analysis.wordCount.coerceAtLeast(1)
    val totalSeconds = max(1, ceil(wordCount * 60.0 / targetWpm).toInt())

    val isAudioModeActive = uiState.dictationMode == DictationMode.COURT_AUDIO && uiState.audioState.isLoaded
    val isPlayingNow = if (isAudioModeActive) uiState.audioState.isPlaying else uiState.ttsState.isSpeaking

    val progressFraction = if (isAudioModeActive) {
        val dur = uiState.audioState.durationMs.coerceAtLeast(1)
        (uiState.audioState.positionMs.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
    } else {
        uiState.ttsState.progressFraction.coerceIn(0f, 1f)
    }

    val elapsedSeconds = if (isAudioModeActive) {
        (uiState.audioState.positionMs / 1000).coerceAtLeast(0)
    } else {
        (totalSeconds * progressFraction).roundToInt()
    }
    val displayTotalSeconds = if (isAudioModeActive) {
        (uiState.audioState.durationMs / 1000).coerceAtLeast(1)
    } else {
        totalSeconds
    }

    val trackFileName = if (isAudioModeActive && !uiState.audioState.sourceLabel.isNullOrBlank()) {
        uiState.audioState.sourceLabel
    } else {
        "${uiState.activePassage.id}_audiomaster.mp3"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Acoustic Dictation Engine Card
        Surface(
            color = extendedColors.surfaceContainerLowest,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.acoustic_engine_title),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.acoustic_engine_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (isPlayingNow) "DICTATING" else stringResource(R.string.live_dictator_badge),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }

                // 1. Audio Source Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "AUDIO SOURCE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        AudioSourceType.entries.forEach { source ->
                            val selected = prefs.audioSource == source
                            Surface(
                                color = if (selected) {
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                                } else {
                                    extendedColors.surfaceContainerLow
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectAudioSource(source) }
                                    .testTag("audio_source_${source.name}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        RadioButton(
                                            selected = selected,
                                            onClick = { onSelectAudioSource(source) },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                        Text(
                                            text = source.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = source.badge,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Voice Cadence Profile
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "VOICE CADENCE PROFILE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VoiceCadenceProfile.entries.forEach { profile ->
                            val selected = prefs.cadenceProfile == profile
                            Surface(
                                color = if (selected) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    extendedColors.surfaceContainerLow
                                },
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = if (selected) 1.dp else 0.dp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectCadenceProfile(profile) }
                                    .testTag("cadence_profile_${profile.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    if (profile == VoiceCadenceProfile.COMMONWEALTH_PAKISTAN) {
                                        Icon(
                                            imageVector = Icons.Outlined.AccountBalance,
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
                                        text = profile.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
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

                // 3. Waveform Visualizer & Audio Player Track
                Surface(
                    color = extendedColors.surfaceContainerHigh,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Icon(
                                    imageVector = Icons.Rounded.GraphicEq,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = trackFileName,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${WpmBenchmarkUtils.formatClockDuration(elapsedSeconds)} / ${WpmBenchmarkUtils.formatClockDuration(displayTotalSeconds)}",
                                fontFamily = JetBrainsMonoFontFamily,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("audio_elapsed_counter")
                            )
                        }

                        // Acoustic Soundwave Spectrum
                        AcousticSoundwaveSpectrum(
                            progressFraction = progressFraction,
                            isAnimating = isPlayingNow || uiState.cadenceState.testStatus == CadenceTestStatus.TESTING
                        )

                        // Player Transport Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = extendedColors.surfaceContainerLowest,
                                    shape = CircleShape,
                                    shadowElevation = 1.dp,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .clickable { onSeekSeconds(-5) }
                                        .testTag("audio_rewind_5s")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.Replay5,
                                            contentDescription = "Rewind 5 seconds",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape,
                                    shadowElevation = 3.dp,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .clickable(onClick = onPlayerPlayPause)
                                        .testTag("acoustic_play_pause_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isPlayingNow) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                            contentDescription = if (isPlayingNow) "Pause dictation" else "Play dictation",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }

                                Surface(
                                    color = extendedColors.surfaceContainerLowest,
                                    shape = CircleShape,
                                    shadowElevation = 1.dp,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .clickable { onSeekSeconds(5) }
                                        .testTag("audio_forward_5s")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.Forward5,
                                            contentDescription = "Forward 5 seconds",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = onTogglePitchPreserve)
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("pitch_preserve_toggle")
                            ) {
                                Text(
                                    text = if (prefs.pitchPreserve) "PITCH PRESERVE" else "NATURAL PITCH",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (prefs.pitchPreserve) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Toggle pitch preserve",
                                    tint = if (prefs.pitchPreserve) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 4. Dynamic WPM Speed Selector & Granular Controls
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                imageVector = Icons.Outlined.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.target_speed_label),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = WpmBenchmarkUtils.tierLabel(targetWpm),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.testTag("wpm_tier_label")
                        )
                    }

                    // 3x2 WPM Preset Grid (60 to 160 WPM)
                    val presetRows = WpmBenchmarkUtils.presetWpms.chunked(3)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        presetRows.forEach { rowWpms ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowWpms.forEach { preset ->
                                    val selected = targetWpm == preset
                                    val presetSeconds = max(1, ceil(wordCount * 60.0 / preset).toInt())
                                    val durationLabel = WpmBenchmarkUtils.formatCompactDuration(presetSeconds)
                                    val subLabel = if (preset == 100) "$durationLabel • Exam" else durationLabel

                                    Surface(
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.secondary
                                        } else {
                                            extendedColors.surfaceContainerLow
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        shadowElevation = if (selected) 2.dp else 0.dp,
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                            .border(
                                                width = if (selected) 2.dp else 0.dp,
                                                color = if (selected) {
                                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
                                                } else {
                                                    Color.Transparent
                                                },
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onSetTargetWpm(preset) }
                                            .testTag("speed_preset_$preset")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(1.dp)
                                        ) {
                                            Text(
                                                text = "$preset WPM",
                                                fontFamily = JetBrainsMonoFontFamily,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) {
                                                    MaterialTheme.colorScheme.onSecondary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurface
                                                }
                                            )
                                            Text(
                                                text = subLabel,
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontSize = 10.sp,
                                                color = if (selected) {
                                                    MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.9f)
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Granular Fine-Tune Slider Box
                    Surface(
                        color = extendedColors.surfaceContainerLow,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = targetWpm.toString(),
                                        fontFamily = SourceSerifFontFamily,
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.testTag("slider_wpm_display")
                                    )
                                    Text(
                                        text = "WPM Fine-Tune",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = extendedColors.surfaceContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = String.format(Locale.US, "%.2fx Pacing", targetWpm / 100f),
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${WpmBenchmarkUtils.formatCompactDuration(totalSeconds)} ($wordCount Words)",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "60",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Slider(
                                    value = targetWpm.toFloat(),
                                    onValueChange = { raw ->
                                        val snapped = ((raw / 5f).roundToInt() * 5).coerceIn(60, 180)
                                        onSetTargetWpm(snapped)
                                    },
                                    valueRange = 60f..180f,
                                    steps = 23,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.secondary,
                                        activeTrackColor = MaterialTheme.colorScheme.secondary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("speed_range_slider")
                                )
                                Text(
                                    text = "180",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 5. Live Cadence Metronome & Benchmark Deck
                Surface(
                    color = extendedColors.surfaceContainerLow,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = extendedColors.surfaceContainerHigh,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = stringResource(R.string.cadence_metronome_title),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = stringResource(R.string.cadence_metronome_subtitle),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "25-WORD INTERVALS",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Visual Metronome Rhythm Pulse Bar
                        MetronomeBenchmarkBar(
                            targetWpm = targetWpm,
                            currentWordIndex = uiState.ttsState.currentWordIndex,
                            totalWords = wordCount,
                            isTesting = uiState.cadenceState.testStatus == CadenceTestStatus.TESTING,
                            currentBeatIndex = uiState.cadenceState.currentBeatIndex
                        )

                        // Interactive 5-Sec Test Cadence Preview Button
                        TestCadencePreviewButton(
                            status = uiState.cadenceState.testStatus,
                            onClick = onToggleFiveSecondCadenceTest
                        )

                        // Official Pakistan Legal Benchmark Standards 2x2 Grid
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "OFFICIAL PAKISTAN LEGAL BENCHMARK STANDARDS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val benchmarks = listOf(
                                "Sessions Court:" to 80,
                                "High Court Exam:" to 100,
                                "Supreme Court Test:" to 120,
                                "National Assembly:" to 140
                            )
                            benchmarks.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    pair.forEach { (label, wpmVal) ->
                                        val isActiveBenchmark = targetWpm == wpmVal
                                        Surface(
                                            color = if (isActiveBenchmark) {
                                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                                            } else {
                                                extendedColors.surfaceContainerLowest
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(
                                                    width = if (isActiveBenchmark) 1.dp else 0.dp,
                                                    color = if (isActiveBenchmark) {
                                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)
                                                    } else {
                                                        Color.Transparent
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onSetTargetWpm(wpmVal) }
                                                .testTag("benchmark_card_$wpmVal")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = if (isActiveBenchmark) FontWeight.SemiBold else FontWeight.Normal,
                                                    color = if (isActiveBenchmark) {
                                                        MaterialTheme.colorScheme.secondary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    },
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "$wpmVal WPM",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isActiveBenchmark) {
                                                        MaterialTheme.colorScheme.secondary
                                                    } else {
                                                        MaterialTheme.colorScheme.primary
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Stenographer Audio Tools Toggles
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AudioToolCheckboxRow(
                        icon = Icons.Outlined.NotificationsActive,
                        title = "Quarter-Minute Acoustic Chime",
                        subtitle = "Beeps every 25 words to benchmark shorthand line rate",
                        checked = prefs.quarterMinuteChime,
                        onCheckedChange = onToggleQuarterMinuteChime,
                        testTag = "toggle_quarter_minute_chime"
                    )
                    AudioToolCheckboxRow(
                        icon = Icons.Outlined.NoiseAware,
                        title = "Courtroom Ambient Masking",
                        subtitle = "Simulates gavel taps, gallery murmur, and fan hum for exam readiness",
                        checked = prefs.courtroomAmbientMasking,
                        onCheckedChange = onToggleAmbientMasking,
                        testTag = "toggle_ambient_masking"
                    )
                    Surface(
                        color = extendedColors.surfaceContainerLow,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleSpeedProgression(!prefs.speedProgressionLadder) }
                            .testTag("toggle_speed_progression")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.TrendingUp,
                                    contentDescription = null,
                                    tint = if (prefs.speedProgressionLadder) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(19.dp)
                                )
                                Column {
                                    Text(
                                        text = "Speed Progression Ladder",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Pyramid training mode (+10 WPM every 60 seconds)",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (prefs.speedProgressionLadder) {
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                } else {
                                    extendedColors.surfaceContainerHigh
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (prefs.speedProgressionLadder) "Pyramid Active" else "Static Pace",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (prefs.speedProgressionLadder) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Primary Action Bar & Quick Actions
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onGenerateDictationExercise,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_dictation_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.RocketLaunch,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Dictation Exercise (@ $targetWpm WPM)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = if (uiState.isActivePassageBookmarked) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    } else {
                        extendedColors.surfaceContainerLowest
                    },
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onToggleBookmark)
                        .testTag("save_legal_library_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (uiState.isActivePassageBookmarked) {
                                Icons.Rounded.BookmarkAdded
                            } else {
                                Icons.Outlined.BookmarkAdd
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isActivePassageBookmarked) {
                                stringResource(R.string.saved_in_legal_library)
                            } else {
                                stringResource(R.string.save_to_legal_library)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color = extendedColors.surfaceContainerLowest,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onExportPdf)
                        .testTag("export_steno_pdf_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PictureAsPdf,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.export_steno_pdf),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AcousticSoundwaveSpectrum(
    progressFraction: Float,
    isAnimating: Boolean
) {
    val extendedColors = LocalStenoColors.current
    val baseHeights = listOf(
        12, 20, 28, 16, 32, 24, 34, 20, 28, 16, 30,
        34, 28, 14, 20, 16, 24, 10, 20, 16, 12, 8
    )
    val transition = rememberInfiniteTransition(label = "spectrum_wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spectrum_phase"
    )

    Surface(
        color = extendedColors.surfaceContainerLowest,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val activeCutoff = ((progressFraction.coerceIn(0f, 1f)) * baseHeights.size).roundToInt()
                .coerceAtLeast(if (progressFraction == 0f) 0 else 1)

            baseHeights.forEachIndexed { index, baseDp ->
                val isActive = index < activeCutoff
                val animatedFactor = if (isAnimating) {
                    0.75f + 0.35f * if (index % 2 == 0) phase else (1f - phase)
                } else {
                    1f
                }
                val barHeight by animateFloatAsState(
                    targetValue = (baseDp * animatedFactor).coerceIn(6f, 34f),
                    animationSpec = tween(180),
                    label = "bar_$index"
                )
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(barHeight.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isActive) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
    }
}

@Composable
private fun MetronomeBenchmarkBar(
    targetWpm: Int,
    currentWordIndex: Int,
    totalWords: Int,
    isTesting: Boolean,
    currentBeatIndex: Int
) {
    val extendedColors = LocalStenoColors.current
    val wordsPerSec = String.format(Locale.US, "%.2f", targetWpm / 60f)
    val chimeSeconds = String.format(Locale.US, "%.1f", (25 * 60f) / targetWpm)

    val pulseTransition = rememberInfiniteTransition(label = "metronome_pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (60_000 / targetWpm).coerceIn(300, 1000)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "metronome_dot"
    )

    val milestones = listOf(
        "Start" to 0,
        "1/4m (25w)" to 25,
        "1/2m (50w)" to 50,
        "3/4m (75w)" to 75,
        "Full (${totalWords}w)" to totalWords
    )

    Surface(
        color = extendedColors.surfaceContainerLowest,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(if (isTesting || currentWordIndex > 0) pulseAlpha else 0.9f)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                    Text(
                        text = "Cadence Pulse: $targetWpm WPM ($wordsPerSec wps)",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Chime: Every ${chimeSeconds}s",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = extendedColors.surfaceContainerLow,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    milestones.forEachIndexed { idx, (label, threshold) ->
                        val reached = when {
                            idx == 0 -> true
                            isTesting -> (currentBeatIndex % milestones.size) >= idx
                            else -> currentWordIndex >= threshold && threshold > 0
                        }
                        val nodeColor by animateColorAsState(
                            targetValue = if (reached) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                extendedColors.surfaceContainerHigh
                            },
                            label = "node_$idx"
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(nodeColor)
                            ) {
                                Text(
                                    text = when (idx) {
                                        0 -> "✓"
                                        milestones.lastIndex -> "★"
                                        else -> idx.toString()
                                    },
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (reached) {
                                        MaterialTheme.colorScheme.onSecondary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = label,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.5.sp,
                                fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal,
                                color = if (reached) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                        if (idx < milestones.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .padding(horizontal = 3.dp)
                                    .background(
                                        if (reached) MaterialTheme.colorScheme.secondary
                                        else extendedColors.surfaceContainerHigh
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TestCadencePreviewButton(
    status: CadenceTestStatus,
    onClick: () -> Unit
) {
    val extendedColors = LocalStenoColors.current
    val isTesting = status == CadenceTestStatus.TESTING

    val transition = rememberInfiniteTransition(label = "eq_bars")
    val bounce by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 360),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_bounce"
    )

    Surface(
        color = if (isTesting) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        } else {
            extendedColors.surfaceContainer
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("test_cadence_preview_button")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isTesting) Icons.Rounded.PauseCircle else Icons.Rounded.PlayCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Test Cadence Playback (5-Sec Preview)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val barHeights = listOf(12f, 16f, 10f)
                barHeights.forEachIndexed { idx, base ->
                    val h = if (isTesting) {
                        val factor = if (idx % 2 == 0) bounce else (1.75f - bounce)
                        (base * factor).coerceIn(6f, 20f)
                    } else {
                        base
                    }
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(h.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (status) {
                        CadenceTestStatus.READY -> "Ready"
                        CadenceTestStatus.TESTING -> "Testing (5s)..."
                        CadenceTestStatus.CALIBRATED -> "Calibrated"
                    },
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.testTag("test_cadence_status_text")
                )
            }
        }
    }
}

@Composable
private fun AudioToolCheckboxRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val extendedColors = LocalStenoColors.current
    Surface(
        color = extendedColors.surfaceContainerLow,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(19.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

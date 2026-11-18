package com.example.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.domain.model.PassageHighlight
import com.example.ui.theme.LocalStenoColors
import com.example.ui.theme.SourceSerifFontFamily

@Composable
fun InteractivePassageText(
    text: String,
    highlights: List<PassageHighlight>,
    selectedHighlight: PassageHighlight?,
    activeKaraokeRange: IntRange?,
    karaokeEnabled: Boolean,
    onHighlightClick: (PassageHighlight) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    val highlightBg = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.50f)
    val highlightStrongBg = MaterialTheme.colorScheme.secondaryContainer
    val highlightText = extendedColors.onSecondaryFixedVariant
    val selectedBg = MaterialTheme.colorScheme.primaryContainer
    val selectedText = MaterialTheme.colorScheme.onPrimary
    val karaokeBg = MaterialTheme.colorScheme.secondary
    val karaokeText = MaterialTheme.colorScheme.onSecondary
    val primaryInk = MaterialTheme.colorScheme.primary

    val annotated = remember(text, highlights, selectedHighlight, activeKaraokeRange, karaokeEnabled) {
        buildAnnotatedString {
            append(text)

            // Apply Pitman highlight markers
            for (hl in highlights) {
                val safeStart = hl.startIndex.coerceIn(0, text.length)
                val safeEnd = hl.endIndex.coerceIn(safeStart, text.length)
                if (safeStart < safeEnd) {
                    val isSelected = selectedHighlight != null &&
                        selectedHighlight.startIndex == hl.startIndex &&
                        selectedHighlight.endIndex == hl.endIndex
                    val isStrong = hl.matchedText.equals("learned counsel", ignoreCase = true)
                    addStyle(
                        style = SpanStyle(
                            background = when {
                                isSelected -> selectedBg
                                isStrong -> highlightStrongBg
                                else -> highlightBg
                            },
                            color = if (isSelected) selectedText else highlightText,
                            fontWeight = if (isSelected || isStrong) FontWeight.SemiBold else FontWeight.Medium,
                            textDecoration = if (isSelected) TextDecoration.Underline else TextDecoration.None
                        ),
                        start = safeStart,
                        end = safeEnd
                    )
                }
            }

            // Italicize classic Latin legal phrases
            val latinPhrases = listOf("audi alteram partem", "locus poenitentiae", "ultra vires")
            val lower = text.lowercase()
            for (latin in latinPhrases) {
                var idx = lower.indexOf(latin)
                while (idx >= 0) {
                    addStyle(
                        style = SpanStyle(
                            fontStyle = FontStyle.Italic,
                            color = primaryInk
                        ),
                        start = idx,
                        end = (idx + latin.length).coerceAtMost(text.length)
                    )
                    idx = lower.indexOf(latin, idx + latin.length)
                }
            }

            // Apply live Karaoke Sync active word overlay
            if (karaokeEnabled && activeKaraokeRange != null) {
                val kStart = activeKaraokeRange.first.coerceIn(0, text.length)
                val kEnd = (activeKaraokeRange.last + 1).coerceIn(kStart, text.length)
                if (kStart < kEnd) {
                    addStyle(
                        style = SpanStyle(
                            background = karaokeBg,
                            color = karaokeText,
                            fontWeight = FontWeight.Bold
                        ),
                        start = kStart,
                        end = kEnd
                    )
                }
            }
        }
    }

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = annotated,
        fontFamily = SourceSerifFontFamily,
        fontSize = 17.sp,
        lineHeight = 29.sp,
        color = MaterialTheme.colorScheme.onSurface,
        onTextLayout = { textLayoutResult = it },
        modifier = modifier.pointerInput(highlights) {
            detectTapGestures { offset ->
                val layout = textLayoutResult ?: return@detectTapGestures
                val charIndex = layout.getOffsetForPosition(offset)
                val hit = highlights.firstOrNull { charIndex in it.startIndex until it.endIndex }
                if (hit != null) {
                    onHighlightClick(hit)
                }
            }
        }
    )
}

package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.domain.model.StenoPosition
import com.example.domain.model.StenoStrokeSpec
import com.example.ui.theme.LocalStenoColors

@Composable
fun PitmanStemCanvas(
    spec: StenoStrokeSpec,
    isKaraokeHighlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalStenoColors.current
    val primaryInk = MaterialTheme.colorScheme.primary
    val accentTeal = MaterialTheme.colorScheme.secondary
    val guideColor = MaterialTheme.colorScheme.outlineVariant

    val wellBg by animateColorAsState(
        targetValue = if (isKaraokeHighlighted) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        } else {
            extendedColors.surfaceContainerLow
        },
        animationSpec = tween(220),
        label = "stem_well_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isKaraokeHighlighted) {
            accentTeal
        } else {
            Color.Transparent
        },
        animationSpec = tween(220),
        label = "stem_well_border"
    )

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(wellBg)
            .border(
                width = if (isKaraokeHighlighted) 1.5.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxSize()
    ) {
        val w = size.width
        val h = size.height

        // 3 stenographic ruled lines (top guide, baseline, bottom guide)
        val topLineY = h * 0.25f
        val baseLineY = h * 0.52f
        val bottomLineY = h * 0.78f
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)

        drawLine(
            color = guideColor.copy(alpha = 0.65f),
            start = Offset(w * 0.06f, topLineY),
            end = Offset(w * 0.94f, topLineY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = dashEffect
        )
        drawLine(
            color = accentTeal.copy(alpha = 0.55f),
            start = Offset(w * 0.06f, baseLineY),
            end = Offset(w * 0.94f, baseLineY),
            strokeWidth = 1.25.dp.toPx()
        )
        drawLine(
            color = guideColor.copy(alpha = 0.65f),
            start = Offset(w * 0.06f, bottomLineY),
            end = Offset(w * 0.94f, bottomLineY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = dashEffect
        )

        val yShift = when (spec.position) {
            StenoPosition.FIRST -> -h * 0.14f
            StenoPosition.SECOND -> 0f
            StenoPosition.THIRD -> h * 0.14f
        }

        drawPitmanGlyph(
            glyphKey = spec.glyphKey,
            yShift = yShift,
            inkColor = primaryInk,
            tealColor = accentTeal
        )
    }
}

private fun DrawScope.drawPitmanGlyph(
    glyphKey: String,
    yShift: Float,
    inkColor: Color,
    tealColor: Color
) {
    val w = size.width
    val h = size.height
    val lightStroke = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    val heavyStroke = Stroke(width = 3.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    val tealStroke = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

    fun p(rx: Float, ry: Float): Offset = Offset(w * rx, (h * ry) + yShift)

    when (glyphKey) {
        "learned_counsel" -> {
            // Upward L-R-N-D halved stroke
            val main = Path().apply {
                val a = p(0.20f, 0.66f)
                val b = p(0.36f, 0.44f)
                val c = p(0.50f, 0.28f)
                moveTo(a.x, a.y)
                quadraticTo(b.x, b.y, c.x, c.y)
            }
            drawPath(main, color = inkColor, style = lightStroke)
            // Intersecting K-N-S-L horizontal/curved stroke in teal
            val intersect = Path().apply {
                val s = p(0.36f, 0.20f)
                val m = p(0.62f, 0.56f)
                val e = p(0.82f, 0.48f)
                moveTo(s.x, s.y)
                lineTo(m.x, m.y)
                quadraticTo(w * 0.72f, (h * 0.66f) + yShift, e.x, e.y)
            }
            drawPath(intersect, color = tealColor, style = tealStroke)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = p(0.28f, 0.34f))
        }

        "jurisdiction" -> {
            // Heavy downward Jay through line + S circle + D-K curve + -tion hook
            val jay = Path().apply {
                val a = p(0.24f, 0.14f)
                val b = p(0.38f, 0.62f)
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
            }
            drawPath(jay, color = inkColor, style = heavyStroke)
            // Small S-circle
            drawCircle(
                color = tealColor,
                radius = 4.2.dp.toPx(),
                center = p(0.41f, 0.64f),
                style = lightStroke
            )
            // Curved D-K + final -tion hook
            val rest = Path().apply {
                val s = p(0.45f, 0.64f)
                val e = p(0.76f, 0.52f)
                moveTo(s.x, s.y)
                cubicTo(
                    w * 0.55f, (h * 0.46f) + yShift,
                    w * 0.68f, (h * 0.44f) + yShift,
                    e.x, e.y
                )
                quadraticTo(w * 0.83f, (h * 0.44f) + yShift, w * 0.78f, (h * 0.60f) + yShift)
            }
            drawPath(rest, color = inkColor, style = lightStroke)
        }

        "petitioner" -> {
            // Light downward Pee + intersecting Tee stroke (P-T legal intersection)
            val pee = Path().apply {
                val a = p(0.28f, 0.24f)
                val b = p(0.52f, 0.66f)
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
            }
            drawPath(pee, color = inkColor, style = lightStroke)
            // Intersecting vertical Tee in teal
            val tee = Path().apply {
                val a = p(0.54f, 0.22f)
                val b = p(0.54f, 0.70f)
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
            }
            drawPath(tee, color = tealColor, style = tealStroke)
            // Light vowel dot
            drawCircle(color = tealColor, radius = 2.3.dp.toPx(), center = p(0.35f, 0.52f))
        }

        "constitution" -> {
            // Horizontal Kay + Stee loop + halved Tee + -tion hook
            val kay = Path().apply {
                val a = p(0.18f, 0.50f)
                val b = p(0.46f, 0.50f)
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
            }
            drawPath(kay, color = inkColor, style = lightStroke)
            // Stee loop
            val stee = Path().apply {
                val a = p(0.46f, 0.50f)
                val b = p(0.64f, 0.34f)
                moveTo(a.x, a.y)
                cubicTo(
                    w * 0.52f, (h * 0.38f) + yShift,
                    w * 0.60f, (h * 0.32f) + yShift,
                    b.x, b.y
                )
            }
            drawPath(stee, color = tealColor, style = tealStroke)
            // Halved downward stem + -tion hook
            val shn = Path().apply {
                val a = p(0.64f, 0.34f)
                val b = p(0.64f, 0.60f)
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
                quadraticTo(w * 0.72f, (h * 0.68f) + yShift, w * 0.78f, (h * 0.56f) + yShift)
            }
            drawPath(shn, color = inkColor, style = lightStroke)
        }

        "high_court" -> {
            // Upward Hay with initial circle + intersecting heavy Kay
            drawCircle(
                color = inkColor,
                radius = 4.dp.toPx(),
                center = p(0.26f, 0.64f),
                style = lightStroke
            )
            val hay = Path().apply {
                val a = p(0.30f, 0.62f)
                val b = p(0.56f, 0.26f)
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
            }
            drawPath(hay, color = inkColor, style = lightStroke)
            val kay = Path().apply {
                val a = p(0.38f, 0.44f)
                val b = p(0.78f, 0.44f)
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
            }
            drawPath(kay, color = tealColor, style = tealStroke)
        }

        "well_settled", "natural_justice", "fundamental_rights" -> {
            // Joined phrasing outline: initial hook + sweeping curve + S-circle + final stroke
            val phrase = Path().apply {
                val a = p(0.18f, 0.58f)
                val b = p(0.44f, 0.36f)
                val c = p(0.68f, 0.58f)
                moveTo(a.x, a.y)
                quadraticTo(w * 0.30f, (h * 0.28f) + yShift, b.x, b.y)
                lineTo(c.x, c.y)
            }
            drawPath(phrase, color = inkColor, style = lightStroke)
            drawCircle(
                color = tealColor,
                radius = 4.2.dp.toPx(),
                center = p(0.68f, 0.58f),
                style = lightStroke
            )
            val tail = Path().apply {
                val s = p(0.72f, 0.58f)
                val e = p(0.84f, 0.38f)
                moveTo(s.x, s.y)
                lineTo(e.x, e.y)
            }
            drawPath(tail, color = tealColor, style = tealStroke)
        }

        // === Pitman Consonant Straight Strokes ===
        "sym_p", "sym_b" -> {
            val style = if (glyphKey == "sym_b") heavyStroke else lightStroke
            val start = p(0.36f, 0.26f)
            val end = p(0.62f, 0.68f)
            drawLine(color = inkColor, start = start, end = end, strokeWidth = style.width, cap = StrokeCap.Round)
            drawCircle(color = tealColor, radius = 2.6.dp.toPx(), center = start)
        }

        "sym_t", "sym_d" -> {
            val style = if (glyphKey == "sym_d") heavyStroke else lightStroke
            val start = p(0.50f, 0.22f)
            val end = p(0.50f, 0.72f)
            drawLine(color = inkColor, start = start, end = end, strokeWidth = style.width, cap = StrokeCap.Round)
            drawCircle(color = tealColor, radius = 2.6.dp.toPx(), center = start)
        }

        "sym_ch", "sym_j" -> {
            val style = if (glyphKey == "sym_j") heavyStroke else lightStroke
            val start = p(0.64f, 0.24f)
            val end = p(0.36f, 0.70f)
            drawLine(color = inkColor, start = start, end = end, strokeWidth = style.width, cap = StrokeCap.Round)
            drawCircle(color = tealColor, radius = 2.6.dp.toPx(), center = start)
        }

        "sym_k", "sym_g" -> {
            val style = if (glyphKey == "sym_g") heavyStroke else lightStroke
            val start = p(0.24f, 0.52f)
            val end = p(0.76f, 0.52f)
            drawLine(color = inkColor, start = start, end = end, strokeWidth = style.width, cap = StrokeCap.Round)
            drawCircle(color = tealColor, radius = 2.6.dp.toPx(), center = start)
        }

        "sym_ray" -> {
            val start = p(0.24f, 0.66f)
            val end = p(0.76f, 0.34f)
            drawLine(color = inkColor, start = start, end = end, strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            drawCircle(color = tealColor, radius = 2.6.dp.toPx(), center = start)
        }

        // === Pitman Curved Strokes ===
        "sym_f", "sym_v" -> {
            val style = if (glyphKey == "sym_v") heavyStroke else lightStroke
            val start = p(0.34f, 0.26f)
            val curve = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(w * 0.36f, (h * 0.58f) + yShift, w * 0.64f, (h * 0.68f) + yShift)
            }
            drawPath(curve, color = inkColor, style = style)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = start)
        }

        "sym_th", "sym_dh" -> {
            val style = if (glyphKey == "sym_dh") heavyStroke else lightStroke
            val start = p(0.54f, 0.24f)
            val curve = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(w * 0.36f, (h * 0.48f) + yShift, w * 0.54f, (h * 0.72f) + yShift)
            }
            drawPath(curve, color = inkColor, style = style)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = start)
        }

        "sym_s", "sym_z" -> {
            val style = if (glyphKey == "sym_z") heavyStroke else lightStroke
            val start = p(0.46f, 0.24f)
            val curve = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(w * 0.64f, (h * 0.48f) + yShift, w * 0.46f, (h * 0.72f) + yShift)
            }
            drawPath(curve, color = inkColor, style = style)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = start)
        }

        "sym_sh", "sym_zh" -> {
            val style = if (glyphKey == "sym_zh") heavyStroke else lightStroke
            val start = p(0.64f, 0.26f)
            val curve = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(w * 0.58f, (h * 0.58f) + yShift, w * 0.34f, (h * 0.68f) + yShift)
            }
            drawPath(curve, color = inkColor, style = style)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = start)
        }

        "sym_m" -> {
            val start = p(0.24f, 0.52f)
            val curve = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(w * 0.50f, (h * 0.30f) + yShift, w * 0.76f, (h * 0.52f) + yShift)
            }
            drawPath(curve, color = inkColor, style = lightStroke)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = start)
        }

        "sym_n", "sym_ng" -> {
            val style = if (glyphKey == "sym_ng") heavyStroke else lightStroke
            val start = p(0.24f, 0.46f)
            val curve = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(w * 0.50f, (h * 0.70f) + yShift, w * 0.76f, (h * 0.46f) + yShift)
            }
            drawPath(curve, color = inkColor, style = style)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = start)
        }

        "sym_l" -> {
            val start = p(0.24f, 0.66f)
            val curve = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(w * 0.46f, (h * 0.38f) + yShift, w * 0.74f, (h * 0.30f) + yShift)
            }
            drawPath(curve, color = inkColor, style = lightStroke)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = start)
        }

        "sym_ar" -> {
            val start = p(0.68f, 0.28f)
            val curve = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(w * 0.58f, (h * 0.58f) + yShift, w * 0.32f, (h * 0.68f) + yShift)
            }
            drawPath(curve, color = inkColor, style = lightStroke)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = start)
        }

        "sym_w" -> {
            val hook = Path().apply {
                moveTo(w * 0.24f, (h * 0.56f) + yShift)
                quadraticTo(w * 0.22f, (h * 0.68f) + yShift, w * 0.32f, (h * 0.64f) + yShift)
            }
            drawPath(hook, color = tealColor, style = tealStroke)
            drawLine(color = inkColor, start = p(0.32f, 0.64f), end = p(0.76f, 0.34f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
        }

        "sym_y" -> {
            val hook = Path().apply {
                moveTo(w * 0.36f, (h * 0.72f) + yShift)
                quadraticTo(w * 0.26f, (h * 0.70f) + yShift, w * 0.30f, (h * 0.62f) + yShift)
            }
            drawPath(hook, color = tealColor, style = tealStroke)
            drawLine(color = inkColor, start = p(0.30f, 0.62f), end = p(0.76f, 0.34f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
        }

        "sym_h" -> {
            drawCircle(color = tealColor, radius = 4.2.dp.toPx(), center = p(0.28f, 0.66f), style = tealStroke)
            drawLine(color = inkColor, start = p(0.33f, 0.62f), end = p(0.76f, 0.32f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
        }

        // === Circles, Loops & Hooks ===
        "sym_circle_s" -> {
            drawCircle(color = tealColor, radius = 4.5.dp.toPx(), center = p(0.32f, 0.44f), style = tealStroke)
            drawLine(color = inkColor, start = p(0.36f, 0.52f), end = p(0.76f, 0.52f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
        }

        "sym_circle_ses" -> {
            drawCircle(color = tealColor, radius = 8.2.dp.toPx(), center = p(0.68f, 0.43f), style = tealStroke)
            drawLine(color = inkColor, start = p(0.22f, 0.52f), end = p(0.64f, 0.52f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
        }

        "sym_loop_st" -> {
            drawLine(color = inkColor, start = p(0.22f, 0.56f), end = p(0.76f, 0.56f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            val loop = Path().apply {
                moveTo(w * 0.24f, (h * 0.56f) + yShift)
                cubicTo(w * 0.30f, (h * 0.36f) + yShift, w * 0.48f, (h * 0.38f) + yShift, w * 0.50f, (h * 0.56f) + yShift)
            }
            drawPath(loop, color = tealColor, style = tealStroke)
        }

        "sym_loop_str" -> {
            drawLine(color = inkColor, start = p(0.20f, 0.58f), end = p(0.78f, 0.58f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            val loop = Path().apply {
                moveTo(w * 0.36f, (h * 0.58f) + yShift)
                cubicTo(w * 0.46f, (h * 0.30f) + yShift, w * 0.74f, (h * 0.32f) + yShift, w * 0.78f, (h * 0.58f) + yShift)
            }
            drawPath(loop, color = tealColor, style = tealStroke)
        }

        "sym_hook_r" -> {
            val hook = Path().apply {
                moveTo(w * 0.44f, (h * 0.24f) + yShift)
                quadraticTo(w * 0.32f, (h * 0.22f) + yShift, w * 0.38f, (h * 0.34f) + yShift)
            }
            drawPath(hook, color = tealColor, style = tealStroke)
            drawLine(color = inkColor, start = p(0.38f, 0.34f), end = p(0.62f, 0.72f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
        }

        "sym_hook_l" -> {
            val hook = Path().apply {
                moveTo(w * 0.32f, (h * 0.30f) + yShift)
                quadraticTo(w * 0.46f, (h * 0.22f) + yShift, w * 0.40f, (h * 0.36f) + yShift)
            }
            drawPath(hook, color = tealColor, style = tealStroke)
            drawLine(color = inkColor, start = p(0.40f, 0.36f), end = p(0.62f, 0.72f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
        }

        "sym_hook_n" -> {
            drawLine(color = inkColor, start = p(0.24f, 0.48f), end = p(0.68f, 0.48f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            val hook = Path().apply {
                moveTo(w * 0.68f, (h * 0.48f) + yShift)
                quadraticTo(w * 0.74f, (h * 0.60f) + yShift, w * 0.62f, (h * 0.60f) + yShift)
            }
            drawPath(hook, color = tealColor, style = tealStroke)
        }

        "sym_hook_shun" -> {
            drawLine(color = inkColor, start = p(0.24f, 0.44f), end = p(0.66f, 0.44f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            val hook = Path().apply {
                moveTo(w * 0.66f, (h * 0.44f) + yShift)
                cubicTo(w * 0.78f, (h * 0.46f) + yShift, w * 0.76f, (h * 0.68f) + yShift, w * 0.56f, (h * 0.62f) + yShift)
            }
            drawPath(hook, color = tealColor, style = tealStroke)
        }

        // === Core Grammalogues ===
        "gram_the" -> {
            drawCircle(color = inkColor, radius = 4.2.dp.toPx(), center = p(0.40f, 0.50f))
            drawLine(color = tealColor, start = p(0.56f, 0.40f), end = p(0.66f, 0.56f), strokeWidth = tealStroke.width, cap = StrokeCap.Round)
        }

        "gram_of" -> {
            drawLine(color = inkColor, start = p(0.44f, 0.44f), end = p(0.56f, 0.58f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            drawCircle(color = tealColor, radius = 2.2.dp.toPx(), center = p(0.44f, 0.44f))
        }

        "gram_should", "gram_and" -> {
            drawLine(color = inkColor, start = p(0.42f, 0.58f), end = p(0.58f, 0.44f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            drawCircle(color = tealColor, radius = 2.2.dp.toPx(), center = p(0.42f, 0.58f))
        }

        // === Dawn Editorial Outlines ===
        "dawn_government" -> {
            drawLine(color = inkColor, start = p(0.22f, 0.42f), end = p(0.52f, 0.42f), strokeWidth = heavyStroke.width, cap = StrokeCap.Round)
            val veeHook = Path().apply {
                moveTo(w * 0.52f, (h * 0.42f) + yShift)
                quadraticTo(w * 0.54f, (h * 0.62f) + yShift, w * 0.70f, (h * 0.70f) + yShift)
                quadraticTo(w * 0.78f, (h * 0.70f) + yShift, w * 0.72f, (h * 0.58f) + yShift)
            }
            drawPath(veeHook, color = tealColor, style = heavyStroke)
        }

        "dawn_parliament" -> {
            val prHook = Path().apply {
                moveTo(w * 0.30f, (h * 0.24f) + yShift)
                quadraticTo(w * 0.22f, (h * 0.24f) + yShift, w * 0.26f, (h * 0.34f) + yShift)
                lineTo(w * 0.42f, (h * 0.60f) + yShift)
            }
            drawPath(prHook, color = inkColor, style = lightStroke)
            val lmt = Path().apply {
                moveTo(w * 0.42f, (h * 0.60f) + yShift)
                quadraticTo(w * 0.56f, (h * 0.42f) + yShift, w * 0.66f, (h * 0.38f) + yShift)
                quadraticTo(w * 0.76f, (h * 0.32f) + yShift, w * 0.82f, (h * 0.42f) + yShift)
            }
            drawPath(lmt, color = tealColor, style = tealStroke)
        }

        "dawn_economy" -> {
            drawLine(color = inkColor, start = p(0.18f, 0.50f), end = p(0.40f, 0.50f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            val nm = Path().apply {
                moveTo(w * 0.40f, (h * 0.50f) + yShift)
                quadraticTo(w * 0.50f, (h * 0.62f) + yShift, w * 0.60f, (h * 0.50f) + yShift)
                quadraticTo(w * 0.70f, (h * 0.36f) + yShift, w * 0.82f, (h * 0.50f) + yShift)
            }
            drawPath(nm, color = tealColor, style = tealStroke)
        }

        "dawn_inflation" -> {
            val enFl = Path().apply {
                moveTo(w * 0.18f, (h * 0.42f) + yShift)
                quadraticTo(w * 0.30f, (h * 0.54f) + yShift, w * 0.44f, (h * 0.42f) + yShift)
                quadraticTo(w * 0.50f, (h * 0.60f) + yShift, w * 0.68f, (h * 0.68f) + yShift)
                cubicTo(w * 0.78f, (h * 0.70f) + yShift, w * 0.80f, (h * 0.54f) + yShift, w * 0.68f, (h * 0.54f) + yShift)
            }
            drawPath(enFl, color = inkColor, style = lightStroke)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = p(0.44f, 0.42f))
        }

        "dawn_accountability" -> {
            drawLine(color = inkColor, start = p(0.20f, 0.48f), end = p(0.46f, 0.48f), strokeWidth = lightStroke.width, cap = StrokeCap.Round)
            val hook = Path().apply {
                moveTo(w * 0.46f, (h * 0.48f) + yShift)
                quadraticTo(w * 0.50f, (h * 0.58f) + yShift, w * 0.42f, (h * 0.58f) + yShift)
            }
            drawPath(hook, color = inkColor, style = lightStroke)
            // Disjoined heavy Bee (-bility)
            drawLine(color = tealColor, start = p(0.60f, 0.30f), end = p(0.76f, 0.64f), strokeWidth = heavyStroke.width, cap = StrokeCap.Round)
        }

        "dawn_legislation" -> {
            val path = Path().apply {
                moveTo(w * 0.18f, (h * 0.60f) + yShift)
                quadraticTo(w * 0.30f, (h * 0.44f) + yShift, w * 0.42f, (h * 0.34f) + yShift)
                lineTo(w * 0.32f, (h * 0.66f) + yShift)
            }
            drawPath(path, color = inkColor, style = heavyStroke)
            drawCircle(color = tealColor, radius = 4.dp.toPx(), center = p(0.36f, 0.68f), style = tealStroke)
            val end = Path().apply {
                moveTo(w * 0.40f, (h * 0.68f) + yShift)
                quadraticTo(w * 0.58f, (h * 0.48f) + yShift, w * 0.74f, (h * 0.40f) + yShift)
                quadraticTo(w * 0.82f, (h * 0.50f) + yShift, w * 0.70f, (h * 0.56f) + yShift)
            }
            drawPath(end, color = inkColor, style = lightStroke)
        }

        "dawn_reform" -> {
            val path = Path().apply {
                moveTo(w * 0.18f, (h * 0.62f) + yShift)
                lineTo(w * 0.44f, (h * 0.38f) + yShift)
                quadraticTo(w * 0.48f, (h * 0.58f) + yShift, w * 0.64f, (h * 0.64f) + yShift)
                quadraticTo(w * 0.74f, (h * 0.50f) + yShift, w * 0.84f, (h * 0.58f) + yShift)
            }
            drawPath(path, color = inkColor, style = lightStroke)
            drawCircle(color = tealColor, radius = 2.4.dp.toPx(), center = p(0.18f, 0.62f))
        }

        else -> {
            // Judicial balanced outline: heavy stem + medial curve + hook
            val stem = Path().apply {
                val a = p(0.24f, 0.28f)
                val b = p(0.42f, 0.62f)
                val c = p(0.76f, 0.48f)
                moveTo(a.x, a.y)
                lineTo(b.x, b.y)
                quadraticTo(w * 0.58f, (h * 0.36f) + yShift, c.x, c.y)
            }
            drawPath(stem, color = inkColor, style = heavyStroke)
            drawCircle(color = tealColor, radius = 2.5.dp.toPx(), center = p(0.54f, 0.34f))
        }
    }
}

package com.example.export

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.domain.model.LegalCasePassage
import com.example.domain.model.StenoAnalysis
import com.example.domain.model.WpmBenchmarkUtils
import java.io.File
import java.io.FileOutputStream

object StenoPdfExporter {

    fun exportAndShare(
        context: Context,
        passage: LegalCasePassage,
        analysis: StenoAnalysis,
        targetWpm: Int
    ): Result<File> {
        return try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val safeName = passage.citation
                .replace(Regex("[^A-Za-z0-9._-]+"), "_")
                .trim('_')
                .ifEmpty { "steno_passage" }
            val outFile = File(exportDir, "${safeName}_${targetWpm}wpm.pdf")

            val doc = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val margin = 40f
            val contentWidth = (pageWidth - margin * 2).toInt()

            var pageNumber = 1
            var page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            var canvas = page.canvas
            var y = margin

            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#131B2E")
                textSize = 18f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            }
            val subPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#006A61")
                textSize = 11f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            }
            val sectionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#131B2E")
                textSize = 13f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            }
            val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#191C1E")
                textSize = 11.5f
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            }
            val monoPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#45464D")
                textSize = 9.5f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            }
            val rulePaint = Paint().apply {
                color = Color.parseColor("#C6C6CD")
                strokeWidth = 1f
            }

            fun ensureSpace(neededHeight: Float) {
                if (y + neededHeight > pageHeight - margin) {
                    doc.finishPage(page)
                    pageNumber++
                    page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                    canvas = page.canvas
                    y = margin
                }
            }

            fun drawWrappedText(text: String, paint: TextPaint, extraBottom: Float = 8f) {
                val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, contentWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(2f, 1.15f)
                    .build()
                ensureSpace(layout.height + extraBottom)
                canvas.save()
                canvas.translate(margin, y)
                layout.draw(canvas)
                canvas.restore()
                y += layout.height + extraBottom
            }

            // Header
            drawWrappedText("PITMAN STENO • LEGAL DICTATION SHEET", subPaint, 4f)
            drawWrappedText("${passage.citation} — ${passage.court}", titlePaint, 4f)
            drawWrappedText(passage.subtitle, monoPaint, 10f)
            canvas.drawLine(margin, y, pageWidth - margin, y, rulePaint)
            y += 14f

            // Metrics & Speed Benchmarks
            drawWrappedText("SPEED BENCHMARKS (${analysis.wordCount} WORDS • ${analysis.complexityLabel})", sectionPaint, 6f)
            val targetSec = kotlin.math.max(1, kotlin.math.ceil(analysis.wordCount * 60.0 / targetWpm).toInt())
            val benchmarkLine = buildString {
                append("Active Target: $targetWpm WPM (${WpmBenchmarkUtils.formatCompactDuration(targetSec)})   |   ")
                append("80 WPM: ${WpmBenchmarkUtils.formatCompactDuration(analysis.secondsAt80Wpm)}   |   ")
                append("100 WPM: ${WpmBenchmarkUtils.formatCompactDuration(analysis.secondsAt100Wpm)}   |   ")
                append("120 WPM: ${WpmBenchmarkUtils.formatCompactDuration(analysis.secondsAt120Wpm)}   |   ")
                append("140 WPM: ${WpmBenchmarkUtils.formatCompactDuration(analysis.secondsAt140Wpm)}")
            }
            drawWrappedText(benchmarkLine, monoPaint, 8f)
            drawWrappedText(
                "Pitman Architecture: ${analysis.grammalogueCount} Grammalogues • ${analysis.legalContractionCount} Contractions • ${analysis.intersectionCount} Intersections • ${analysis.halvedDoubledCount} Halved/Doubled",
                monoPaint,
                12f
            )

            // Passage Body
            drawWrappedText("VERBATIM DICTATION PASSAGE", sectionPaint, 6f)
            drawWrappedText(passage.text, bodyPaint, 14f)

            // Pitman Outline Key
            drawWrappedText("DETECTED PITMAN OUTLINES & THEORY KEY", sectionPaint, 6f)
            analysis.allDetectedOutlines.forEach { spec ->
                val line = "${spec.term}  [${spec.strokeFormula}]  •  ${spec.position.badge}  (${spec.category.label})"
                drawWrappedText(line, subPaint, 2f)
                drawWrappedText(spec.ruleNote, monoPaint, 8f)
            }

            doc.finishPage(page)
            FileOutputStream(outFile).use { fos ->
                doc.writeTo(fos)
            }
            doc.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                outFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Pitman Steno Sheet: ${passage.citation} @ ${targetWpm} WPM")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Export Steno PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            Result.success(outFile)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}

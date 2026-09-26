package com.nihongosteps.app.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset

/**
 * Scores a hand-drawn character by comparing it with the font glyph.
 *
 * Both the glyph and the user's strokes are rendered into a small bitmap. The score is
 * the F1 of: how much of the glyph the strokes cover (recall) and how much of the ink
 * lands on the glyph (precision), each with a small tolerance.
 */
object HandwritingGrader {

    private const val N = 72
    private const val TOLERANCE = 3

    /** Geometry shared with the on-screen guide so they line up exactly. */
    const val GLYPH_SCALE = 0.78f
    const val STROKE_SCALE = 0.05f

    fun glyphPaint(typeface: Typeface, size: Float, color: Int): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.typeface = typeface
        textSize = size * GLYPH_SCALE
        textAlign = Paint.Align.CENTER
        this.color = color
    }

    /** Baseline y that vertically centres the glyph's em box in a square of [size]. */
    fun baseline(paint: Paint, size: Float): Float {
        val fm = paint.fontMetrics
        return size / 2f - (fm.ascent + fm.descent) / 2f
    }

    fun grade(char: String, typeface: Typeface, strokes: List<List<Offset>>, canvasSize: Float): Int {
        if (strokes.isEmpty() || canvasSize <= 0f) return 0

        // Target glyph.
        val target = Bitmap.createBitmap(N, N, Bitmap.Config.ARGB_8888)
        Canvas(target).apply {
            val p = glyphPaint(typeface, N.toFloat(), android.graphics.Color.BLACK)
            drawText(char, N / 2f, baseline(p, N.toFloat()), p)
        }

        // User ink.
        val user = Bitmap.createBitmap(N, N, Bitmap.Config.ARGB_8888)
        Canvas(user).apply {
            val k = N / canvasSize
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = N * STROKE_SCALE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                color = android.graphics.Color.BLACK
            }
            strokes.forEach { stroke ->
                if (stroke.size == 1) {
                    drawPoint(stroke[0].x * k, stroke[0].y * k, p)
                } else {
                    for (i in 1 until stroke.size) {
                        drawLine(stroke[i - 1].x * k, stroke[i - 1].y * k, stroke[i].x * k, stroke[i].y * k, p)
                    }
                }
            }
        }

        val t = mask(target); val u = mask(user)
        target.recycle(); user.recycle()
        val tCount = t.count { it }; val uCount = u.count { it }
        if (tCount == 0 || uCount == 0) return 0

        val tDil = dilate(t); val uDil = dilate(u)
        var covered = 0; var onTarget = 0
        for (i in t.indices) {
            if (t[i] && uDil[i]) covered++
            if (u[i] && tDil[i]) onTarget++
        }
        val recall = covered / tCount.toFloat()
        val precision = onTarget / uCount.toFloat()
        if (recall + precision == 0f) return 0
        val f1 = 2 * recall * precision / (recall + precision)
        return (f1 * 100).toInt().coerceIn(0, 100)
    }

    private fun mask(b: Bitmap): BooleanArray {
        val px = IntArray(N * N)
        b.getPixels(px, 0, N, 0, 0, N, N)
        return BooleanArray(N * N) { (px[it] ushr 24) > 80 }
    }

    /** Square dilation, done as two separable passes. */
    private fun dilate(m: BooleanArray): BooleanArray {
        val h = BooleanArray(N * N)
        for (y in 0 until N) for (x in 0 until N) {
            var on = false
            for (dx in -TOLERANCE..TOLERANCE) {
                val xx = x + dx
                if (xx in 0 until N && m[y * N + xx]) { on = true; break }
            }
            h[y * N + x] = on
        }
        val out = BooleanArray(N * N)
        for (y in 0 until N) for (x in 0 until N) {
            var on = false
            for (dy in -TOLERANCE..TOLERANCE) {
                val yy = y + dy
                if (yy in 0 until N && h[yy * N + x]) { on = true; break }
            }
            out[y * N + x] = on
        }
        return out
    }
}

package com.nihongosteps.app.ui.screens

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.nihongosteps.app.R
import com.nihongosteps.app.data.KanaData
import com.nihongosteps.app.data.Script
import com.nihongosteps.app.ui.ChoiceChips
import com.nihongosteps.app.ui.LocalApp
import com.nihongosteps.app.ui.ScreenScaffold
import com.nihongosteps.app.ui.SpeakButton
import com.nihongosteps.app.ui.masu
import com.nihongosteps.app.ui.rememberProgress
import com.nihongosteps.app.ui.theme.LocalExtraColors
import com.nihongosteps.app.ui.theme.jp
import com.nihongosteps.app.util.HandwritingGrader

private enum class WriteSet(val label: String) { HIRAGANA("Hiragana"), KATAKANA("Katakana"), KANJI("Kanji") }

private data class WriteItem(val char: String, val reading: String, val meaning: String?, val strokes: Int)

private fun itemsFor(set: WriteSet): List<WriteItem> = when (set) {
    WriteSet.HIRAGANA -> KanaData.all(Script.HIRAGANA).map { WriteItem(it.char, it.romaji, null, it.strokes) }
    WriteSet.KATAKANA -> KanaData.all(Script.KATAKANA).map { WriteItem(it.char, it.romaji, null, it.strokes) }
    WriteSet.KANJI -> KanaData.kanji.map { WriteItem(it.char, "${it.kun}  ·  ${it.on}", it.meaning, it.strokes) }
}

private const val PASS = 55
private const val WRITE_XP = 5

@Composable
fun WriteScreen(startChar: String?, onBack: (() -> Unit)?) {
    val context = LocalContext.current
    val app = LocalApp.current
    val progress by rememberProgress()
    val extra = LocalExtraColors.current
    val scheme = MaterialTheme.colorScheme

    val initialSet = remember(startChar) {
        when {
            startChar == null -> WriteSet.HIRAGANA
            KanaData.all(Script.KATAKANA).any { it.char == startChar } -> WriteSet.KATAKANA
            KanaData.kanji.any { it.char == startChar } -> WriteSet.KANJI
            else -> WriteSet.HIRAGANA
        }
    }
    var set by rememberSaveable { mutableStateOf(initialSet) }
    val list = remember(set) { itemsFor(set) }
    var index by rememberSaveable(set) { mutableIntStateOf(list.indexOfFirst { it.char == startChar }.coerceAtLeast(0)) }
    val item = list[index.coerceIn(list.indices)]

    val strokes = remember(item.char) { mutableStateListOf<List<Offset>>() }
    var current by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var showGuide by rememberSaveable { mutableStateOf(true) }
    var score by remember(item.char) { mutableStateOf<Int?>(null) }
    var canvasPx by remember { mutableFloatStateOf(0f) }
    val rewarded = remember { mutableSetOf<String>() }
    val typeface = remember { ResourcesCompat.getFont(context, R.font.klee_one) ?: Typeface.DEFAULT }

    val listState = rememberLazyListState()
    LaunchedEffect(set, index) { listState.animateScrollToItem((index - 3).coerceAtLeast(0)) }

    ScreenScaffold(title = "Writing practice", onBack = onBack) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            ChoiceChips(WriteSet.entries.map { it.label }, set.label, { label -> set = WriteSet.entries.first { it.label == label } })

            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                itemsIndexed(list, key = { _, w -> w.char }) { i, w ->
                    val selected = i == index
                    Surface(
                        onClick = { index = i },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) scheme.primary else scheme.surfaceContainerLowest,
                        modifier = Modifier.size(46.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(w.char, style = jp(22), color = if (selected) scheme.onPrimary else scheme.onSurface)
                            if (w.char in progress.writtenChars) {
                                Box(
                                    Modifier.align(Alignment.TopEnd).padding(4.dp).size(5.dp)
                                        .background(if (selected) scheme.onPrimary else scheme.tertiary, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (item.meaning != null) item.meaning else item.reading,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        (if (item.meaning != null) item.reading + "   " else "") + "${item.strokes} stroke${if (item.strokes == 1) "" else "s"}",
                        style = if (item.meaning != null) jp(16) else MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
                SpeakButton(if (set == WriteSet.KANJI) item.reading.substringBefore("  ").substringBefore("(").substringBefore("・") else item.char)
            }

            // The practice square.
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .padding(16.dp)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .background(extra.paper, RoundedCornerShape(20.dp))
                        .masu(extra.grid, radius = 20.dp, width = 1.5.dp)
                        .then(if (score != null) Modifier.border(2.dp, if (score!! >= PASS) extra.correct else extra.wrong, RoundedCornerShape(20.dp)) else Modifier)
                        .onSizeChanged { canvasPx = it.width.toFloat() }
                        .pointerInput(item.char) {
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                down.consume()
                                val points = mutableListOf(down.position)
                                current = points.toList()
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                    if (!change.pressed) break
                                    points += change.position
                                    change.consume()
                                    current = points.toList()
                                }
                                strokes.add(points.toList())
                                current = emptyList()
                                score = null
                            }
                        },
                ) {
                    val guide = scheme.primary.copy(alpha = 0.16f)
                    val ink = scheme.onSurface
                    Canvas(Modifier.fillMaxSize()) {
                        if (showGuide) {
                            drawIntoCanvas { c ->
                                val paint = HandwritingGrader.glyphPaint(typeface, size.width, guide.toArgb())
                                c.nativeCanvas.drawText(item.char, size.width / 2f, HandwritingGrader.baseline(paint, size.width), paint)
                            }
                        }
                        val width = size.width * HandwritingGrader.STROKE_SCALE
                        (strokes + listOf(current)).forEach { stroke ->
                            when {
                                stroke.isEmpty() -> Unit
                                stroke.size == 1 -> drawCircle(ink, radius = width / 2f, center = stroke[0])
                                else -> drawPath(
                                    smoothPath(stroke), ink,
                                    style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round),
                                )
                            }
                        }
                    }
                }
            }

            // Feedback.
            val s = score
            Box(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 20.dp), contentAlignment = Alignment.CenterStart) {
                if (s != null) {
                    Column {
                        Text(
                            when {
                                s >= 75 -> "$s% — excellent, clean and well placed."
                                s >= PASS -> "$s% — good. Compare with the guide and try once more."
                                else -> "$s% — trace slowly over the guide and try again."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (s >= PASS) extra.correct else extra.wrong,
                        )
                        if (strokes.size != item.strokes) {
                            Text(
                                "You drew ${strokes.size} stroke${if (strokes.size == 1) "" else "s"}; this character has ${item.strokes}. Lift your finger between strokes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    Text(
                        if (showGuide) "Trace the grey character. Strokes go top to bottom, left to right."
                        else "From memory: write it without the guide.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(start = 12.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex); score = null }) {
                    Icon(painterResource(R.drawable.ic_undo), contentDescription = "Undo last stroke")
                }
                TextButton(onClick = { strokes.clear(); score = null }) { Text("Clear") }
                FilterChip(selected = showGuide, onClick = { showGuide = !showGuide }, label = { Text("Guide") })
                Spacer(Modifier.weight(1f))
                val passed = s != null && s >= PASS
                Button(
                    enabled = strokes.isNotEmpty() || passed,
                    onClick = {
                        if (passed) {
                            index = (index + 1) % list.size
                        } else {
                            val result = HandwritingGrader.grade(item.char, typeface, strokes.toList(), canvasPx)
                            score = result
                            if (result >= PASS && rewarded.add(item.char)) {
                                app.repository.markWritten(item.char)
                                app.repository.addXp(WRITE_XP)
                            }
                        }
                    },
                ) { Text(if (passed) "Next" else "Check") }
            }
        }
    }
}

private fun smoothPath(points: List<Offset>): Path = Path().apply {
    moveTo(points[0].x, points[0].y)
    for (i in 1 until points.size) {
        val a = points[i - 1]
        val b = points[i]
        quadraticTo(a.x, a.y, (a.x + b.x) / 2f, (a.y + b.y) / 2f)
    }
    val last = points.last()
    lineTo(last.x, last.y)
}

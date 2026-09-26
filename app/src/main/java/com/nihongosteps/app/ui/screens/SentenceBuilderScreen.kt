@file:OptIn(ExperimentalLayoutApi::class)

package com.nihongosteps.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nihongosteps.app.data.SentenceData
import com.nihongosteps.app.data.SentenceTask
import com.nihongosteps.app.data.Tile
import com.nihongosteps.app.ui.LocalApp
import com.nihongosteps.app.ui.ResultPanel
import com.nihongosteps.app.ui.ScreenScaffold
import com.nihongosteps.app.ui.rememberProgress
import com.nihongosteps.app.ui.theme.LocalExtraColors
import com.nihongosteps.app.ui.theme.jp

private const val SENTENCE_XP = 10

@Composable
fun SentenceBuilderScreen(onBack: () -> Unit) {
    val app = LocalApp.current
    val progress by rememberProgress()
    var round by remember { mutableIntStateOf(0) }
    val tasks = remember(round) { SentenceData.tasks.shuffled().take(SENTENCE_ROUND) }
    var index by remember(round) { mutableIntStateOf(0) }
    var correct by remember(round) { mutableIntStateOf(0) }
    var finished by remember(round) { mutableStateOf(false) }

    ScreenScaffold(title = "Sentence builder", onBack = onBack) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            if (finished) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    ResultPanel(correct, tasks.size, correct * SENTENCE_XP, onAgain = { round++ }, onDone = onBack)
                }
            } else {
                key(round, index) {
                    SentenceTaskView(tasks[index], index, tasks.size, progress.showRomaji) { ok ->
                        if (ok) correct++
                        if (index + 1 >= tasks.size) {
                            app.repository.addXp(correct * SENTENCE_XP)
                            app.repository.recordScore("translate", correct)
                            finished = true
                        } else {
                            index++
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SentenceTaskView(task: SentenceTask, position: Int, total: Int, showRomaji: Boolean, onDone: (Boolean) -> Unit) {
    val speaker = LocalApp.current.speaker
    val extra = LocalExtraColors.current
    val bank = remember(task) { task.tiles.indices.shuffled() }
    val chosen = remember(task) { mutableStateListOf<Int>() }
    var result by remember(task) { mutableStateOf<Boolean?>(null) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        LinearProgressIndicator(
            progress = { (position + (if (result != null) 1 else 0)) / total.toFloat() },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(Modifier.height(20.dp))
        Text("Say it in Japanese", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(task.en, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))

        // Answer line
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(
                if (result == null) 1.dp else 2.dp,
                when (result) { null -> MaterialTheme.colorScheme.outlineVariant; true -> extra.correct; false -> extra.wrong },
            ),
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
        ) {
            FlowRow(
                Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (chosen.isEmpty()) {
                    Text(
                        "Tap the tiles below in the right order.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp),
                    )
                }
                chosen.forEach { i ->
                    TileChip(task.tiles[i], showRomaji, enabled = result == null) { chosen.remove(i) }
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        // Tile bank
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            bank.forEach { i ->
                val used = i in chosen
                TileChip(task.tiles[i], showRomaji, enabled = !used && result == null, faded = used) { chosen.add(i) }
            }
        }
        Spacer(Modifier.height(20.dp))

        when (result) {
            true -> Feedback("Correct!", task.note, extra.correct)
            false -> {
                val answer = task.mainAnswer
                val romaji = answer.map { a -> task.tiles.first { it.jp == a }.romaji }
                Feedback(
                    "Correct answer: " + answer.joinToString(" "),
                    (if (showRomaji) romaji.joinToString(" ") + "\n" else "") + task.note,
                    extra.wrong,
                    jpTitle = true,
                )
            }
            null -> Unit
        }
        Spacer(Modifier.height(16.dp))
        Button(
            enabled = result != null || chosen.isNotEmpty(),
            onClick = {
                val r = result
                if (r == null) {
                    val ok = task.isCorrect(chosen.map { task.tiles[it].jp })
                    result = ok
                    speaker.speak(task.mainAnswer.joinToString(""))
                } else {
                    onDone(r)
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) { Text(if (result == null) "Check" else "Continue") }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Feedback(title: String, body: String, color: androidx.compose.ui.graphics.Color, jpTitle: Boolean = false) {
    Column {
        Text(title, style = if (jpTitle) jp(20, FontWeight.SemiBold) else MaterialTheme.typography.titleMedium, color = color)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TileChip(tile: Tile, showRomaji: Boolean, enabled: Boolean, faded: Boolean = false, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = if (faded) 0.dp else 1.dp,
        modifier = Modifier.alpha(if (faded) 0.25f else 1f),
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(tile.jp, style = jp(21))
            if (showRomaji) Text(tile.romaji, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

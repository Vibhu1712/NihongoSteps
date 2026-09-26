package com.nihongosteps.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nihongosteps.app.data.Check
import com.nihongosteps.app.data.GrammarData
import com.nihongosteps.app.ui.LocalApp
import com.nihongosteps.app.ui.OptionButton
import com.nihongosteps.app.ui.OptionState
import com.nihongosteps.app.ui.ScreenScaffold
import com.nihongosteps.app.ui.SpeakButton
import com.nihongosteps.app.ui.masu
import com.nihongosteps.app.ui.rememberProgress
import com.nihongosteps.app.ui.theme.LocalExtraColors
import com.nihongosteps.app.ui.theme.jp

private const val LESSON_XP = 20

@Composable
fun GrammarListScreen(onBack: () -> Unit, open: (String) -> Unit) {
    val progress by rememberProgress()
    ScreenScaffold(title = "Grammar", onBack = onBack) { pad ->
        LazyColumn(
            Modifier.padding(pad).fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    "Lessons build on each other, so work through them in order. Finish the check at the end of a lesson to mark it done.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }
            itemsIndexed(GrammarData.lessons, key = { _, l -> l.id }) { i, lesson ->
                val done = lesson.id in progress.completedLessons
                Surface(
                    onClick = { open(lesson.id) },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).masu(LocalExtraColors.current.grid, radius = 8.dp), contentAlignment = Alignment.Center) {
                            Text("${i + 1}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(lesson.jpTitle, style = jp(20, FontWeight.SemiBold))
                            Text(lesson.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (done) Icon(Icons.Filled.CheckCircle, contentDescription = "Done", tint = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
    }
}

@Composable
fun GrammarDetailScreen(id: String, onBack: () -> Unit, openNext: (String) -> Unit) {
    val lesson = GrammarData.byId(id) ?: return
    val app = LocalApp.current
    val progress by rememberProgress()
    val index = GrammarData.lessons.indexOf(lesson)
    val next = GrammarData.lessons.getOrNull(index + 1)

    val answers = remember(id) { mutableStateListOf<Int?>().apply { repeat(lesson.checks.size) { add(null) } } }
    var awarded by remember(id) { mutableStateOf(false) }
    var earned by remember(id) { mutableStateOf(false) }
    val allAnswered = answers.all { it != null }
    val allCorrect = allAnswered && lesson.checks.indices.all { answers[it] == lesson.checks[it].answer }

    LaunchedEffect(allCorrect) {
        if (allCorrect && !awarded) {
            awarded = true
            if (lesson.id !in progress.completedLessons) {
                app.repository.completeLesson(lesson.id)
                app.repository.addXp(LESSON_XP)
                earned = true
            }
        }
    }

    ScreenScaffold(title = "Lesson ${index + 1}", onBack = onBack) { pad ->
        LazyColumn(
            Modifier.padding(pad).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text(lesson.jpTitle, style = jp(32, FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                Text(lesson.title, style = MaterialTheme.typography.titleLarge)
            }
            item {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Pattern", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(lesson.pattern, style = jp(20, FontWeight.SemiBold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    lesson.explanation.forEach { Text(it, style = MaterialTheme.typography.bodyLarge) }
                }
            }
            item { Text("Examples", style = MaterialTheme.typography.titleMedium) }
            lesson.examples.forEach { ex ->
                item {
                    Surface(
                        onClick = { app.speaker.speak(ex.jp) },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(ex.jp, style = jp(22))
                                if (progress.showRomaji) Text(ex.romaji, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(ex.en, style = MaterialTheme.typography.bodyLarge)
                            }
                            SpeakButton(ex.jp)
                        }
                    }
                }
            }
            item {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Watch out", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(Modifier.height(4.dp))
                        Text(lesson.tip, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Text("Check yourself", style = MaterialTheme.typography.titleMedium)
            }
            lesson.checks.forEachIndexed { qi, check ->
                item(key = "check-$id-$qi") {
                    CheckCard(check, answers[qi]) { answers[qi] = it }
                }
            }
            if (allAnswered) {
                item {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        if (allCorrect) {
                            Text(
                                if (earned) "Lesson complete · +$LESSON_XP XP" else "Lesson complete.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Spacer(Modifier.height(12.dp))
                            if (next != null) Button(onClick = { openNext(next.id) }) { Text("Next: ${next.title}") }
                            else Button(onClick = onBack) { Text("Back to lessons") }
                        } else {
                            Text("Not quite — read the notes above and try again.", style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(onClick = { for (i in answers.indices) answers[i] = null }) { Text("Try again") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckCard(check: Check, chosen: Int?, onChoose: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(check.question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        check.options.forEachIndexed { i, opt ->
            val state = when {
                chosen == null -> OptionState.Idle
                i == check.answer -> OptionState.Correct
                i == chosen -> OptionState.Wrong
                else -> OptionState.Faded
            }
            val isJp = opt.any { it.code in 0x3040..0x30FF || it.code in 0x4E00..0x9FFF }
            OptionButton(opt, state, onClick = { onChoose(i) }, japanese = isJp)
        }
        if (chosen != null) {
            Text(
                (if (chosen == check.answer) "Correct. " else "Not quite. ") + check.why,
                style = MaterialTheme.typography.bodyMedium,
                color = if (chosen == check.answer) LocalExtraColors.current.correct else LocalExtraColors.current.wrong,
            )
        }
    }
}

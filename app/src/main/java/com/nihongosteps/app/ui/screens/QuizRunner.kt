package com.nihongosteps.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nihongosteps.app.R
import com.nihongosteps.app.ui.LocalApp
import com.nihongosteps.app.ui.OptionButton
import com.nihongosteps.app.ui.OptionState
import com.nihongosteps.app.ui.masu
import com.nihongosteps.app.ui.theme.LocalExtraColors
import com.nihongosteps.app.ui.theme.jp
import kotlinx.coroutines.delay

data class QuizQuestion(
    val prompt: String,
    val promptIsJp: Boolean,
    val options: List<String>,
    val optionsAreJp: Boolean,
    val answer: Int,
    val speakText: String?,
    val promptSub: String? = null,
    val optionSubs: List<String?> = emptyList(),
    val listenOnly: Boolean = false,
    val reveal: String? = null,
)

/** Plays a list of multiple-choice questions and reports how many were answered correctly. */
@Composable
fun QuizRunner(questions: List<QuizQuestion>, onFinish: (correct: Int, bestCombo: Int) -> Unit) {
    val speaker = LocalApp.current.speaker
    val extra = LocalExtraColors.current
    var index by remember(questions) { mutableIntStateOf(0) }
    var chosen by remember(questions) { mutableStateOf<Int?>(null) }
    var correct by remember(questions) { mutableIntStateOf(0) }
    var combo by remember(questions) { mutableIntStateOf(0) }
    var bestCombo by remember(questions) { mutableIntStateOf(0) }

    val q = questions[index]

    fun next() {
        if (index + 1 >= questions.size) onFinish(correct, bestCombo)
        else { index++; chosen = null }
    }

    LaunchedEffect(questions, index) {
        if (q.listenOnly && q.speakText != null) { delay(250); speaker.speak(q.speakText) }
    }
    LaunchedEffect(chosen) {
        if (chosen != null && chosen == q.answer) { delay(900); next() }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        LinearProgressIndicator(
            progress = { (index + (if (chosen != null) 1 else 0)) / questions.size.toFloat() },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("${index + 1} of ${questions.size}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            if (combo >= 2) Text("$combo in a row", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        }
        Spacer(Modifier.height(20.dp))

        // Prompt
        Box(Modifier.fillMaxWidth().height(190.dp), contentAlignment = Alignment.Center) {
            when {
                q.listenOnly -> FilledTonalIconButton(
                    onClick = { q.speakText?.let(speaker::speak) },
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                ) { Icon(painterResource(R.drawable.ic_volume), contentDescription = "Play again", modifier = Modifier.size(48.dp)) }

                q.promptIsJp && q.prompt.length <= 2 -> Box(
                    Modifier.size(170.dp).masu(extra.grid, radius = 18.dp),
                    contentAlignment = Alignment.Center,
                ) { Text(q.prompt, style = jp(96)) }

                else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        q.prompt,
                        style = if (q.promptIsJp) jp(if (q.prompt.length > 6) 34 else 48) else MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )
                    if (q.promptSub != null) {
                        Text(q.promptSub, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Text(
            when {
                q.listenOnly -> "What did you hear?"
                q.optionsAreJp -> "Choose the Japanese"
                else -> "Choose the meaning"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))

        // Options: a 2×2 grid for short answers, a list otherwise.
        fun stateOf(i: Int) = when {
            chosen == null -> OptionState.Idle
            i == q.answer -> OptionState.Correct
            i == chosen -> OptionState.Wrong
            else -> OptionState.Faded
        }
        fun choose(i: Int) {
            if (chosen != null) return
            chosen = i
            if (i == q.answer) { correct++; combo++; if (combo > bestCombo) bestCombo = combo } else combo = 0
            q.speakText?.let(speaker::speak)
        }
        val short = q.options.all { it.length <= 4 } && q.optionSubs.all { it == null }
        if (short) {
            q.options.indices.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { i ->
                        OptionButton(q.options[i], stateOf(i), { choose(i) }, Modifier.weight(1f), japanese = q.optionsAreJp)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                q.options.forEachIndexed { i, o ->
                    OptionButton(o, stateOf(i), { choose(i) }, japanese = q.optionsAreJp, sub = q.optionSubs.getOrNull(i))
                }
            }
        }

        if (chosen != null) {
            Spacer(Modifier.height(16.dp))
            if (q.reveal != null) {
                Text(q.reveal, style = jp(18), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            if (chosen != q.answer) {
                Spacer(Modifier.height(12.dp))
                Button(onClick = { next() }, modifier = Modifier.fillMaxWidth()) { Text("Continue") }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

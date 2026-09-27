package com.nihongosteps.app.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nihongosteps.app.data.GrammarData
import com.nihongosteps.app.data.KanaData
import com.nihongosteps.app.data.Progress
import com.nihongosteps.app.data.Script
import com.nihongosteps.app.data.SentenceData
import com.nihongosteps.app.data.VocabData
import com.nihongosteps.app.data.XP_PER_LEVEL
import com.nihongosteps.app.ui.Routes
import com.nihongosteps.app.ui.masu
import com.nihongosteps.app.ui.rememberProgress
import com.nihongosteps.app.ui.theme.LocalExtraColors
import com.nihongosteps.app.ui.theme.jp

@Composable
fun LearnScreen(open: (String) -> Unit) {
    val progress by rememberProgress()
    val lessonsDone = GrammarData.lessons.count { it.id in progress.completedLessons }

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { TodayHeader(progress) }
        item { Spacer(Modifier.height(4.dp)) }
        if (progress.xp == 0 && progress.completedLessons.isEmpty() && progress.writtenChars.isEmpty()) {
            item { WelcomeCard(onClick = { open(Routes.FAQ) }) }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScriptCard("あ", Script.HIRAGANA, progress, Modifier.weight(1f)) { open(Routes.kana(Script.HIRAGANA)) }
                ScriptCard("ア", Script.KATAKANA, progress, Modifier.weight(1f)) { open(Routes.kana(Script.KATAKANA)) }
            }
        }
        item {
            PathCard("語", "Vocabulary", "${VocabData.words.size} everyday words in ${VocabData.categories.size} topics, with audio") { open(Routes.VOCAB) }
        }
        item {
            PathCard(
                "文", "Grammar", "$lessonsDone of ${GrammarData.lessons.size} lessons complete",
                fraction = lessonsDone / GrammarData.lessons.size.toFloat(),
            ) { open(Routes.GRAMMAR) }
        }
        item {
            PathCard("訳", "Translate", "Turn ${SentenceData.tasks.size} English sentences into Japanese, tile by tile") { open(Routes.TRANSLATE) }
        }
    }
}

@Composable
private fun WelcomeCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "New to Japanese?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Read a two-minute introduction to hiragana, katakana and kanji before you start.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun TodayHeader(progress: Progress) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("こんにちは", style = jp(34, FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
            Text(
                if (progress.streak > 0) "${progress.streak}-day streak · level ${progress.level}" else "Level ${progress.level} · start a streak today",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress.levelProgress },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                strokeCap = StrokeCap.Round,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                "${progress.xp % XP_PER_LEVEL} / $XP_PER_LEVEL XP to level ${progress.level + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Spacer(Modifier.width(20.dp))
        Box(Modifier.size(84.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress.goalProgress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 7.dp,
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${progress.todayXp}", style = MaterialTheme.typography.titleLarge)
                Text("of ${progress.dailyGoal}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ScriptCard(glyph: String, script: Script, progress: Progress, modifier: Modifier, onClick: () -> Unit) {
    val all = KanaData.all(script)
    val written = all.count { it.char in progress.writtenChars }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier,
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier.fillMaxWidth().aspectRatio(1f).masu(LocalExtraColors.current.grid, radius = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(glyph, style = jp(84), color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(12.dp))
            Text(script.label, style = MaterialTheme.typography.titleMedium)
            Text("$written of ${all.size} written", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PathCard(glyph: String, title: String, subtitle: String, fraction: Float? = null, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(64.dp).masu(LocalExtraColors.current.grid), contentAlignment = Alignment.Center) {
                Text(glyph, style = jp(36), color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (fraction != null) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

package com.nihongosteps.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nihongosteps.app.BuildConfig
import com.nihongosteps.app.data.GrammarData
import com.nihongosteps.app.data.KanaData
import com.nihongosteps.app.data.Script
import com.nihongosteps.app.ui.ChoiceChips
import com.nihongosteps.app.ui.LocalApp
import com.nihongosteps.app.ui.StatLine
import com.nihongosteps.app.ui.rememberProgress
import com.nihongosteps.app.ui.theme.LocalExtraColors
import com.nihongosteps.app.ui.theme.jp

private val GOALS = mapOf("Casual · 20 XP" to 20, "Regular · 50 XP" to 50, "Serious · 100 XP" to 100)

private const val PRIVACY = "Nihongo Steps does not collect, store or share any personal data.\n\n" +
    "• Your progress and settings are saved only on your device. If Android backup is on, they are included in your own Google backup so they survive a phone change.\n" +
    "• The app has no accounts, no ads, no analytics and no internet access.\n" +
    "• Pronunciation is produced by your phone's built-in text-to-speech engine.\n\n" +
    "Questions: contact the developer through the Google Play listing."

@Composable
fun MeScreen() {
    val app = LocalApp.current
    val context = LocalContext.current
    val progress by rememberProgress()
    var dialog by remember { mutableStateOf<String?>(null) }
    var rate by remember(progress.speechRate) { mutableFloatStateOf(progress.speechRate) }

    val totalChars = KanaData.all(Script.HIRAGANA).size + KanaData.all(Script.KATAKANA).size + KanaData.kanji.size

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp),
    ) {
        item {
            Text("きろく", style = jp(34, FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
            Text("Your progress", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
        }
        item {
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLowest, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    StatLine("Level", "${progress.level}")
                    StatLine("Total XP", "${progress.xp}")
                    StatLine("Current streak", if (progress.streak == 1) "1 day" else "${progress.streak} days")
                    StatLine("Grammar lessons", "${progress.completedLessons.size} / ${GrammarData.lessons.size}")
                    StatLine("Characters written", "${progress.writtenChars.size} / $totalChars")
                    progress.bestScores["kana"]?.let { StatLine("Best kana sprint", "$it / 15") }
                    progress.bestScores["vocab"]?.let { StatLine("Best word quiz", "$it / 10") }
                    progress.bestScores["match"]?.let { StatLine("Best memory match", "$it points") }
                    progress.bestScores["translate"]?.let { StatLine("Best sentence builder", "$it / $SENTENCE_ROUND") }
                }
            }
        }

        item { Header("Study settings") }
        item {
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Show romaji", style = MaterialTheme.typography.bodyLarge)
                    Text("Turn off once you can read kana, to stop leaning on it.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = progress.showRomaji, onCheckedChange = { app.repository.setShowRomaji(it) })
            }
        }
        item {
            Text("Daily goal", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            val selected = GOALS.entries.firstOrNull { it.value == progress.dailyGoal }?.key ?: GOALS.keys.elementAt(1)
            ChoiceChips(GOALS.keys.toList(), selected, { app.repository.setDailyGoal(GOALS.getValue(it)) }, Modifier.padding(start = 0.dp))
        }

        item { Header("Pronunciation") }
        item {
            if (app.speaker.available) {
                Text("Speaking speed", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = rate,
                    onValueChange = { rate = it },
                    valueRange = 0.5f..1.3f,
                    onValueChangeFinished = {
                        app.repository.setSpeechRate(rate)
                        app.speaker.rate = rate
                        app.speaker.speak("こんにちは")
                    },
                )
                Text(
                    when { rate < 0.75f -> "Slow"; rate < 1.05f -> "Natural"; else -> "Fast" },
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    "No Japanese voice is installed, so audio buttons are hidden. Install one to hear every word and sentence.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { app.speaker.openVoiceInstaller(context) }) { Text("Install Japanese voice") }
                Text(
                    "After installing, choose Japanese in your phone's text-to-speech settings and restart the app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        item { Header("About") }
        item {
            Column {
                TextButton(onClick = { dialog = "privacy" }) { Text("Privacy policy") }
                TextButton(onClick = { dialog = "licenses" }) { Text("Open-source licences") }
                TextButton(onClick = { dialog = "reset" }) { Text("Reset progress", color = LocalExtraColors.current.wrong) }
                Text(
                    "Nihongo Steps ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                )
            }
        }
    }

    when (dialog) {
        "privacy" -> InfoDialog("Privacy policy", PRIVACY) { dialog = null }
        "licenses" -> {
            val text = remember {
                runCatching { context.assets.open("licenses/KleeOne-OFL.txt").bufferedReader().use { it.readText() } }.getOrDefault("")
            }
            InfoDialog("Open-source licences", "Klee One typeface by Fontworks Inc., used under the SIL Open Font License 1.1.\n\n$text") { dialog = null }
        }
        "reset" -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Reset all progress?") },
            text = { Text("XP, streak, finished lessons, written characters and best scores will be deleted. Your settings stay. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = { app.repository.resetProgress(); dialog = null }) {
                    Text("Reset", color = LocalExtraColors.current.wrong)
                }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun Header(text: String) {
    Column {
        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(16.dp))
        Text(text, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun InfoDialog(title: String, body: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                Text(body, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp))
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

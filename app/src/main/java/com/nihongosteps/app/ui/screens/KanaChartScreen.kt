package com.nihongosteps.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nihongosteps.app.data.Kana
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

@Composable
fun KanaChartScreen(script: Script, onBack: () -> Unit, onWrite: (String) -> Unit, onQuiz: () -> Unit) {
    val progress by rememberProgress()
    val speaker = LocalApp.current.speaker
    var voiced by rememberSaveable { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Kana?>(null) }

    ScreenScaffold(
        title = script.label,
        onBack = onBack,
        actions = { TextButton(onClick = onQuiz) { Text("Quiz me") } },
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            ChoiceChips(listOf("Basic", "Voiced ゛゜"), if (voiced) "Voiced ゛゜" else "Basic", { voiced = it != "Basic" })
            Text(
                if (voiced) "Two small marks (゛) make a sound voiced: か ka → が ga. A small circle (゜) turns h into p: は ha → ぱ pa."
                else "Tap a character to hear it and see how many strokes it takes. A dot means you've written it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            val cells = if (voiced) KanaData.voicedChart(script) else KanaData.chart(script)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(cells.size) { i ->
                    val k = cells[i]
                    if (k == null) {
                        Spacer(Modifier.aspectRatio(0.82f))
                    } else {
                        KanaCell(k, progress.showRomaji, k.char in progress.writtenChars) {
                            selected = k
                            speaker.speak(k.char)
                        }
                    }
                }
            }
        }
    }

    selected?.let { k ->
        KanaDialog(k, onDismiss = { selected = null }, onWrite = { selected = null; onWrite(k.char) })
    }
}

@Composable
private fun KanaCell(k: Kana, showRomaji: Boolean, written: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.aspectRatio(0.82f),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(k.char, style = jp(28), color = MaterialTheme.colorScheme.onSurface)
                if (showRomaji) {
                    Text(k.romaji, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (written) {
                Box(
                    Modifier.align(Alignment.TopEnd).padding(7.dp).size(7.dp)
                        .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun KanaDialog(k: Kana, onDismiss: () -> Unit, onWrite: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = onWrite) { Text("Practise writing") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        text = {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(150.dp).masu(LocalExtraColors.current.grid, radius = 16.dp), contentAlignment = Alignment.Center) {
                    Text(k.char, style = jp(96))
                }
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(k.romaji, style = MaterialTheme.typography.headlineMedium)
                    SpeakButton(k.char)
                }
                Text(
                    "${k.strokes} stroke${if (k.strokes == 1) "" else "s"} · ${k.script.label}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

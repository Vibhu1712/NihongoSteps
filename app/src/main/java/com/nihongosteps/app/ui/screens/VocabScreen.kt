package com.nihongosteps.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nihongosteps.app.data.VocabData
import com.nihongosteps.app.data.Word
import com.nihongosteps.app.ui.ChoiceChips
import com.nihongosteps.app.ui.LocalApp
import com.nihongosteps.app.ui.ScreenScaffold
import com.nihongosteps.app.ui.SpeakButton
import com.nihongosteps.app.ui.rememberProgress
import com.nihongosteps.app.ui.theme.jp

@Composable
fun VocabScreen(onBack: () -> Unit) {
    val progress by rememberProgress()
    val speaker = LocalApp.current.speaker
    var category by rememberSaveable { mutableStateOf("All") }
    var query by rememberSaveable { mutableStateOf("") }

    val words = VocabData.words.filter { w ->
        (category == "All" || w.category == category) &&
            (query.isBlank() || listOf(w.en, w.romaji, w.kana, w.jp).any { it.contains(query.trim(), ignoreCase = true) })
    }

    ScreenScaffold(title = "Vocabulary", onBack = onBack) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search in English, romaji or kana") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            )
            ChoiceChips(listOf("All") + VocabData.categories, category, { category = it }, Modifier.padding(vertical = 8.dp))
            if (words.isEmpty()) {
                Text(
                    "No words match \"$query\". Try a shorter search or pick All.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(words, key = { it.category + it.jp }) { w ->
                    WordRow(w, progress.showRomaji) { speaker.speak(w.kana) }
                }
            }
        }
    }
}

@Composable
private fun WordRow(w: Word, showRomaji: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(w.jp, style = jp(24), color = MaterialTheme.colorScheme.onSurface)
                val reading = listOfNotNull(w.kana.takeIf { w.hasKanji }, w.romaji.takeIf { showRomaji }).joinToString("  ")
                if (reading.isNotEmpty()) {
                    Text(reading, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(w.en, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.End)
                if (w.note.isNotEmpty()) {
                    Text(w.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.End)
                }
            }
            SpeakButton(w.kana)
        }
    }
}

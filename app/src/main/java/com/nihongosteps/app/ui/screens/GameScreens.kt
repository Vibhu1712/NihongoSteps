package com.nihongosteps.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nihongosteps.app.data.Kana
import com.nihongosteps.app.data.KanaData
import com.nihongosteps.app.data.Script
import com.nihongosteps.app.data.SentenceData
import com.nihongosteps.app.data.VocabData
import com.nihongosteps.app.data.Word
import com.nihongosteps.app.ui.ChoiceChips
import com.nihongosteps.app.ui.LocalApp
import com.nihongosteps.app.ui.ResultPanel
import com.nihongosteps.app.ui.Routes
import com.nihongosteps.app.ui.ScreenScaffold
import com.nihongosteps.app.ui.masu
import com.nihongosteps.app.ui.rememberProgress
import com.nihongosteps.app.ui.theme.LocalExtraColors
import com.nihongosteps.app.ui.theme.jp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class Phase { Setup, Play, Result }

// ─────────────────────────────── Play hub ───────────────────────────────

@Composable
fun PlayScreen(open: (String) -> Unit) {
    val progress by rememberProgress()
    fun best(key: String) = progress.bestScores[key]
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("あそぶ", style = jp(34, androidx.compose.ui.text.font.FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
            Text("Short rounds that reuse what you've studied.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
        }
        item {
            PathCard("あ", "Kana sprint", "15 quick questions" + (best("kana")?.let { " · best $it/15" } ?: "")) { open(Routes.GAME_KANA) }
        }
        item {
            PathCard("語", "Word quiz", "Meaning, reading or listening" + (best("vocab")?.let { " · best $it/10" } ?: "")) { open(Routes.GAME_VOCAB) }
        }
        item {
            PathCard("対", "Memory match", "Flip cards to find the pairs" + (best("match")?.let { " · best $it points" } ?: "")) { open(Routes.GAME_MATCH) }
        }
        item {
            PathCard("訳", "Sentence builder", "English to Japanese, tile by tile" + (best("translate")?.let { " · best $it/8" } ?: "")) { open(Routes.TRANSLATE) }
        }
    }
}

@Composable
private fun SetupColumn(glyph: String, intro: String, onStart: () -> Unit, startEnabled: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 8.dp)) {
        Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(72.dp).masu(LocalExtraColors.current.grid), contentAlignment = Alignment.Center) {
                Text(glyph, style = jp(42), color = MaterialTheme.colorScheme.secondary)
            }
            Text(intro, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp))
        }
        Spacer(Modifier.height(20.dp))
        content()
        Spacer(Modifier.height(28.dp))
        Button(onClick = onStart, enabled = startEnabled, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(52.dp)) {
            Text("Start")
        }
    }
}

@Composable
private fun SettingLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 6.dp))
}

// ─────────────────────────────── Kana sprint ───────────────────────────────

private fun kanaQuestions(pool: List<Kana>, reverse: Boolean, n: Int = 15): List<QuizQuestion> =
    pool.shuffled().take(n).map { k ->
        val distract = pool.filter { it.romaji != k.romaji }.distinctBy { it.romaji }.shuffled().take(3)
        val opts = (distract + k).shuffled()
        if (!reverse) {
            QuizQuestion(k.char, true, opts.map { it.romaji }, false, opts.indexOf(k), k.char)
        } else {
            QuizQuestion(k.romaji, false, opts.map { it.char }, true, opts.indexOf(k), k.char)
        }
    }

@Composable
fun KanaQuizScreen(onBack: () -> Unit) {
    val app = LocalApp.current
    var phase by remember { mutableStateOf(Phase.Setup) }
    var script by rememberSaveable { mutableStateOf("Hiragana") }
    var direction by rememberSaveable { mutableStateOf("Kana → romaji") }
    var voiced by rememberSaveable { mutableStateOf(false) }
    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var result by remember { mutableStateOf(Triple(0, 0, 0)) } // correct, combo, xp

    fun build() {
        val scripts = when (script) { "Hiragana" -> listOf(Script.HIRAGANA); "Katakana" -> listOf(Script.KATAKANA); else -> Script.entries }
        val pool = scripts.flatMap { if (voiced) KanaData.all(it) else KanaData.basic(it) }
        questions = kanaQuestions(pool, reverse = direction != "Kana → romaji")
        phase = Phase.Play
    }

    ScreenScaffold(title = "Kana sprint", onBack = onBack) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (phase) {
                Phase.Setup -> SetupColumn("あ", "Recognise characters fast. Answers are read aloud so you learn the sound too.", ::build) {
                    SettingLabel("Characters")
                    ChoiceChips(listOf("Hiragana", "Katakana", "Both"), script, { script = it })
                    SettingLabel("Direction")
                    ChoiceChips(listOf("Kana → romaji", "Romaji → kana"), direction, { direction = it })
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Include voiced (が, ぱ…)", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Switch(checked = voiced, onCheckedChange = { voiced = it })
                    }
                }
                Phase.Play -> QuizRunner(questions) { correct, combo ->
                    val xp = correct * 2 + combo
                    result = Triple(correct, combo, xp)
                    app.repository.addXp(xp)
                    app.repository.recordScore("kana", correct)
                    phase = Phase.Result
                }
                Phase.Result -> Column(Modifier.verticalScroll(rememberScrollState())) {
                    ResultPanel(result.first, questions.size, result.third, onAgain = ::build, onDone = onBack, detail = "Longest run: ${result.second} in a row")
                }
            }
        }
    }
}

// ─────────────────────────────── Word quiz ───────────────────────────────

private const val EN_JP = "English → Japanese"
private const val JP_EN = "Japanese → English"
private const val LISTEN = "Listening"

private fun reading(w: Word, showRomaji: Boolean): String? =
    listOfNotNull(w.kana.takeIf { w.hasKanji }, w.romaji.takeIf { showRomaji }).joinToString("  ").ifEmpty { null }

private fun vocabQuestions(mode: String, category: String, showRomaji: Boolean, n: Int = 10): List<QuizQuestion> {
    val pool = if (category == "All") VocabData.words else VocabData.words.filter { it.category == category }
    return pool.shuffled().take(n).map { w ->
        val sameCategory = VocabData.words.filter { it.category == w.category }.shuffled()
        val distract = (sameCategory + VocabData.words.shuffled())
            .filter { it.en != w.en && it.jp != w.jp }
            .distinctBy { it.en }
            .take(3)
        val opts = (distract + w).shuffled()
        val answer = opts.indexOf(w)
        val reveal = listOfNotNull(w.jp, w.kana.takeIf { w.hasKanji }, w.romaji.takeIf { showRomaji }).joinToString("  ") + "  —  " + w.en
        when (mode) {
            EN_JP -> QuizQuestion(w.en, false, opts.map { it.jp }, true, answer, w.kana, optionSubs = opts.map { reading(it, showRomaji) })
            JP_EN -> QuizQuestion(w.jp, true, opts.map { it.en }, false, answer, w.kana, promptSub = reading(w, showRomaji), reveal = reveal)
            else -> QuizQuestion(w.jp, true, opts.map { it.en }, false, answer, w.kana, listenOnly = true, reveal = reveal)
        }
    }
}

@Composable
fun VocabQuizScreen(onBack: () -> Unit) {
    val app = LocalApp.current
    val progress by rememberProgress()
    var phase by remember { mutableStateOf(Phase.Setup) }
    var mode by rememberSaveable { mutableStateOf(EN_JP) }
    var category by rememberSaveable { mutableStateOf("All") }
    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var result by remember { mutableStateOf(Triple(0, 0, 0)) }
    val canListen = app.speaker.available

    fun build() {
        questions = vocabQuestions(mode, category, progress.showRomaji)
        phase = Phase.Play
    }

    ScreenScaffold(title = "Word quiz", onBack = onBack) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (phase) {
                Phase.Setup -> SetupColumn("語", "Ten words per round, drawn from the vocabulary list.", ::build, startEnabled = mode != LISTEN || canListen) {
                    SettingLabel("Mode")
                    ChoiceChips(listOf(EN_JP, JP_EN, LISTEN), mode, { mode = it })
                    if (mode == LISTEN && !canListen) {
                        Text(
                            "Listening needs a Japanese text-to-speech voice. Install one from the Me tab, then come back.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalExtraColors.current.wrong,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        )
                    }
                    SettingLabel("Topic")
                    ChoiceChips(listOf("All") + VocabData.categories, category, { category = it })
                }
                Phase.Play -> QuizRunner(questions) { correct, combo ->
                    val xp = correct * 3 + combo
                    result = Triple(correct, combo, xp)
                    app.repository.addXp(xp)
                    app.repository.recordScore("vocab", correct)
                    phase = Phase.Result
                }
                Phase.Result -> Column(Modifier.verticalScroll(rememberScrollState())) {
                    ResultPanel(result.first, questions.size, result.third, onAgain = ::build, onDone = onBack, detail = "Longest run: ${result.second} in a row")
                }
            }
        }
    }
}

// ─────────────────────────────── Memory match ───────────────────────────────

private data class MatchCard(val id: Int, val pair: Int, val text: String, val jp: Boolean)

private fun buildDeck(set: String): List<MatchCard> {
    val pairs: List<Pair<String, String>> = when (set) {
        "Hiragana" -> KanaData.hiraganaBasic.shuffled().take(6).map { it.char to it.romaji }
        "Katakana" -> KanaData.katakanaBasic.shuffled().take(6).map { it.char to it.romaji }
        else -> VocabData.words.filter { it.en.length <= 16 && it.jp.length <= 5 }.shuffled().distinctBy { it.en }.take(6).map { it.jp to it.en }
    }
    return pairs.flatMapIndexed { i, (a, b) -> listOf(MatchCard(i * 2, i, a, true), MatchCard(i * 2 + 1, i, b, false)) }.shuffled()
}

@Composable
fun MatchGameScreen(onBack: () -> Unit) {
    val app = LocalApp.current
    val scope = rememberCoroutineScope()
    var phase by remember { mutableStateOf(Phase.Setup) }
    var set by rememberSaveable { mutableStateOf("Hiragana") }
    var deck by remember { mutableStateOf<List<MatchCard>>(emptyList()) }
    val faceUp = remember { mutableStateListOf<Int>() }
    val matched = remember { mutableStateListOf<Int>() }
    var moves by remember { mutableIntStateOf(0) }
    var seconds by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }

    fun start() {
        deck = buildDeck(set); faceUp.clear(); matched.clear(); moves = 0; seconds = 0
        phase = Phase.Play
    }

    fun tap(card: MatchCard) {
        if (card.id in matched || card.id in faceUp || faceUp.size == 2) return
        faceUp.add(card.id)
        if (card.jp) app.speaker.speak(card.text)
        if (faceUp.size == 2) {
            moves++
            val (a, b) = faceUp.map { id -> deck.first { it.id == id } }
            if (a.pair == b.pair) {
                matched.addAll(faceUp); faceUp.clear()
            } else {
                scope.launch { delay(850); faceUp.clear() }
            }
        }
    }

    LaunchedEffect(phase, deck) {
        while (phase == Phase.Play) { delay(1000); seconds++ }
    }
    LaunchedEffect(matched.size) {
        if (phase == Phase.Play && deck.isNotEmpty() && matched.size == deck.size) {
            delay(700)
            score = (100 - (moves - deck.size / 2) * 5).coerceIn(10, 100)
            app.repository.addXp(score / 10)
            app.repository.recordScore("match", score)
            phase = Phase.Result
        }
    }

    ScreenScaffold(title = "Memory match", onBack = onBack) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (phase) {
                Phase.Setup -> SetupColumn("対", "Turn over two cards at a time. Match each Japanese card with its reading or meaning in as few moves as you can.", ::start) {
                    SettingLabel("Cards")
                    ChoiceChips(listOf("Hiragana", "Katakana", "Words"), set, { set = it })
                }
                Phase.Play -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text("$moves moves", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text("%d:%02d".format(seconds / 60, seconds % 60), style = MaterialTheme.typography.titleMedium)
                    }
                    deck.chunked(3).forEach { row ->
                        Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { card ->
                                FlipCard(card, up = card.id in faceUp, matched = card.id in matched, onClick = { tap(card) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Phase.Result -> Column(Modifier.verticalScroll(rememberScrollState())) {
                    ResultPanel(score, 100, score / 10, onAgain = ::start, onDone = onBack, detail = "$moves moves in %d:%02d".format(seconds / 60, seconds % 60))
                }
            }
        }
    }
}

@Composable
private fun FlipCard(card: MatchCard, up: Boolean, matched: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val rotation by animateFloatAsState(if (up || matched) 180f else 0f, tween(320), label = "flip")
    val density = LocalDensity.current.density
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier
            .aspectRatio(0.8f)
            .graphicsLayer { rotationY = rotation; cameraDistance = 14f * density }
            .clickable(enabled = !up && !matched, onClick = onClick),
    ) {
        if (rotation <= 90f) {
            Box(Modifier.fillMaxSize().background(scheme.primary, shape).masu(scheme.onPrimary.copy(alpha = 0.22f), radius = 14.dp))
        } else {
            Surface(
                shape = shape,
                color = if (matched) scheme.tertiaryContainer else scheme.surfaceContainerLowest,
                border = BorderStroke(if (matched) 2.dp else 1.dp, if (matched) scheme.tertiary else scheme.outlineVariant),
                modifier = Modifier.fillMaxSize().graphicsLayer { rotationY = 180f },
            ) {
                Box(Modifier.padding(6.dp), contentAlignment = Alignment.Center) {
                    Text(
                        card.text,
                        style = if (card.jp) jp(if (card.text.length <= 2) 40 else 22) else MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = if (matched) scheme.onTertiaryContainer else scheme.onSurface,
                    )
                }
            }
        }
    }
}

// Keeps the sentence count visible to the hub.
val SENTENCE_ROUND = minOf(8, SentenceData.tasks.size)

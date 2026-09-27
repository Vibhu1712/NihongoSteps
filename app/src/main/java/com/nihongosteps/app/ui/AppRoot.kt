package com.nihongosteps.app.ui

import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nihongosteps.app.data.Script
import com.nihongosteps.app.ui.screens.FaqScreen
import com.nihongosteps.app.ui.screens.GrammarDetailScreen
import com.nihongosteps.app.ui.screens.GrammarListScreen
import com.nihongosteps.app.ui.screens.KanaChartScreen
import com.nihongosteps.app.ui.screens.KanaQuizScreen
import com.nihongosteps.app.ui.screens.LearnScreen
import com.nihongosteps.app.ui.screens.MatchGameScreen
import com.nihongosteps.app.ui.screens.MeScreen
import com.nihongosteps.app.ui.screens.PlayScreen
import com.nihongosteps.app.ui.screens.SentenceBuilderScreen
import com.nihongosteps.app.ui.screens.VocabQuizScreen
import com.nihongosteps.app.ui.screens.VocabScreen
import com.nihongosteps.app.ui.screens.WriteScreen

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("learn", "Learn", Icons.Filled.Home),
    Tab("write", "Write", Icons.Filled.Create),
    Tab("play", "Play", Icons.Filled.PlayArrow),
    Tab("me", "Me", Icons.Filled.Person),
)

object Routes {
    fun kana(script: Script) = "kana/${script.name}"
    fun grammar(id: String) = "grammar/$id"
    fun write(char: String) = "practice/" + Uri.encode(char)
    const val VOCAB = "vocab"
    const val GRAMMAR = "grammar"
    const val TRANSLATE = "translate"
    const val GAME_KANA = "game/kana"
    const val GAME_VOCAB = "game/vocab"
    const val GAME_MATCH = "game/match"
    const val FAQ = "faq"
}

@Composable
fun AppRoot() {
    val app = LocalApp.current
    val progress by rememberProgress()
    LaunchedEffect(progress.speechRate) { app.speaker.rate = progress.speechRate }

    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val currentTab = tabs.firstOrNull { it.route == route }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentTab != null) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == currentTab,
                            onClick = { nav.switchTab(tab.route) },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "learn",
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
        ) {
            composable("learn") { LearnScreen(open = { nav.navigate(it) }) }
            composable("write") { WriteScreen(startChar = null, onBack = null) }
            composable("practice/{char}") { e -> WriteScreen(startChar = e.arguments?.getString("char"), onBack = { nav.popBackStack() }) }
            composable("play") { PlayScreen(open = { nav.navigate(it) }) }
            composable("me") { MeScreen(openFaq = { nav.navigate(Routes.FAQ) }) }

            composable("kana/{script}") { e ->
                val script = runCatching { Script.valueOf(e.arguments?.getString("script") ?: "") }.getOrDefault(Script.HIRAGANA)
                KanaChartScreen(script, onBack = { nav.popBackStack() }, onWrite = { nav.navigate(Routes.write(it)) }, onQuiz = { nav.navigate(Routes.GAME_KANA) })
            }
            composable(Routes.VOCAB) { VocabScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.GRAMMAR) { GrammarListScreen(onBack = { nav.popBackStack() }, open = { nav.navigate(Routes.grammar(it)) }) }
            composable("grammar/{id}") { e ->
                GrammarDetailScreen(
                    id = e.arguments?.getString("id") ?: "",
                    onBack = { nav.popBackStack() },
                    openNext = { next -> nav.navigate(Routes.grammar(next)) { popUpTo(Routes.GRAMMAR) } },
                )
            }
            composable(Routes.TRANSLATE) { SentenceBuilderScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.GAME_KANA) { KanaQuizScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.GAME_VOCAB) { VocabQuizScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.GAME_MATCH) { MatchGameScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.FAQ) { FaqScreen(onBack = { nav.popBackStack() }) }
        }
    }
}

private fun NavHostController.switchTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

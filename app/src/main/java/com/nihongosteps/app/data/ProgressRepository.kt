package com.nihongosteps.app.data

import android.content.Context
import java.io.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "progress")

const val XP_PER_LEVEL = 250

data class Progress(
    val xp: Int = 0,
    val streak: Int = 0,
    val todayXp: Int = 0,
    val dailyGoal: Int = 50,
    val completedLessons: Set<String> = emptySet(),
    val writtenChars: Set<String> = emptySet(),
    val bestScores: Map<String, Int> = emptyMap(),
    val showRomaji: Boolean = true,
    val speechRate: Float = 0.9f,
    val loaded: Boolean = false,
) {
    val level: Int get() = xp / XP_PER_LEVEL + 1
    val levelProgress: Float get() = (xp % XP_PER_LEVEL) / XP_PER_LEVEL.toFloat()
    val goalProgress: Float get() = (todayXp / dailyGoal.toFloat()).coerceIn(0f, 1f)
}

/** Stores everything locally on the device with Jetpack DataStore. */
class ProgressRepository(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object K {
        val XP = intPreferencesKey("xp")
        val STREAK = intPreferencesKey("streak")
        val LAST_DAY = longPreferencesKey("last_day")
        val TODAY_XP = intPreferencesKey("today_xp")
        val GOAL = intPreferencesKey("daily_goal")
        val LESSONS = stringSetPreferencesKey("lessons_done")
        val WRITTEN = stringSetPreferencesKey("chars_written")
        val ROMAJI = booleanPreferencesKey("show_romaji")
        val RATE = floatPreferencesKey("speech_rate")
        const val BEST_PREFIX = "best_"
    }

    private fun today() = LocalDate.now().toEpochDay()

    val progress: Flow<Progress> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { p -> p.toProgress() }

    private fun Preferences.toProgress(): Progress {
        val today = today()
        val last = this[K.LAST_DAY] ?: -10L
        val best = asMap().entries
            .filter { it.key.name.startsWith(K.BEST_PREFIX) }
            .associate { it.key.name.removePrefix(K.BEST_PREFIX) to ((it.value as? Int) ?: 0) }
        return Progress(
            xp = this[K.XP] ?: 0,
            // A streak survives only if you practised today or yesterday.
            streak = if (last >= today - 1) this[K.STREAK] ?: 0 else 0,
            todayXp = if (last == today) this[K.TODAY_XP] ?: 0 else 0,
            dailyGoal = this[K.GOAL] ?: 50,
            completedLessons = this[K.LESSONS] ?: emptySet(),
            writtenChars = this[K.WRITTEN] ?: emptySet(),
            bestScores = best,
            showRomaji = this[K.ROMAJI] ?: true,
            speechRate = this[K.RATE] ?: 0.9f,
            loaded = true,
        )
    }

    private fun edit(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        scope.launch { context.dataStore.edit { block(it) } }
    }

    fun addXp(amount: Int) = edit { p ->
        if (amount <= 0) return@edit
        val today = today()
        val last = p[K.LAST_DAY] ?: -10L
        when (last) {
            today -> p[K.TODAY_XP] = (p[K.TODAY_XP] ?: 0) + amount
            today - 1 -> { p[K.STREAK] = (p[K.STREAK] ?: 0) + 1; p[K.TODAY_XP] = amount }
            else -> { p[K.STREAK] = 1; p[K.TODAY_XP] = amount }
        }
        p[K.LAST_DAY] = today
        p[K.XP] = (p[K.XP] ?: 0) + amount
    }

    fun recordScore(game: String, score: Int) = edit { p ->
        val key = intPreferencesKey(K.BEST_PREFIX + game)
        if (score > (p[key] ?: 0)) p[key] = score
    }

    fun completeLesson(id: String) = edit { p -> p[K.LESSONS] = (p[K.LESSONS] ?: emptySet()) + id }
    fun markWritten(char: String) = edit { p -> p[K.WRITTEN] = (p[K.WRITTEN] ?: emptySet()) + char }
    fun setShowRomaji(value: Boolean) = edit { it[K.ROMAJI] = value }
    fun setSpeechRate(value: Float) = edit { it[K.RATE] = value }
    fun setDailyGoal(value: Int) = edit { it[K.GOAL] = value }

    /** Clears learning progress but keeps the user's settings. */
    fun resetProgress() = edit { p ->
        val romaji = p[K.ROMAJI]; val rate = p[K.RATE]; val goal = p[K.GOAL]
        p.clear()
        romaji?.let { p[K.ROMAJI] = it }
        rate?.let { p[K.RATE] = it }
        goal?.let { p[K.GOAL] = it }
    }
}

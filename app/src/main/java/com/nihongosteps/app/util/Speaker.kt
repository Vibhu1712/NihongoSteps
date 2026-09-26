package com.nihongosteps.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/** Thin wrapper around the device's text-to-speech engine, set to Japanese. */
class Speaker(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val tts = TextToSpeech(appContext, this)

    /** true once a Japanese voice is ready. */
    var available by mutableStateOf(false)
        private set

    var rate: Float = 0.9f

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val result = tts.setLanguage(Locale.JAPANESE)
        available = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
    }

    fun speak(text: String) {
        if (!available) return
        // Strip helper symbols that would be read out loud.
        val clean = text.replace("〜", "").replace("・", "、")
        tts.setSpeechRate(rate)
        tts.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "nihongo")
    }

    /** Opens the system screen to download a Japanese voice. */
    fun openVoiceInstaller(context: Context) {
        val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            try {
                context.startActivity(Intent("com.android.settings.TTS_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (_: ActivityNotFoundException) { }
        }
    }
}

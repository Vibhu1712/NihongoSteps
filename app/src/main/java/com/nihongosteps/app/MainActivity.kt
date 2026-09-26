package com.nihongosteps.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nihongosteps.app.ui.AppRoot
import com.nihongosteps.app.ui.LocalApp
import com.nihongosteps.app.ui.theme.NihongoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as NihongoApp
        setContent {
            CompositionLocalProvider(LocalApp provides app) {
                NihongoTheme { AppRoot() }
            }
        }
    }
}

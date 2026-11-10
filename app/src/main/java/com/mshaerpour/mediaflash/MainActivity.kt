package com.mshaerpour.mediaflash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mshaerpour.mediaflash.data.SettingsStore
import com.mshaerpour.mediaflash.ui.LocalAppLanguage
import com.mshaerpour.mediaflash.ui.navigation.MediaFlashApp
import com.mshaerpour.mediaflash.ui.theme.MediaFlashTheme
import com.mshaerpour.mediaflash.util.LocaleManager

/**
 * Single Activity entry point for the Android TV app.
 * Hosts the Compose navigation graph and applies the dark theme.
 */
class MainActivity : ComponentActivity() {

    private lateinit var settingsStore: SettingsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        settingsStore = SettingsStore(applicationContext)

        setContent {
            val lang by settingsStore.language.collectAsState(initial = "fa")

            // Apply the locale to the running activity if it changed
            LocaleManager.applyLocale(this, lang)

            CompositionLocalProvider(LocalAppLanguage provides lang) {
                MediaFlashTheme {
                    MediaFlashApp()
                }
            }
        }
    }
}

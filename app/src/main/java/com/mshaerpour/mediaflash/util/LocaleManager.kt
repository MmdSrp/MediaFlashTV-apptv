package com.mshaerpour.mediaflash.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Sets the app locale programmatically without restarting the activity.
 * Used to switch between Persian and English from the Settings screen.
 */
object LocaleManager {

    fun applyLocale(context: Context, language: String) {
        val locale = Locale(if (language == "fa") "fa" else "en")
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        // Apply to the application context resources so anything queried from them updates
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        if (context is Activity) {
            context.window?.decorView?.layoutDirection =
                if (language == "fa") android.view.View.LAYOUT_DIRECTION_RTL
                else android.view.View.LAYOUT_DIRECTION_LTR
        }
    }
}

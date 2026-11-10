package com.mshaerpour.mediaflash.ui

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal holding the currently selected app language code.
 * Possible values: "fa" (Persian) or "en" (English).
 */
val LocalAppLanguage = staticCompositionLocalOf { "fa" }

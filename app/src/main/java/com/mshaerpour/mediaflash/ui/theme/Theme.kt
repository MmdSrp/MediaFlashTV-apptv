package com.mshaerpour.mediaflash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Windows 11-inspired dark theme.
 *
 * Surfaces are layered: Mica (deepest) → Acrylic card → Acrylic hover/focused.
 * Strokes are subtle to keep focus on content. The emerald accent is reserved
 * for selection, focus indication, and primary actions.
 */
private val WindowsDarkScheme = darkColorScheme(
    primary = Accent,
    onPrimary = TextOnAccent,
    primaryContainer = AcrylicCardAlt,
    onPrimaryContainer = TextPrimary,
    secondary = TextSecondary,
    onSecondary = MicaDeep,
    tertiary = Accent,
    onTertiary = TextOnAccent,
    background = MicaDeep,
    onBackground = TextPrimary,
    surface = Mica,
    onSurface = TextPrimary,
    surfaceVariant = AcrylicCard,
    onSurfaceVariant = TextSecondary,
    surfaceTint = Accent,
    outline = StrokeDefault,
    outlineVariant = StrokeSubtle,
    error = Error,
    onError = TextPrimary,
)

@Composable
fun MediaFlashTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = WindowsDarkScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = WindowsTypography,
        content = content,
    )
}

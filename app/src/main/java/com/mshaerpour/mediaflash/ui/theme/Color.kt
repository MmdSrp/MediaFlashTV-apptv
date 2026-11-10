package com.mshaerpour.mediaflash.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palette inspired by Windows 11 (Mica + Acrylic dark mode).
 *
 * Windows 11 uses layered surfaces: a deep Mica background, slightly-elevated
 * acrylic surfaces, and crisp accent strokes. We keep the brand emerald accent
 * but apply Microsoft-style surface hierarchy for a familiar Windows feel.
 *
 * Reference: Windows 11 Design Guidelines - Materials (Mica, Acrylic).
 */
// Mica background - deep charcoal with a hint of warmth
val MicaDeep = Color(0xFF1A1A1A)
val Mica = Color(0xFF1F1F1F)
val MicaElevated = Color(0xFF232323)

// Acrylic surfaces - semi-translucent feel (used solid here for non-blurring Compose)
val AcrylicBase = Color(0xFF2C2C2C)
val AcrylicCard = Color(0xFF2B2B2B)
val AcrylicCardAlt = Color(0xFF323232)
val AcrylicHover = Color(0xFF3A3A3A)
val AcrylicFocused = Color(0xFF383838)

// Stroke / divider colors (Microsoft "Stroke" tokens)
val StrokeSubtle = Color(0xFF383838)
val StrokeDefault = Color(0xFF404040)
val StrokeStrong = Color(0xFF565656)
val StrokeAccent = Color(0xFF00C896)

// Brand accent (emerald, kept from v1)
val Accent = Color(0xFF00C896)
val AccentBright = Color(0xFF39E5B5)
val AccentDim = Color(0xFF007F66)
val AccentHover = Color(0xFF33D4A8)
val AccentSubtle = Color(0x1A00C896)  // 10% accent

// Text colors (Microsoft "Text" tokens)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFC5C5C5)
val TextTertiary = Color(0xFF9A9A9A)
val TextDisabled = Color(0xFF6B6B6B)
val TextOnAccent = Color(0xFF0A1F18)

// Semantic
val Error = Color(0xFFFF4444)
val Warning = Color(0xFFFFB341)
val Success = Color(0xFF00C896)

// File type colors - Windows 11-style icons
val FolderTint = Color(0xFFFFD56B)      // Windows folder yellow
val VideoTint = Color(0xFF4CC2FF)       // Windows 11 accent blue
val AudioTint = Color(0xFFE97298)       // soft pink
val ImageTint = Color(0xFF7CE0C0)       // mint (brand-adjacent)
val SubtitleTint = Color(0xFFB7A4FF)
val DocumentTint = Color(0xFF74A9FF)
val ArchiveTint = Color(0xFFC7A77B)
val FileTint = Color(0xFF8B8B8B)

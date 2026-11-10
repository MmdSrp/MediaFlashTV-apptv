package com.mshaerpour.mediaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mshaerpour.mediaflash.BuildConfig
import com.mshaerpour.mediaflash.R
import com.mshaerpour.mediaflash.data.SettingsStore
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.AcrylicCard
import com.mshaerpour.mediaflash.ui.theme.AcrylicHover
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.StrokeDefault
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import kotlinx.coroutines.launch

/**
 * Windows 11 Settings-app inspired settings screen.
 *
 * Cards (Win11 Settings groups) with leading icon tile + label/description and
 * selectable content underneath. Language is a segmented pill control, About
 * shows version + author credit.
 */
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val store = remember { SettingsStore(context) }
    val lang by store.language.collectAsState(initial = "fa")
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MicaDeep)
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        // Page header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Accent, Accent.copy(alpha = 0.7f)),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(26.dp))
            }
            Text(
                text = stringResource(R.string.nav_settings),
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
            )
        }

        // Language group card
        SettingsCard(
            icon = Icons.Filled.Language,
            title = stringResource(R.string.settings_language),
            description = "Choose the app display language",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LanguagePill(
                    label = stringResource(R.string.settings_language_fa),
                    selected = lang == "fa",
                    onClick = { scope.launch { store.setLanguage("fa") } },
                )
                LanguagePill(
                    label = stringResource(R.string.settings_language_en),
                    selected = lang == "en",
                    onClick = { scope.launch { store.setLanguage("en") } },
                )
            }
        }

        // About group card
        SettingsCard(
            icon = Icons.Filled.Info,
            title = stringResource(R.string.settings_about),
            description = "App info and credits",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Media Flash",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                val version = try { BuildConfig.VERSION_NAME } catch (e: Exception) { "1.0.0" }
                Text(
                    text = stringResource(R.string.settings_version, version),
                    color = TextSecondary,
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AcrylicCard)
                        .border(1.dp, StrokeSubtle, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.settings_author),
                        color = Accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AcrylicCard)
            .border(1.dp, StrokeSubtle, RoundedCornerShape(8.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(description, color = TextTertiary, fontSize = 11.sp)
            }
        }
        Box(modifier = Modifier.padding(start = 48.dp)) { content() }
    }
}

@Composable
private fun LanguagePill(label: String, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) Accent.copy(alpha = 0.18f) else AcrylicHover.copy(alpha = 0.5f))
            .border(
                width = if (isFocused || selected) 2.dp else 1.dp,
                color = if (isFocused || selected) Accent else StrokeDefault,
                shape = RoundedCornerShape(6.dp),
            )
            .clickable { onClick() }
            .focusable(interactionSource = interactionSource),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Accent else TextPrimary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

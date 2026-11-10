package com.mshaerpour.mediaflash.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mshaerpour.mediaflash.R
import com.mshaerpour.mediaflash.ui.theme.AcrylicCardAlt
import com.mshaerpour.mediaflash.ui.theme.AcrylicHover
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.AccentSubtle
import com.mshaerpour.mediaflash.ui.theme.StrokeDefault
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary

/**
 * Navigation entries shown in the left sidebar.
 */
sealed class NavEntry(val route: String, val labelRes: Int, val icon: ImageVector, val iconSelected: ImageVector) {
    data object Home : NavEntry("home", R.string.nav_home, Icons.Outlined.Home, Icons.Filled.Home)
    data object Recent : NavEntry("recent", R.string.nav_recent, Icons.Outlined.History, Icons.Filled.History)
    data object Favorites : NavEntry("favorites", R.string.nav_favorites, Icons.Outlined.Star, Icons.Filled.Star)
    data object Search : NavEntry("search", R.string.nav_search, Icons.Outlined.Search, Icons.Filled.Search)
    data object Settings : NavEntry("settings", R.string.nav_settings, Icons.Outlined.Settings, Icons.Filled.Settings)
}

/**
 * Windows 11-style NavigationView sidebar.
 *
 * Features:
 * - Acrylic surface with subtle gradient
 * - Pill-shaped selection indicator (accent background, 6px corner radius)
 * - 3px vertical accent bar on the left edge of the selected item (Win11 marker)
 * - Icon switches from outlined to filled when selected
 * - Hover/Focus uses a lighter acrylic tone
 */
@Composable
fun Sidebar(
    currentRoute: String,
    onSelect: (NavEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    listOf(MicaElevated, MicaDeep.copy(alpha = 0.98f)),
                ),
            )
            .padding(vertical = 18.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        // Brand / app header
        BrandHeader()
        Spacer(Modifier.height(16.dp))
        SidebarButton(NavEntry.Home, currentRoute, onSelect)
        SidebarButton(NavEntry.Recent, currentRoute, onSelect)
        SidebarButton(NavEntry.Favorites, currentRoute, onSelect)
        SidebarButton(NavEntry.Search, currentRoute, onSelect)
        Spacer(Modifier.weight(1f))
        SidebarButton(NavEntry.Settings, currentRoute, onSelect)
    }
}

@Composable
private fun BrandHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        // Brand tile: square with rounded corners + "M" mark, Windows 11 Start style
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.linearGradient(listOf(Accent, Accent.copy(alpha = 0.78f))),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "M", color = MicaDeep, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = "Media Flash",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.1).sp,
            )
            Text(
                text = "USB Media Center",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun SidebarButton(
    entry: NavEntry,
    currentRoute: String,
    onSelect: (NavEntry) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isSelected = currentRoute == entry.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                when {
                    isSelected -> AccentSubtle
                    isFocused -> AcrylicHover.copy(alpha = 0.7f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = 1.dp,
                color = if (isFocused && !isSelected) StrokeDefault else Color.Transparent,
                shape = RoundedCornerShape(6.dp),
            )
            .clickable { onSelect(entry) }
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Selection marker (Win11 left pill)
            Box(
                modifier = Modifier
                    .width(if (isSelected) 3.dp else 3.dp)
                    .height(if (isSelected) 18.dp else 14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) Accent else Color.Transparent),
            )
            Icon(
                imageVector = if (isSelected) entry.iconSelected else entry.icon,
                contentDescription = null,
                tint = if (isSelected) Accent else TextSecondary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(entry.labelRes),
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

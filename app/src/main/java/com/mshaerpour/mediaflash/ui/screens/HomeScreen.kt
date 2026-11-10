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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.mshaerpour.mediaflash.data.UsbDrive
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.AcrylicCard
import com.mshaerpour.mediaflash.ui.theme.AcrylicFocused
import com.mshaerpour.mediaflash.ui.theme.AcrylicHover
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.StrokeDefault
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import com.mshaerpour.mediaflash.util.FileUtils

/**
 * Home screen styled after Windows 11 "Home" / File Explorer landing page.
 *
 * Layout:
 * - Big title + subtitle (Win11 page header)
 * - Refresh pill button row (Win11 command bar)
 * - USB drive cards (large, Win11 drive tile style)
 * - Quick access tiles (Win11 Home pinned items)
 */
@Composable
fun HomeScreen(
    sharedViewModel: SharedViewModel,
    onUsbSelected: (UsbDrive) -> Unit,
    onRecentClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val drives by sharedViewModel.drives.collectAsState()

    LaunchedEffect(Unit) { sharedViewModel.refreshDrives() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MicaDeep)
            .padding(horizontal = 28.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Header — Win11 page title style
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.select_usb_title),
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.3).sp,
            )
            Text(
                text = stringResource(R.string.select_usb_subtitle),
                color = TextSecondary,
                fontSize = 14.sp,
            )
        }

        // Command bar - refresh pill button
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CommandPillButton(
                icon = Icons.Filled.Refresh,
                label = stringResource(R.string.refresh),
                onClick = { sharedViewModel.refreshDrives() },
            )
        }

        // USB drives section
        if (drives.isEmpty()) {
            EmptyUsbState()
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 2.dp),
            ) {
                items(drives, key = { it.id }) { drive ->
                    UsbCard(drive = drive, onClick = { onUsbSelected(drive) })
                }
            }
        }

        // Quick Access section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Quick Access",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickTile(
                    label = stringResource(R.string.nav_recent),
                    icon = Icons.Outlined.History,
                    onClick = onRecentClick,
                )
                QuickTile(
                    label = stringResource(R.string.nav_favorites),
                    icon = Icons.Outlined.Star,
                    onClick = onFavoritesClick,
                )
                QuickTile(
                    label = stringResource(R.string.nav_search),
                    icon = Icons.Outlined.Search,
                    onClick = onSearchClick,
                )
                QuickTile(
                    label = stringResource(R.string.nav_settings),
                    icon = Icons.Outlined.Settings,
                    onClick = onSettingsClick,
                )
            }
        }
    }
}

@Composable
private fun CommandPillButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isFocused) AcrylicHover else AcrylicCard)
            .border(1.dp, if (isFocused) StrokeDefault else StrokeSubtle, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(16.dp))
            Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun UsbCard(drive: UsbDrive, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val used = drive.totalBytes - drive.freeBytes
    val usedPct = if (drive.totalBytes > 0) (used.toFloat() / drive.totalBytes.toFloat()) else 0f

    Column(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) AcrylicFocused else AcrylicCard)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Accent else StrokeSubtle,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Header row
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Usb,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = drive.name,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.1).sp,
                )
                Text(
                    text = "USB Drive",
                    color = TextTertiary,
                    fontSize = 11.sp,
                )
            }
        }

        // Capacity info
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${FileUtils.humanReadableSize(used)} of ${FileUtils.humanReadableSize(drive.totalBytes)} used",
                color = TextSecondary,
                fontSize = 11.sp,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(StrokeDefault),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(usedPct.coerceIn(0f, 1f))
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Accent),
                )
            }
            Text(
                text = "${FileUtils.humanReadableSize(drive.freeBytes)} free",
                color = TextTertiary,
                fontSize = 10.sp,
            )
        }

        // Open button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Accent)
                .clickable { onClick() }
                .focusable(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.usb_open),
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun EmptyUsbState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AcrylicCard)
            .border(1.dp, StrokeSubtle, RoundedCornerShape(8.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(StrokeSubtle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Usb,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = stringResource(R.string.no_usb_found),
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.no_usb_hint),
                color = TextSecondary,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun QuickTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) AcrylicHover else AcrylicCard)
            .border(
                width = 1.dp,
                color = if (isFocused) Accent else StrokeSubtle,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(18.dp))
            }
            Text(
                text = label,
                color = if (isFocused) TextPrimary else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

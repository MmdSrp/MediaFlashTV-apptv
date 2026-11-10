package com.mshaerpour.mediaflash.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.AcrylicCard
import com.mshaerpour.mediaflash.ui.theme.AcrylicHover
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.StrokeDefault
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary

/**
 * Windows 11-style breadcrumb path control.
 *
 * Renders a chain of clickable path segments separated by chevron icons,
 * inside a rounded acrylic pill — exactly like the Win11 File Explorer address bar.
 *
 * [crumbs] is the list of path segment labels (drive name first, folder names after).
 * [onCrumbClick] is called with the index of the clicked segment.
 */
@Composable
fun BreadcrumbBar(
    crumbs: List<String>,
    onCrumbClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MicaElevated)
            .border(1.dp, StrokeSubtle, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            crumbs.forEachIndexed { index, label ->
                if (index > 0) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(14.dp),
                    )
                }
                val interactionSource = remember { MutableInteractionSource() }
                val isFocused by interactionSource.collectIsFocusedAsState()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isFocused) AcrylicHover else Color.Transparent)
                        .clickable { onCrumbClick(index) }
                        .focusable(interactionSource = interactionSource)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = label,
                        color = if (index == crumbs.lastIndex) TextPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (index == crumbs.lastIndex) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/**
 * Windows 11 toolbar pill button (icon-only, square 36dp).
 */
@Composable
fun PillIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isFocused) AcrylicHover else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isFocused) StrokeDefault else Color.Transparent,
                shape = RoundedCornerShape(6.dp),
            )
            .clickable { onClick() }
            .focusable(interactionSource = interactionSource),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(18.dp))
    }
}

/**
 * Windows 11-style top app bar that holds:
 * - Back pill button (left)
 * - Breadcrumb (center, takes remaining space)
 * - Sort pill button (right)
 */
@Composable
fun BrowserTopBar(
    crumbs: List<String>,
    onCrumbClick: (Int) -> Unit,
    onBack: () -> Unit,
    onSort: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PillIconButton(
            icon = Icons.Outlined.ArrowBack,
            contentDescription = "Back",
            onClick = onBack,
        )
        BreadcrumbBar(
            crumbs = crumbs,
            onCrumbClick = onCrumbClick,
            modifier = Modifier.weight(1f),
        )
        PillIconButton(
            icon = Icons.Outlined.Sort,
            contentDescription = "Sort",
            onClick = onSort,
            tint = TextSecondary,
        )
        PillIconButton(
            icon = Icons.Outlined.GridView,
            contentDescription = "View",
            onClick = { /* TODO grid/list toggle */ },
            tint = TextSecondary,
        )
    }
}

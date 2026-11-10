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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.VideoFile
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mshaerpour.mediaflash.data.MediaFile
import com.mshaerpour.mediaflash.ui.theme.AcrylicCard
import com.mshaerpour.mediaflash.ui.theme.AcrylicFocused
import com.mshaerpour.mediaflash.ui.theme.AudioTint
import com.mshaerpour.mediaflash.ui.theme.DocumentTint
import com.mshaerpour.mediaflash.ui.theme.FolderTint
import com.mshaerpour.mediaflash.ui.theme.ImageTint
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.SubtitleTint
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import com.mshaerpour.mediaflash.ui.theme.VideoTint
import com.mshaerpour.mediaflash.util.FileType
import com.mshaerpour.mediaflash.util.FileUtils

/**
 * Windows 11 list-view row for files.
 *
 * Used on the Recent, Favorites and Search screens. Compact horizontal layout:
 * small colored type icon · name · type · size · optional trailing indicator.
 */
@Composable
fun FileListRow(
    file: MediaFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isFocused) AcrylicFocused else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isFocused) StrokeSubtle else Color.Transparent,
                shape = RoundedCornerShape(6.dp),
            )
            .clickable { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Small colored type icon in a tinted square (Win11 list view icon)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(fileTypeTint(file.fileType).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = fileTypeIcon(file.fileType),
                contentDescription = null,
                tint = fileTypeTint(file.fileType),
                modifier = Modifier.size(18.dp),
            )
        }

        // Name + meta
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = file.name,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${file.fileType.name} · ${FileUtils.humanReadableSize(file.size)}",
                color = TextTertiary,
                fontSize = 11.sp,
                maxLines = 1,
            )
        }

        if (trailing != null) trailing()
    }
}

private fun fileTypeIcon(type: FileType): ImageVector = when (type) {
    FileType.Folder -> Icons.Filled.Folder
    FileType.Video -> Icons.Filled.VideoFile
    FileType.Audio -> Icons.Filled.AudioFile
    FileType.Image -> Icons.Filled.Image
    else -> Icons.Filled.InsertDriveFile
}

private fun fileTypeTint(type: FileType): Color = when (type) {
    FileType.Folder -> FolderTint
    FileType.Video -> VideoTint
    FileType.Audio -> AudioTint
    FileType.Image -> ImageTint
    FileType.Subtitle -> SubtitleTint
    FileType.Document -> DocumentTint
    FileType.Archive, FileType.Other -> TextTertiary
}

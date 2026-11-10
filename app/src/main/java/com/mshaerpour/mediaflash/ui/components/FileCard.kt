package com.mshaerpour.mediaflash.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mshaerpour.mediaflash.data.MediaFile
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.AcrylicCard
import com.mshaerpour.mediaflash.ui.theme.AcrylicCardAlt
import com.mshaerpour.mediaflash.ui.theme.AcrylicFocused
import com.mshaerpour.mediaflash.ui.theme.AudioTint
import com.mshaerpour.mediaflash.ui.theme.DocumentTint
import com.mshaerpour.mediaflash.ui.theme.FolderTint
import com.mshaerpour.mediaflash.ui.theme.ImageTint
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.StrokeDefault
import com.mshaerpour.mediaflash.ui.theme.StrokeStrong
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.SubtitleTint
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import com.mshaerpour.mediaflash.ui.theme.VideoTint
import com.mshaerpour.mediaflash.util.FileType
import com.mshaerpour.mediaflash.util.FileUtils

/**
 * Windows 11 File Explorer item card.
 *
 * Layout follows the Win11 "Tiles" view:
 * - Square preview tile (1:1) at the top, slightly larger than v1
 * - File name on a single line below, with metadata under it
 * - Folder items show a big yellow folder icon
 * - Video items show first-frame thumbnail + small play badge
 * - Image items show the actual image
 * - Hover/Focus uses a subtle accent border (Win11 selection visual)
 *
 * This is the same data-driven component used everywhere files appear.
 */
@Composable
fun FileCard(
    file: MediaFile,
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        modifier = modifier
            .aspectRatio(1.0f)               // square card (Win11 "Tiles" style)
            .clip(RoundedCornerShape(8.dp))  // Win11 corner radius
            .background(
                if (isFocused) AcrylicFocused else AcrylicCard,
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Accent else StrokeSubtle,
                shape = RoundedCornerShape(8.dp),
            )
            .focusable(interactionSource = interactionSource),
        color = Color.Transparent,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Preview tile - takes most of the space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MicaElevated),
                contentAlignment = Alignment.Center,
            ) {
                PreviewOrIcon(file = file)
                // Favorite badge (top-right)
                if (isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(20.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Accent.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
                // Type chip (top-left) for Win11 style
                TypeChip(file = file)
            }

            // Name + meta - tighter, Win11 size
            Text(
                text = file.name,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
            )
            Text(
                text = fileSubtitle(file),
                color = TextTertiary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp,
            )
        }
    }
}

@Composable
private fun TypeChip(file: MediaFile) {
    val (label, tint) = when (file.fileType) {
        FileType.Folder -> "Folder" to FolderTint
        FileType.Video -> "Video" to VideoTint
        FileType.Audio -> "Audio" to AudioTint
        FileType.Image -> "Image" to ImageTint
        FileType.Subtitle -> "Subtitle" to SubtitleTint
        FileType.Document -> "Document" to DocumentTint
        FileType.Archive, FileType.Other -> "File" to TextTertiary
    }
    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.sp,
        )
    }
}

@Composable
private fun FileTypeIcon(file: MediaFile, size: Dp) {
    val (icon, tint) = when (file.fileType) {
        FileType.Folder -> Icons.Filled.Folder to FolderTint
        FileType.Video -> Icons.Filled.VideoFile to VideoTint
        FileType.Audio -> Icons.Filled.AudioFile to AudioTint
        FileType.Image -> Icons.Filled.Image to ImageTint
        FileType.Subtitle, FileType.Document -> Icons.Filled.InsertDriveFile to DocumentTint
        FileType.Archive, FileType.Other -> Icons.Filled.InsertDriveFile to TextTertiary
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(size),
    )
}

@Composable
private fun PreviewOrIcon(file: MediaFile) {
    when (file.fileType) {
        FileType.Image -> AsyncImage(
            model = file.uri,
            contentDescription = file.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        FileType.Video -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = file.uri,
                contentDescription = file.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Win11 Media Player style play badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        else -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            FileTypeIcon(file = file, size = 64.dp)
        }
    }
}

private fun fileSubtitle(file: MediaFile): String {
    return if (file.isDirectory) {
        "Folder"
    } else {
        "${FileUtils.humanReadableSize(file.size)} · ${file.extension.uppercase()}"
    }
}

package com.mshaerpour.mediaflash.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mshaerpour.mediaflash.R
import com.mshaerpour.mediaflash.data.FileRepository
import com.mshaerpour.mediaflash.data.MediaFile
import com.mshaerpour.mediaflash.data.SettingsStore
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.util.FileType

/**
 * Windows 11 Photos app-inspired image viewer.
 *
 * Layout:
 * - Centered image with full contrast
 * - Top-left acrylic back button
 * - Top-center counter pill (e.g., "1 / 12 · name.jpg")
 * - Bottom-center control pill with prev/zoom/next buttons
 */
@Composable
fun ImageViewerScreen(fileKey: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { FileRepository(context) }
    val settingsStore = remember { SettingsStore(context) }

    val initialUri = remember { Uri.parse(fileKey) }
    var images by remember { mutableStateOf<List<MediaFile>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(initialUri) {
        val parent = parentOfTreeUri(initialUri)
        if (parent != null) {
            val siblings = repository.listChildren(parent)
            images = siblings.filter { it.fileType == FileType.Image }
            currentIndex = images.indexOfFirst { it.uri == initialUri }
        }
        settingsStore.addRecent(fileKey)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusable(),
    ) {
        if (currentIndex in images.indices) {
            AsyncImage(
                model = images[currentIndex].uri,
                contentDescription = images[currentIndex].name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Top-left back button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(38.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                .clickable { onBack() }
                .focusable(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White, modifier = Modifier.size(20.dp))
        }

        // Top-center counter pill
        if (currentIndex in images.indices) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "${currentIndex + 1} / ${images.size}  ·  ${images[currentIndex].name}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        // Bottom control pill (Win11 Photos style)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoundControlButton(
                icon = Icons.Filled.SkipPrevious,
                contentDescription = stringResource(R.string.image_prev),
                enabled = currentIndex > 0,
                onClick = { if (currentIndex > 0) currentIndex-- },
            )
            RoundControlButton(
                icon = Icons.Outlined.ZoomIn,
                contentDescription = stringResource(R.string.image_zoom),
                onClick = { /* TODO zoom */ },
            )
            RoundControlButton(
                icon = Icons.Filled.SkipNext,
                contentDescription = stringResource(R.string.image_next),
                primary = true,
                enabled = currentIndex < images.size - 1,
                onClick = { if (currentIndex < images.size - 1) currentIndex++ },
            )
        }
    }
}

@Composable
private fun RoundControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    primary: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interactionSource = remember { androidx.compose.runtime.MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier = Modifier
            .size(if (primary) 44.dp else 38.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (!enabled) Color.White.copy(alpha = 0.05f)
                else if (primary) Accent
                else if (isFocused) Color.White.copy(alpha = 0.2f)
                else Color.White.copy(alpha = 0.08f)
            )
            .clickable(enabled = enabled) { onClick() }
            .focusable(interactionSource = interactionSource, enabled = enabled),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (!enabled) Color.White.copy(alpha = 0.3f) else if (primary) Color.Black else Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}

private fun parentOfTreeUri(uri: Uri): Uri? {
    val str = uri.toString()
    val docIdx = str.indexOf("/document/")
    if (docIdx < 0) return null
    return Uri.parse(str.substring(0, docIdx))
}

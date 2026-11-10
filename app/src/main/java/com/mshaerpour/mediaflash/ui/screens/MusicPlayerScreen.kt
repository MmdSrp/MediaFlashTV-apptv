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
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.mshaerpour.mediaflash.R
import com.mshaerpour.mediaflash.data.SettingsStore
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.AudioTint
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import com.mshaerpour.mediaflash.util.FileUtils
import kotlinx.coroutines.delay

/**
 * Windows 11 Media Player-inspired music player screen.
 *
 * Layout:
 * - Large rounded album-art tile with gradient placeholder + music icon
 * - Track title + "Now Playing" label
 * - Slim progress bar with timestamps (Win11 style)
 * - Round transport controls (back 10s, prev, play, next, forward 10s)
 * - Repeat / Shuffle toggle pills (top-right)
 */
@Composable
fun MusicPlayerScreen(fileKey: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val settingsStore = remember { SettingsStore(context) }
    val uri = remember { Uri.parse(fileKey) }
    val fileName = remember {
        fileKey.substringAfterLast('/').substringAfterLast('%2F').let {
            try { java.net.URLDecoder.decode(it, "UTF-8") } catch (e: Exception) { it }
        }
    }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    var positionMs by remember { mutableStateOf(0L) }
    var durationMs by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(player) {
        while (true) {
            positionMs = player.currentPosition
            durationMs = if (player.duration > 0) player.duration else 0L
            isPlaying = player.isPlaying
            delay(200)
        }
    }

    LaunchedEffect(uri) { settingsStore.addRecent(fileKey) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MicaDeep)
            .padding(horizontal = 56.dp, vertical = 32.dp),
    ) {
        // Back button (top-left)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(38.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MicaElevated)
                .border(1.dp, StrokeSubtle, RoundedCornerShape(6.dp))
                .clickable { onBack() }
                .focusable(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(R.string.back), tint = TextPrimary, modifier = Modifier.size(20.dp))
        }

        // Repeat + Shuffle (top-right)
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MicaElevated)
                    .border(1.dp, StrokeSubtle, RoundedCornerShape(6.dp))
                    .clickable { /* TODO shuffle */ }
                    .focusable(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Shuffle, contentDescription = stringResource(R.string.shuffle), tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MicaElevated)
                    .border(1.dp, StrokeSubtle, RoundedCornerShape(6.dp))
                    .clickable { /* TODO repeat */ }
                    .focusable(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Repeat, contentDescription = stringResource(R.string.repeat), tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Album art - large rounded tile with gradient
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AudioTint.copy(alpha = 0.55f),
                                MicaElevated,
                                MicaDeep,
                            ),
                        ),
                    )
                    .border(1.dp, StrokeSubtle, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(96.dp),
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = fileName,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.now_playing),
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp),
            )

            Spacer(Modifier.height(28.dp))

            // Progress bar - Win11 slim style
            Column(
                modifier = Modifier.fillMaxWidth(0.55f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.18f)),
                ) {
                    val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Accent),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(FileUtils.humanReadableDuration(positionMs / 1000), color = TextSecondary, fontSize = 11.sp)
                    Text(FileUtils.humanReadableDuration(durationMs / 1000), color = TextTertiary, fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Transport controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundControl(Icons.Filled.Replay10, stringResource(R.string.rewind), 44) { player.seekBack() }
                RoundControl(Icons.Filled.SkipPrevious, stringResource(R.string.previous), 44) { /* TODO */ }
                RoundControl(
                    icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    label = stringResource(R.string.play),
                    size = 60,
                    primary = true,
                ) { if (isPlaying) player.pause() else player.play() }
                RoundControl(Icons.Filled.SkipNext, stringResource(R.string.next), 44) { /* TODO */ }
                RoundControl(Icons.Filled.Forward10, stringResource(R.string.forward), 44) { player.seekForward() }
            }
        }
    }
}

@Composable
private fun RoundControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    size: Int,
    primary: Boolean = false,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (primary) Accent
                else if (isFocused) Color.White.copy(alpha = 0.16f)
                else Color.White.copy(alpha = 0.06f)
            )
            .border(
                width = if (isFocused && !primary) 1.dp else 0.dp,
                color = Color.White.copy(alpha = 0.4f),
                shape = RoundedCornerShape(50),
            )
            .clickable { onClick() }
            .focusable(interactionSource = interactionSource),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (primary) Color.Black else Color.White,
            modifier = Modifier.size((size * 0.5).dp),
        )
    }
}

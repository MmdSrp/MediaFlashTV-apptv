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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.DeleteSweep
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mshaerpour.mediaflash.R
import com.mshaerpour.mediaflash.data.MediaFile
import com.mshaerpour.mediaflash.data.SettingsStore
import com.mshaerpour.mediaflash.ui.components.FileListRow
import com.mshaerpour.mediaflash.ui.components.PageHeader
import com.mshaerpour.mediaflash.ui.components.PillIconButton
import com.mshaerpour.mediaflash.ui.theme.AcrylicCard
import com.mshaerpour.mediaflash.ui.theme.AcrylicHover
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import com.mshaerpour.mediaflash.util.FileType
import com.mshaerpour.mediaflash.util.FileUtils
import kotlinx.coroutines.launch

/**
 * Recently-played media list styled like Windows 11's Recent Files area on the
 * File Explorer Home page.
 */
@Composable
fun RecentScreen() {
    val context = LocalContext.current
    val store = remember { SettingsStore(context) }
    val recent by store.recent.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MicaDeep)
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        PageHeader(
            title = stringResource(R.string.nav_recent),
            subtitle = "Last played media files",
            icon = Icons.Filled.History,
            trailing = {
                if (recent.isNotEmpty()) {
                    androidx.compose.material3.Text(
                        text = stringResource(R.string.clear_recent),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AcrylicCard)
                            .border(1.dp, StrokeSubtle, RoundedCornerShape(6.dp))
                            .clickable { scope.launch { store.clearRecent() } }
                            .focusable()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            },
        )

        if (recent.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteSweep,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(48.dp),
                    )
                    Text(stringResource(R.string.recent_empty), color = TextSecondary, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(recent, key = { it }) { key ->
                    RecentRow(key = key)
                }
            }
        }
    }
}

@Composable
private fun RecentRow(key: String) {
    val name = remember(key) {
        try { java.net.URLDecoder.decode(key.substringAfterLast('/'), "UTF-8") }
        catch (e: Exception) { key.substringAfterLast('/') }
    }
    val type = FileUtils.classify(name, isDirectory = false)
    val file = MediaFile(
        uri = Uri.parse(key),
        name = name,
        isDirectory = false,
        size = 0,
        lastModified = 0,
        mimeType = null,
        parentUri = null,
        fileType = type,
    )
    FileListRow(file = file, onClick = { /* TODO open */ })
}

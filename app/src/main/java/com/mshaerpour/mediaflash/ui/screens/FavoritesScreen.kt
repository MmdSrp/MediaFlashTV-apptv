package com.mshaerpour.mediaflash.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import com.mshaerpour.mediaflash.util.FileUtils

/**
 * Favorites screen styled after Windows 11 Quick Access pinned items list.
 */
@Composable
fun FavoritesScreen() {
    val context = LocalContext.current
    val store = remember { SettingsStore(context) }
    val favorites by store.favorites.collectAsState(initial = emptySet())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MicaDeep)
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        PageHeader(
            title = stringResource(R.string.nav_favorites),
            subtitle = "Pinned files for quick access",
            icon = Icons.Filled.Star,
        )

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(48.dp),
                    )
                    Text(
                        text = stringResource(R.string.favorites_empty),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 48.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(favorites.toList(), key = { it }) { key ->
                    val name = remember(key) {
                        try { java.net.URLDecoder.decode(key.substringAfterLast('/'), "UTF-8") }
                        catch (e: Exception) { key.substringAfterLast('/') }
                    }
                    val file = MediaFile(
                        uri = Uri.parse(key),
                        name = name,
                        isDirectory = false,
                        size = 0,
                        lastModified = 0,
                        mimeType = null,
                        parentUri = null,
                        fileType = FileUtils.classify(name, isDirectory = false),
                    )
                    FileListRow(
                        file = file,
                        onClick = { /* TODO open */ },
                        trailing = {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Accent,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                    )
                }
            }
        }
    }
}

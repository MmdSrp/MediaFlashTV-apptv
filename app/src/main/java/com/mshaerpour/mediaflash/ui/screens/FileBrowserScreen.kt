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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mshaerpour.mediaflash.R
import com.mshaerpour.mediaflash.data.FileRepository
import com.mshaerpour.mediaflash.data.MediaFile
import com.mshaerpour.mediaflash.data.SettingsStore
import com.mshaerpour.mediaflash.data.SortBy
import com.mshaerpour.mediaflash.data.UsbDrive
import com.mshaerpour.mediaflash.ui.components.BrowserTopBar
import com.mshaerpour.mediaflash.ui.components.FileCard
import com.mshaerpour.mediaflash.ui.theme.AcrylicCard
import com.mshaerpour.mediaflash.ui.theme.AcrylicHover
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import com.mshaerpour.mediaflash.util.FileUtils
import kotlinx.coroutines.launch

/**
 * File browser screen styled after Windows 11 File Explorer.
 *
 * Top bar: Back pill button · Breadcrumb pill · Sort/View pill buttons
 * Body: Grid of square file cards (Win11 "Tiles" view)
 *
 * The grid uses Adaptive columns (Win11 doesn't enforce a fixed column count;
 * it adapts to window width with a minimum tile width).
 */
@Composable
fun FileBrowserScreen(
    drive: UsbDrive,
    onFileOpened: (MediaFile) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { FileRepository(context) }
    val favoritesStore = remember { SettingsStore(context) }
    val favorites by favoritesStore.favorites.collectAsState(initial = emptySet())

    var currentDir by remember { mutableStateOf(drive.rootUri) }
    var pathStack by remember { mutableStateOf(listOf<Uri>()) }
    var items by remember { mutableStateOf<List<MediaFile>>(emptyList()) }
    var sort by remember { mutableStateOf(SortBy.NameAsc) }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentDir, sort) {
        loading = true
        items = repository.listChildren(currentDir, sort)
        loading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MicaDeep),
    ) {
        // Sort menu overlay (anchored to the toolbar sort button)
        DropdownMenu(
            expanded = sortMenuOpen,
            onDismissRequest = { sortMenuOpen = false },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 32.dp, top = 60.dp)
                .background(MicaElevated, RoundedCornerShape(8.dp))
                .border(1.dp, StrokeSubtle, RoundedCornerShape(8.dp)),
        ) {
            SortBy.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (sort == option) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Accent,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                            } else {
                                Spacer(Modifier.width(24.dp))
                            }
                            Text(option.label(), color = TextPrimary, fontSize = 13.sp)
                        }
                    },
                    onClick = {
                        sort = option
                        sortMenuOpen = false
                    },
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BrowserTopBar(
                crumbs = buildCrumbs(drive, pathStack, currentDir),
                onCrumbClick = { index ->
                    if (index == 0) {
                        // Drive root
                        pathStack = emptyList()
                        currentDir = drive.rootUri
                    } else {
                        val target = pathStack.getOrNull(index - 1) ?: return@BrowserTopBar
                        pathStack = pathStack.subList(0, index - 1).toList()
                        currentDir = target
                    }
                },
                onBack = {
                    if (pathStack.isEmpty()) onBack()
                    else {
                        currentDir = pathStack.last()
                        pathStack = pathStack.dropLast(1)
                    }
                },
                onSort = { sortMenuOpen = true },
            )

            // Status row (Win11 status line)
            if (!loading && items.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${items.count { it.isDirectory }} folders, ${items.count { !it.isDirectory }} files",
                        color = TextSecondary,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "Sorted: ${sort.label()}",
                        color = TextTertiary,
                        fontSize = 11.sp,
                    )
                }
            }

            when {
                loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.loading), color = TextSecondary, fontSize = 14.sp)
                }
                items.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text("📁", fontSize = 48.sp)
                        Text(
                            text = stringResource(R.string.empty_folder),
                            color = TextSecondary,
                            fontSize = 14.sp,
                        )
                    }
                }
                else -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(items, key = { it.key }) { file ->
                        FileCard(
                            file = file,
                            isFavorite = favorites.contains(file.key),
                            onClick = {
                                if (file.isDirectory) {
                                    pathStack = pathStack + currentDir
                                    currentDir = file.uri
                                } else {
                                    scope.launch { favoritesStore.addRecent(file.key) }
                                    onFileOpened(file)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun buildCrumbs(
    drive: UsbDrive,
    pathStack: List<Uri>,
    currentDir: Uri,
): List<String> {
    val parts = mutableListOf(drive.name)
    pathStack.forEach { parts.add(it.lastPathSegment?.substringAfterLast(':') ?: it.lastPathSegment ?: "") }
    if (currentDir != drive.rootUri) {
        parts.add(currentDir.lastPathSegment?.substringAfterLast(':') ?: currentDir.lastPathSegment ?: "")
    }
    return parts
}

private fun SortBy.label(): String = when (this) {
    SortBy.NameAsc -> "Name (A → Z)"
    SortBy.NameDesc -> "Name (Z → A)"
    SortBy.SizeAsc -> "Size (smallest)"
    SortBy.SizeDesc -> "Size (largest)"
    SortBy.DateAsc -> "Date (oldest)"
    SortBy.DateDesc -> "Date (newest)"
    SortBy.TypeAsc -> "Type"
}

package com.mshaerpour.mediaflash.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mshaerpour.mediaflash.R
import com.mshaerpour.mediaflash.data.FileRepository
import com.mshaerpour.mediaflash.data.MediaFile
import com.mshaerpour.mediaflash.data.SettingsStore
import com.mshaerpour.mediaflash.ui.components.FileListRow
import com.mshaerpour.mediaflash.ui.components.PageHeader
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.MicaElevated
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle
import com.mshaerpour.mediaflash.ui.theme.TextPrimary
import com.mshaerpour.mediaflash.ui.theme.TextSecondary
import com.mshaerpour.mediaflash.ui.theme.TextTertiary
import kotlinx.coroutines.delay

/**
 * Search screen styled after Windows 11 search: acrylic rounded input box
 * with leading search icon and placeholder text, followed by result rows.
 */
@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val store = remember { SettingsStore(context) }
    val repository = remember { FileRepository(context) }
    val lastRoot by store.lastUsbRoot.collectAsState(initial = null)

    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<MediaFile>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        if (query.isBlank()) {
            results = emptyList(); loading = false; return@LaunchedEffect
        }
        loading = true
        delay(400)
        val root = lastRoot?.let(Uri::parse)
        results = if (root != null) repository.search(root, query) else emptyList()
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MicaDeep)
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        PageHeader(
            title = stringResource(R.string.nav_search),
            subtitle = "Find files on the selected USB drive",
            icon = Icons.Filled.Search,
        )

        // Win11-style search input
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = {
                Text(stringResource(R.string.search_hint), color = TextTertiary, fontSize = 13.sp)
            },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Accent, modifier = Modifier.size(18.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = Accent,
                unfocusedBorderColor = StrokeSubtle,
                cursorColor = Accent,
                focusedContainerColor = MicaElevated,
                unfocusedContainerColor = MicaElevated,
            ),
            shape = RoundedCornerShape(6.dp),
        )

        when {
            loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.loading), color = TextSecondary, fontSize = 14.sp)
            }
            query.isNotBlank() && results.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.search_no_results, query),
                    color = TextSecondary,
                    fontSize = 14.sp,
                )
            }
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(results, key = { it.key }) { file ->
                    FileListRow(file = file, onClick = { /* TODO open */ })
                }
            }
        }
    }
}

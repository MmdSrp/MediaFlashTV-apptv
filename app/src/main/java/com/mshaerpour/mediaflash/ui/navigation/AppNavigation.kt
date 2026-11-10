package com.mshaerpour.mediaflash.ui.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mshaerpour.mediaflash.data.UsbDrive
import com.mshaerpour.mediaflash.ui.components.AppFooter
import com.mshaerpour.mediaflash.ui.components.NavEntry
import com.mshaerpour.mediaflash.ui.components.Sidebar
import com.mshaerpour.mediaflash.ui.screens.FavoritesScreen
import com.mshaerpour.mediaflash.ui.screens.FileBrowserScreen
import com.mshaerpour.mediaflash.ui.screens.HomeScreen
import com.mshaerpour.mediaflash.ui.screens.ImageViewerScreen
import com.mshaerpour.mediaflash.ui.screens.MusicPlayerScreen
import com.mshaerpour.mediaflash.ui.screens.RecentScreen
import com.mshaerpour.mediaflash.ui.screens.SearchScreen
import com.mshaerpour.mediaflash.ui.screens.SettingsScreen
import com.mshaerpour.mediaflash.ui.screens.SharedViewModel
import com.mshaerpour.mediaflash.ui.screens.VideoPlayerScreen
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.util.FileType
import java.net.URLEncoder

/**
 * Root navigation graph for Media Flash.
 * Layout: persistent left Sidebar + main content area + bottom footer.
 */
@Composable
fun MediaFlashApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: "home"

    val context = LocalContext.current
    val sharedViewModel: SharedViewModel = viewModel(
        factory = SharedViewModel.provideFactory(context.applicationContext as android.app.Application)
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MicaDeep)
            .systemBarsPadding(),
    ) {
        Sidebar(
            currentRoute = currentRoute,
            onSelect = { entry ->
                navController.navigate(entry.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )

        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 32.dp, vertical = 24.dp),
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable("home") {
                        HomeScreen(
                            sharedViewModel = sharedViewModel,
                            onUsbSelected = { drive ->
                                sharedViewModel.selectDrive(drive)
                                navController.navigate("browser/${drive.id}")
                            },
                            onRecentClick = { navController.navigate("recent") },
                            onFavoritesClick = { navController.navigate("favorites") },
                            onSearchClick = { navController.navigate("search") },
                            onSettingsClick = { navController.navigate("settings") },
                        )
                    }
                    composable("browser/{driveId}") { entry ->
                        val driveId = entry.arguments?.getString("driveId").orEmpty()
                        val drive = sharedViewModel.getDriveById(driveId)
                        if (drive != null) {
                            FileBrowserScreen(
                                drive = drive,
                                onFileOpened = { file ->
                                    val encoded = URLEncoder.encode(file.key, "UTF-8")
                                    when (file.fileType) {
                                        FileType.Video -> navController.navigate("video/$encoded")
                                        FileType.Audio -> navController.navigate("music/$encoded")
                                        FileType.Image -> navController.navigate("image/$encoded")
                                        else -> { /* Info sheet not implemented in v1 */ }
                                    }
                                },
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                    composable("video/{fileKey}") { entry ->
                        val key = Uri.decode(entry.arguments?.getString("fileKey").orEmpty())
                        VideoPlayerScreen(fileKey = key, onBack = { navController.popBackStack() })
                    }
                    composable("music/{fileKey}") { entry ->
                        val key = Uri.decode(entry.arguments?.getString("fileKey").orEmpty())
                        MusicPlayerScreen(fileKey = key, onBack = { navController.popBackStack() })
                    }
                    composable("image/{fileKey}") { entry ->
                        val key = Uri.decode(entry.arguments?.getString("fileKey").orEmpty())
                        ImageViewerScreen(fileKey = key, onBack = { navController.popBackStack() })
                    }
                    composable("recent") { RecentScreen() }
                    composable("favorites") { FavoritesScreen() }
                    composable("search") { SearchScreen() }
                    composable("settings") { SettingsScreen() }
                }
            }
            AppFooter()
        }
    }
}

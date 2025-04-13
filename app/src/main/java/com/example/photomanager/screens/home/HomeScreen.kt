package com.example.photomanager.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.photomanager.screens.album.AlbumScreen
import com.example.photomanager.screens.timeline.TimelineScreen

/**
 * Enum để đại diện cho các tab trong màn hình chính
 */
enum class HomeTab {
    ALBUMS, TIMELINE
}

/**
 * Màn hình chính của ứng dụng - điểm bắt đầu với 2 tab: Albums và Timeline
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    requestPermission: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(HomeTab.ALBUMS) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Manager") },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == HomeTab.ALBUMS,
                    onClick = { selectedTab = HomeTab.ALBUMS },
                    icon = { Icon(Icons.Default.GridView, contentDescription = "Albums") },
                    label = { Text("Albums") }
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.TIMELINE,
                    onClick = { selectedTab = HomeTab.TIMELINE },
                    icon = { Icon(Icons.Default.Timeline, contentDescription = "Timeline") },
                    label = { Text("Timeline") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                HomeTab.ALBUMS -> AlbumScreen(navController)
                HomeTab.TIMELINE -> TimelineScreen(navController)
            }
        }
    }
    
    // Yêu cầu quyền truy cập ảnh khi màn hình được tạo
    LaunchedEffect(Unit) {
        requestPermission()
    }
} 
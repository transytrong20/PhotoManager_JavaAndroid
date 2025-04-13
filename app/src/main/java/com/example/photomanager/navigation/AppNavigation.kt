package com.example.photomanager.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.photomanager.screens.album.AlbumDetailScreen
import com.example.photomanager.screens.detail.PhotoDetailScreen
import com.example.photomanager.screens.home.HomeScreen

/**
 * Định nghĩa các đường dẫn điều hướng trong ứng dụng
 */
object AppDestinations {
    const val HOME_ROUTE = "home"
    const val ALBUM_DETAIL_ROUTE = "album/{albumName}"
    const val PHOTO_DETAIL_ROUTE = "photo/{photoId}"
    
    // Helper function để tạo đường dẫn cho màn hình chi tiết album
    fun albumDetailRoute(albumName: String): String {
        return "album/$albumName"
    }
    
    // Helper function để tạo đường dẫn cho màn hình chi tiết ảnh
    fun photoDetailRoute(photoId: Long): String {
        return "photo/$photoId"
    }
}

/**
 * Quản lý điều hướng giữa các màn hình trong ứng dụng
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    requestPermission: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_ROUTE
    ) {
        // Màn hình chính
        composable(route = AppDestinations.HOME_ROUTE) {
            HomeScreen(navController, requestPermission)
        }
        
        // Màn hình chi tiết album
        composable(
            route = AppDestinations.ALBUM_DETAIL_ROUTE,
            arguments = listOf(
                navArgument("albumName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val albumName = backStackEntry.arguments?.getString("albumName") ?: ""
            AlbumDetailScreen(albumName, navController)
        }
        
        // Màn hình chi tiết ảnh
        composable(
            route = AppDestinations.PHOTO_DETAIL_ROUTE,
            arguments = listOf(
                navArgument("photoId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getLong("photoId") ?: -1L
            PhotoDetailScreen(photoId, navController)
        }
    }
} 
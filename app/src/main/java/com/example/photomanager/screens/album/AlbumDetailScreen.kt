package com.example.photomanager.screens.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.photomanager.model.Photo
import com.example.photomanager.repository.PhotoRepository

/**
 * Màn hình hiển thị chi tiết của một album và các ảnh trong đó
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    albumName: String,
    navController: NavController
) {
    val context = LocalContext.current
    val photoRepository = remember { PhotoRepository(context) }
    val photosState = photoRepository.getPhotosByAlbum(albumName).collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(albumName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (photosState.value.isEmpty()) {
                // Hiển thị thông báo khi album trống
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Album này không có ảnh nào")
                }
            } else {
                AlbumPhotoGrid(
                    photos = photosState.value,
                    onPhotoClick = { photo ->
                        // Điều hướng đến màn hình chi tiết ảnh
                        navController.navigate("photo/${photo.id}")
                    }
                )
            }
        }
    }
}

/**
 * Grid hiển thị danh sách các ảnh trong album
 */
@Composable
fun AlbumPhotoGrid(
    photos: List<Photo>,
    onPhotoClick: (Photo) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(photos) { photo ->
            AlbumPhotoItem(photo = photo, onClick = { onPhotoClick(photo) })
        }
    }
}

/**
 * Item hiển thị một ảnh trong album
 */
@Composable
fun AlbumPhotoItem(
    photo: Photo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Box {
            AsyncImage(
                model = photo.uri,
                contentDescription = photo.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
} 
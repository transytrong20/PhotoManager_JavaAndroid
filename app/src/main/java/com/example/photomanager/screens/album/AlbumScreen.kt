package com.example.photomanager.screens.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.photomanager.model.Album
import com.example.photomanager.repository.PhotoRepository

/**
 * Màn hình hiển thị danh sách các album ảnh
 */
@Composable
fun AlbumScreen(navController: NavController) {
    val context = LocalContext.current
    val photoRepository = remember { PhotoRepository(context) }
    val albumsState = photoRepository.getAllAlbums().collectAsState(initial = emptyList())
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (albumsState.value.isEmpty()) {
            // Hiển thị thông báo khi không có album nào
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy album nào")
            }
        } else {
            AlbumGrid(
                albums = albumsState.value,
                onAlbumClick = { album ->
                    // Điều hướng đến màn hình chi tiết album
                    navController.navigate("album/${album.name}")
                }
            )
        }
    }
}

/**
 * Grid hiển thị danh sách các album
 */
@Composable
fun AlbumGrid(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(albums) { album ->
            AlbumItem(album = album, onClick = { onAlbumClick(album) })
        }
    }
}

/**
 * Item hiển thị thông tin một album
 */
@Composable
fun AlbumItem(
    album: Album,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Box {
            // Hiển thị ảnh đại diện của album
            AsyncImage(
                model = album.thumbnailUri,
                contentDescription = album.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Overlay thông tin album
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${album.photoCount} ảnh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
} 
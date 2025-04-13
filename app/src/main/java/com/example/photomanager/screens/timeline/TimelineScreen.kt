package com.example.photomanager.screens.timeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.photomanager.model.Photo
import com.example.photomanager.repository.PhotoRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * Màn hình hiển thị danh sách ảnh theo dòng thời gian
 */
@Composable
fun TimelineScreen(navController: NavController) {
    val context = LocalContext.current
    val photoRepository = remember { PhotoRepository(context) }
    val photosState = photoRepository.getAllPhotos().collectAsState(initial = emptyList())
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (photosState.value.isEmpty()) {
            // Hiển thị thông báo khi không có ảnh nào
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy ảnh nào")
            }
        } else {
            // Nhóm ảnh theo ngày
            val groupedPhotos = photosState.value.groupBy { photo ->
                // Định dạng ngày tháng để nhóm
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                dateFormat.format(photo.dateTaken)
            }
            
            TimelineList(
                groupedPhotos = groupedPhotos,
                onPhotoClick = { photo ->
                    // Điều hướng đến màn hình chi tiết ảnh
                    navController.navigate("photo/${photo.id}")
                }
            )
        }
    }
}

/**
 * Danh sách ảnh được nhóm theo ngày
 */
@Composable
fun TimelineList(
    groupedPhotos: Map<String, List<Photo>>,
    onPhotoClick: (Photo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        // Duyệt qua các nhóm ảnh theo ngày
        groupedPhotos.entries.sortedByDescending { it.key }.forEach { (date, photos) ->
            item {
                // Hiển thị tiêu đề ngày
                Text(
                    text = date,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            items(photos) { photo ->
                TimelinePhotoItem(photo = photo, onClick = { onPhotoClick(photo) })
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Item hiển thị thông tin một ảnh trong dòng thời gian
 */
@Composable
fun TimelinePhotoItem(
    photo: Photo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh thu nhỏ
            AsyncImage(
                model = photo.uri,
                contentDescription = photo.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f)
            )
            
            // Thông tin ảnh
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = photo.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Album: ${photo.albumName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Hiển thị thời gian chi tiết
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                Text(
                    text = "Thời gian: ${timeFormat.format(photo.dateTaken)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
} 
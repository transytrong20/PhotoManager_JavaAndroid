package com.example.photomanager.screens.detail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.photomanager.model.Photo
import com.example.photomanager.repository.PhotoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Màn hình hiển thị chi tiết của một ảnh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: Long,
    navController: NavController
) {
    val context = LocalContext.current
    val photoRepository = remember { PhotoRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // State cho ảnh hiện tại
    var photo by remember { mutableStateOf<Photo?>(null) }
    
    // State cho dialog chỉnh sửa và xóa
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    // Field cho chỉnh sửa
    var editedName by remember { mutableStateOf("") }
    
    // Tải thông tin ảnh
    LaunchedEffect(photoId) {
        photoRepository.getAllPhotos().collect { photos ->
            photo = photos.find { it.id == photoId }
            photo?.let {
                editedName = it.name
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(photo?.name ?: "Chi tiết ảnh") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa")
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
            photo?.let { currentPhoto ->
                PhotoDetailContent(photo = currentPhoto)
            } ?: run {
                // Hiển thị loading hoặc thông báo lỗi
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    
    // Dialog chỉnh sửa ảnh
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Chỉnh sửa thông tin") },
            text = {
                TextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Tên ảnh") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        photo?.let {
                            coroutineScope.launch {
                                val success = photoRepository.updatePhoto(
                                    photo = it,
                                    newName = editedName
                                )
                                
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Đã cập nhật thông tin ảnh",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Không thể cập nhật thông tin ảnh",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        showEditDialog = false
                    }
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
    
    // Dialog xác nhận xóa
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa ảnh này không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        photo?.let {
                            coroutineScope.launch {
                                val success = photoRepository.deletePhoto(it)
                                
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Đã xóa ảnh",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Không thể xóa ảnh",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

/**
 * Nội dung màn hình chi tiết ảnh
 */
@Composable
fun PhotoDetailContent(photo: Photo) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hiển thị ảnh
        AsyncImage(
            model = photo.uri,
            contentDescription = photo.name,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f/3f)
                .padding(bottom = 16.dp)
        )
        
        // Thông tin chi tiết
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                DetailItem(icon = Icons.Default.Photo, label = "Tên:", value = photo.name)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                DetailItem(
                    icon = Icons.Default.DateRange, 
                    label = "Ngày chụp:", 
                    value = dateFormat.format(photo.dateTaken)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                DetailItem(
                    icon = Icons.Default.Folder, 
                    label = "Album:", 
                    value = photo.albumName
                )
            }
        }
    }
}

/**
 * Item hiển thị một thông tin chi tiết của ảnh
 */
@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
} 
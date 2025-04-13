package com.example.photomanager

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.photomanager.navigation.AppNavigation
import com.example.photomanager.ui.theme.PhotoManagerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Kiểm tra và yêu cầu quyền truy cập ảnh tùy thuộc vào phiên bản Android
                    val permissionsState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    
                    val requestPermission = {
                        if (!permissionsState.status.isGranted) {
                            permissionsState.launchPermissionRequest()
                        }
                    }
                    
                    // Hiển thị thông báo nếu người dùng từ chối quyền
                    LaunchedEffect(permissionsState.status) {
                        if (!permissionsState.status.isGranted && !permissionsState.status.shouldShowRationale) {
                            Toast.makeText(
                                this@MainActivity,
                                "Ứng dụng cần quyền truy cập ảnh để hoạt động",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    
                    // Thiết lập điều hướng
                    AppNavigation(
                        navController = navController,
                        requestPermission = requestPermission
                    )
                }
            }
        }
    }
}
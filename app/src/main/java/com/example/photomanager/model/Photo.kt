package com.example.photomanager.model

import android.net.Uri
import java.util.Date

/**
 * Đại diện cho một ảnh trong ứng dụng
 * @property id ID duy nhất của ảnh
 * @property uri URI của ảnh trong bộ nhớ thiết bị
 * @property name Tên của ảnh
 * @property dateTaken Ngày ảnh được chụp
 * @property albumName Tên album chứa ảnh
 * @property favorite Cho biết ảnh có được đánh dấu yêu thích hay không
 */
data class Photo(
    val id: Long,
    val uri: Uri,
    val name: String,
    val dateTaken: Date,
    val albumName: String,
    val favorite: Boolean = false
)

/**
 * Đại diện cho một album ảnh
 * @property name Tên của album
 * @property thumbnailUri URI của ảnh đại diện cho album
 * @property photoCount Số lượng ảnh trong album
 */
data class Album(
    val name: String, 
    val thumbnailUri: Uri,
    val photoCount: Int
) 
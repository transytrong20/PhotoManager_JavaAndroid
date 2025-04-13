package com.example.photomanager.model

import android.content.Context
import android.net.Uri
import java.util.Date

/**
 * Lớp cung cấp dữ liệu mẫu cho ứng dụng
 */
object DummyData {
    
    /**
     * Danh sách album mẫu
     */
    private val dummyAlbums = listOf(
        "Du lịch",
        "Gia đình",
        "Công việc",
        "Bạn bè",
        "Kỷ niệm"
    )
    
    /**
     * Danh sách tên ảnh mẫu
     */
    private val dummyImageNames = listOf(
        "Biển xanh cát trắng",
        "Hoàng hôn trên biển",
        "Núi rừng trùng điệp",
        "Ngày họp mặt gia đình",
        "Phố cổ Hội An",
        "Hồ Gươm buổi sáng",
        "Vịnh Hạ Long",
        "Lễ hội hoa đăng",
        "Chuyến du lịch Đà Lạt",
        "Món ăn đặc sản",
        "Kỉ niệm sinh nhật",
        "Họp lớp cuối năm",
        "Lễ tốt nghiệp",
        "Chuyến đi Đà Nẵng",
        "Bữa tiệc gia đình"
    )
    
    /**
     * Danh sách tên file drawable
     */
    private val dummyImageResourceNames = listOf(
        "sample_image_1",
        "sample_image_2",
        "sample_image_3",
        "sample_image_4",
        "sample_image_5"
    )
    
    /**
     * Tạo danh sách ảnh mẫu
     */
    fun generateDummyPhotos(context: Context): List<Photo> {
        val photos = mutableListOf<Photo>()
        val currentTime = System.currentTimeMillis()
        val random = java.util.Random()
        
        for (i in 1..20) {
            val albumName = dummyAlbums[random.nextInt(dummyAlbums.size)]
            val name = dummyImageNames[random.nextInt(dummyImageNames.size)]
            
            // Thêm một số ngày ngẫu nhiên trong phạm vi 30 ngày
            val daysOffset = random.nextInt(30)
            val dateOffset = currentTime - (daysOffset * 24 * 60 * 60 * 1000)
            
            // Chọn tên resource ngẫu nhiên
            val resourceName = dummyImageResourceNames[random.nextInt(dummyImageResourceNames.size)]
            
            // Lấy ID của resource từ tên
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            
            // Tạo URI cho resource
            val uri = Uri.parse("android.resource://${context.packageName}/$resourceId")
            
            photos.add(
                Photo(
                    id = i.toLong(),
                    uri = uri,
                    name = name,
                    dateTaken = Date(dateOffset),
                    albumName = albumName
                )
            )
        }
        
        return photos
    }
    
    /**
     * Tạo danh sách album mẫu từ danh sách ảnh
     */
    fun generateDummyAlbums(photos: List<Photo>): List<Album> {
        // Nhóm ảnh theo album
        val albumMap = photos.groupBy { it.albumName }
        
        return albumMap.map { (name, albumPhotos) ->
            Album(
                name = name,
                thumbnailUri = albumPhotos.first().uri,
                photoCount = albumPhotos.size
            )
        }
    }
} 
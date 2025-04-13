package com.example.photomanager.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.photomanager.model.Album
import com.example.photomanager.model.DummyData
import com.example.photomanager.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Date

class PhotoRepository(private val context: Context) {
    
    // Cho biết có sử dụng dữ liệu mẫu hay không
    private var useDummyData = true
    
    // Lưu trữ dữ liệu mẫu
    private val dummyPhotos by lazy { DummyData.generateDummyPhotos(context) }
    private val dummyAlbums by lazy { DummyData.generateDummyAlbums(dummyPhotos) }
    
    /**
     * Lấy tất cả ảnh từ bộ nhớ thiết bị
     */
    fun getAllPhotos(): Flow<List<Photo>> = flow {
        try {
            val photos = queryPhotos()
            if (photos.isNotEmpty()) {
                useDummyData = false
                emit(photos)
            } else {
                emit(dummyPhotos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(dummyPhotos)
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Lấy tất cả album từ bộ nhớ thiết bị
     */
    fun getAllAlbums(): Flow<List<Album>> = flow {
        try {
            val albums = queryAlbums()
            if (albums.isNotEmpty()) {
                useDummyData = false
                emit(albums)
            } else {
                emit(dummyAlbums)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(dummyAlbums)
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Lấy tất cả ảnh trong một album
     * @param albumName Tên album cần lấy ảnh
     */
    fun getPhotosByAlbum(albumName: String): Flow<List<Photo>> = flow {
        try {
            if (useDummyData) {
                emit(dummyPhotos.filter { it.albumName == albumName })
            } else {
                val photos = queryPhotosByAlbum(albumName)
                emit(photos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(dummyPhotos.filter { it.albumName == albumName })
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Xóa một ảnh
     * @param photo Ảnh cần xóa
     */
    suspend fun deletePhoto(photo: Photo): Boolean = withContext(Dispatchers.IO) {
        try {
            if (useDummyData) {
                // Giả lập xóa ảnh thành công
                return@withContext true
            }
            
            val deletedRows = context.contentResolver.delete(
                photo.uri,
                null,
                null
            )
            deletedRows > 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Cập nhật thông tin của ảnh
     * @param photo Ảnh cần cập nhật
     * @param newName Tên mới cho ảnh
     * @param newAlbumName Album mới cho ảnh
     */
    suspend fun updatePhoto(photo: Photo, newName: String? = null, newAlbumName: String? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            if (useDummyData) {
                // Giả lập cập nhật ảnh thành công
                return@withContext true
            }
            
            val contentValues = ContentValues().apply {
                newName?.let { put(MediaStore.Images.Media.DISPLAY_NAME, it) }
                // Trên thực tế, việc di chuyển ảnh giữa các album phức tạp hơn,
                // cần phương pháp khác tùy thuộc vào API level
            }
            
            val updatedRows = context.contentResolver.update(
                photo.uri,
                contentValues,
                null,
                null
            )
            updatedRows > 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Query để lấy tất cả ảnh từ MediaStore
     */
    private fun queryPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateTaken = Date(cursor.getLong(dateTakenColumn))
                val bucket = cursor.getString(bucketColumn) ?: "Unknown"
                
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                photos.add(
                    Photo(
                        id = id,
                        uri = contentUri,
                        name = name,
                        dateTaken = dateTaken,
                        albumName = bucket
                    )
                )
            }
        }
        
        return photos
    }
    
    /**
     * Query để lấy tất cả album
     */
    private fun queryAlbums(): List<Album> {
        val albumMap = mutableMapOf<String, MutableList<Uri>>()
        
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val bucket = cursor.getString(bucketColumn) ?: "Unknown"
                
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                if (!albumMap.containsKey(bucket)) {
                    albumMap[bucket] = mutableListOf()
                }
                albumMap[bucket]?.add(contentUri)
            }
        }
        
        return albumMap.map { (name, uris) ->
            Album(
                name = name,
                thumbnailUri = uris.first(),
                photoCount = uris.size
            )
        }
    }
    
    /**
     * Query để lấy ảnh trong một album cụ thể
     */
    private fun queryPhotosByAlbum(albumName: String): List<Photo> {
        val photos = mutableListOf<Photo>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(albumName)
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateTaken = Date(cursor.getLong(dateTakenColumn))
                val bucket = cursor.getString(bucketColumn) ?: "Unknown"
                
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                photos.add(
                    Photo(
                        id = id,
                        uri = contentUri,
                        name = name,
                        dateTaken = dateTaken,
                        albumName = bucket
                    )
                )
            }
        }
        
        return photos
    }
} 
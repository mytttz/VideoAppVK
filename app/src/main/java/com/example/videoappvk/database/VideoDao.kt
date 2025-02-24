package com.example.videoappvk.database

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(videos: List<VideoEntity>)

    @Query("SELECT * FROM videos LIMIT :pageSize OFFSET :page * :pageSize")
    suspend fun getVideosPage(page: Int, pageSize: Int): List<VideoEntity>

    @Query("SELECT * FROM videos")
    fun getVideosPagingSource(): PagingSource<Int, VideoEntity>

    @Query("DELETE FROM videos")
    suspend fun clearAll()

    @Query("SELECT * FROM videos WHERE user LIKE '%' || :query || '%' LIMIT :pageSize OFFSET :page * :pageSize")
    suspend fun searchVideos(query: String, page: Int, pageSize: Int): List<VideoEntity>

    @Query("SELECT * FROM videos WHERE user LIKE '%' || :query || '%'")
    fun searchVideosPagingSource(query: String): PagingSource<Int, VideoEntity>

}
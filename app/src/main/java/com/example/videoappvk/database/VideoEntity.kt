package com.example.videoappvk.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.videoappvk.dataclasses.User
import com.example.videoappvk.dataclasses.Video
import com.example.videoappvk.dataclasses.VideoFiles

@Entity(tableName = "videos", indices = [Index(value = ["externalID"], unique = true)])
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val externalID: Int,
    val user: String,
    val image: String,
    val duration: String,
    val videoUrl: String
) {
    fun toVideo(): Video = Video(
        id = this.externalID,
        dbID = this.id,
        user = User("", this.user),
        image = this.image,
        duration = this.duration,
        video_files = listOf(VideoFiles(link = this.videoUrl))
    )
}

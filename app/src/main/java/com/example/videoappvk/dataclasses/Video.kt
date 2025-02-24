package com.example.videoappvk.dataclasses

import com.example.videoappvk.database.VideoEntity

data class Video(
    val id: Int,
    val dbID: Long,
    val image: String,
    val duration: String,
    val video_files: List<VideoFiles>,
    val user: User
) {
    fun toVideoEntity(): VideoEntity = VideoEntity(
        externalID = this.id,
        user = this.user.name,
        image = this.image,
        duration = this.duration,
        videoUrl = this.video_files.firstOrNull()?.link ?: ""
    )
}

data class VideoFiles(
    val link: String
)

data class User(
    val id: String,
    val name: String
)

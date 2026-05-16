package com.pocketstudios.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_clips")
data class VideoClipEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val uri: String,
    val duration: Long
)
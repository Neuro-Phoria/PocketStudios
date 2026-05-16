package com.pocketstudios.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "text_overlays")
data class TextOverlayEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val text: String,
    val startTime: Long,
    val endTime: Long
)
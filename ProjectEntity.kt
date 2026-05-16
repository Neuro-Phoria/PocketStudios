package com.pocketstudios.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L,
    val thumbnailPath: String? = null,
    val aspectRatio: String = "9:16",
    val resolution: String = "1080p",
    val frameRate: Int = 30,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)

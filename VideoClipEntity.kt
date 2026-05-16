package com.pocketstudios.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "video_clips",
    foreignKeys = [ForeignKey(
        entity = ProjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["projectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("projectId")]
)
data class VideoClipEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val trackIndex: Int = 0,
    val filePath: String,
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 0L,
    val timelinePositionMs: Long = 0L,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val filterPresetId: String? = null,
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val saturation: Float = 0f,
    val warmth: Float = 0f,
    val transitionInId: String? = null,
    val transitionOutId: String? = null,
    val orderIndex: Int = 0
) {
    val durationMs: Long get() = endTimeMs - startTimeMs
}

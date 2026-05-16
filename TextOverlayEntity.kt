package com.pocketstudios.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "text_overlays",
    foreignKeys = [ForeignKey(
        entity = ProjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["projectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("projectId")]
)
data class TextOverlayEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val text: String,
    val fontFamily: String = "Inter",
    val fontSize: Float = 32f,
    val textColor: Long = 0xFFFFFFFF,
    val backgroundColor: Long = 0x00000000,
    val positionX: Float = 0.5f, // 0.0–1.0 normalized
    val positionY: Float = 0.8f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotationDegrees: Float = 0f,
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 3000L,
    val animationIn: String = "FADE_IN",
    val animationOut: String = "FADE_OUT",
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val alignment: String = "CENTER"
)

package com.pocketstudios.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pocketstudios.core.database.dao.ProjectDao
import com.pocketstudios.core.database.dao.VideoClipDao
import com.pocketstudios.core.database.entity.AudioTrackEntity
import com.pocketstudios.core.database.entity.ProjectEntity
import com.pocketstudios.core.database.entity.TextOverlayEntity
import com.pocketstudios.core.database.entity.VideoClipEntity

@Database(
    entities = [
        ProjectEntity::class,
        VideoClipEntity::class,
        AudioTrackEntity::class,
        TextOverlayEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class PocketStudiosDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun videoClipDao(): VideoClipDao

    companion object {
        const val DATABASE_NAME = "pocket_studios.db"
    }
}

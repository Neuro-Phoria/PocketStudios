package com.pocketstudios.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pocketstudios.data.dao.ProjectDao
import com.pocketstudios.data.dao.VideoClipDao
import com.pocketstudios.data.entity.ProjectEntity
import com.pocketstudios.data.entity.VideoClipEntity

@Database(
    entities = [ProjectEntity::class, VideoClipEntity::class],
    version = 1
)
abstract class PocketStudiosDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun videoClipDao(): VideoClipDao
}
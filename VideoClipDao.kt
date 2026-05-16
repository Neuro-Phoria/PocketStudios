package com.pocketstudios.core.database.dao

import androidx.room.*
import com.pocketstudios.core.database.entity.VideoClipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoClipDao {
    @Query("SELECT * FROM video_clips WHERE projectId = :projectId ORDER BY orderIndex ASC")
    fun getClipsForProject(projectId: String): Flow<List<VideoClipEntity>>

    @Query("SELECT * FROM video_clips WHERE id = :id")
    suspend fun getClipById(id: String): VideoClipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(clip: VideoClipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clips: List<VideoClipEntity>)

    @Update
    suspend fun update(clip: VideoClipEntity)

    @Delete
    suspend fun delete(clip: VideoClipEntity)

    @Query("DELETE FROM video_clips WHERE projectId = :projectId")
    suspend fun deleteAllForProject(projectId: String)

    @Transaction
    suspend fun splitClip(original: VideoClipEntity, clipA: VideoClipEntity, clipB: VideoClipEntity) {
        delete(original)
        insert(clipA)
        insert(clipB)
    }
}

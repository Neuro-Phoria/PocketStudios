package com.pocketstudios.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.pocketstudios.data.entity.VideoClipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoClipDao {
    @Query("SELECT * FROM video_clips WHERE projectId = :projectId")
    fun getClipsForProject(projectId: String): Flow<List<VideoClipEntity>>
    
    @Insert
    suspend fun insertClip(clip: VideoClipEntity)
    
    @Delete
    suspend fun deleteClip(clip: VideoClipEntity)
}
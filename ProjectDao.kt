package com.pocketstudios.core.database.dao

import androidx.room.*
import com.pocketstudios.core.database.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE isDeleted = 0 ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id AND isDeleted = 0")
    fun getProjectById(id: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity)

    @Update
    suspend fun update(project: ProjectEntity)

    @Query("UPDATE projects SET isDeleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE projects SET isSynced = :synced WHERE id = :id")
    suspend fun updateSyncStatus(id: String, synced: Boolean)

    @Query("SELECT COUNT(*) FROM projects WHERE isDeleted = 0")
    suspend fun getProjectCount(): Int
}

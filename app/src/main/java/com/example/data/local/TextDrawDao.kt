package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TextDrawDao {

    @Query("SELECT * FROM td_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM td_projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Query("DELETE FROM td_projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Long)

    @Query("SELECT * FROM td_elements WHERE projectId = :projectId ORDER BY zIndex ASC")
    fun getElementsByProjectId(projectId: Long): Flow<List<ElementEntity>>

    @Query("SELECT * FROM td_elements WHERE projectId = :projectId ORDER BY zIndex ASC")
    suspend fun getElementsSync(projectId: Long): List<ElementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElement(element: ElementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElements(elements: List<ElementEntity>)

    @Query("DELETE FROM td_elements WHERE id = :elementId")
    suspend fun deleteElementById(elementId: String)

    @Query("DELETE FROM td_elements WHERE projectId = :projectId")
    suspend fun deleteElementsByProjectId(projectId: Long)

    @Transaction
    suspend fun replaceAllElements(projectId: Long, elements: List<ElementEntity>) {
        deleteElementsByProjectId(projectId)
        insertElements(elements)
    }
}

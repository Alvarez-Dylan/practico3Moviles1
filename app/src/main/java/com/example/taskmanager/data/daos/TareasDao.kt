package com.example.taskmanager.data.daos

import androidx.room.*
import com.example.taskmanager.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TareasDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskWithLabels>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<TaskWithLabels?>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTasks(query: String): Flow<List<TaskWithLabels>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY createdAt DESC")
    fun getTasksByStatus(status: Status): Flow<List<TaskWithLabels>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: Priority): Flow<List<TaskWithLabels>>

    @Query("""
        SELECT DISTINCT t.* FROM tasks t
        INNER JOIN task_label_cross_ref tlcr ON t.id = tlcr.taskId
        WHERE tlcr.labelId = :labelId
        ORDER BY t.createdAt DESC
    """)
    fun getTasksByLabel(labelId: Int): Flow<List<TaskWithLabels>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(tareas: Tareas): Long

    @Update
    suspend fun updateTask(tareas: Tareas)

    @Delete
    suspend fun deleteTask(tareas: Tareas)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLabelCrossRef(crossRef: TaskLabelCrossRef)

    @Delete
    suspend fun deleteTaskLabelCrossRef(crossRef: TaskLabelCrossRef)

    @Query("DELETE FROM task_label_cross_ref WHERE taskId = :taskId")
    suspend fun deleteAllLabelsFromTask(taskId: Int)

}

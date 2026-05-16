package com.example.taskmanager.data.daos

import androidx.room.*
import com.example.taskmanager.data.entities.Etiquetas
import kotlinx.coroutines.flow.Flow

@Dao
interface EtiquetasDao {

    @Query("SELECT * FROM labels ORDER BY name ASC")
    fun getAllLabels(): Flow<List<Etiquetas>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(etiquetas: Etiquetas): Long

    @Update
    suspend fun updateLabel(etiquetas: Etiquetas)

    @Delete
    suspend fun deleteLabel(etiquetas: Etiquetas)

    @Query("DELETE FROM task_label_cross_ref WHERE labelId = :labelId")
    suspend fun deleteAllTasksFromLabel(labelId: Int)

}

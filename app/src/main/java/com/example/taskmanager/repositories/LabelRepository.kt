package com.example.taskmanager.repositories

import com.example.taskmanager.data.daos.EtiquetasDao
import com.example.taskmanager.data.entities.Etiquetas
import kotlinx.coroutines.flow.Flow

class LabelRepository(
    private val etiquetasDao: EtiquetasDao
) {

    fun getAllLabels():
            Flow<List<Etiquetas>> = etiquetasDao.getAllLabels()
    suspend fun insertLabel(etiquetas: Etiquetas):
            Long = etiquetasDao.insertLabel(etiquetas)
    suspend fun updateLabel(etiquetas: Etiquetas) =
        etiquetasDao.updateLabel(etiquetas)

    suspend fun deleteLabel(etiquetas: Etiquetas) {
        etiquetasDao.deleteAllTasksFromLabel(etiquetas.id)
        etiquetasDao.deleteLabel(etiquetas)
    }
}

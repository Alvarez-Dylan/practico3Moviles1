package com.example.taskmanager.repositories

import com.example.taskmanager.data.daos.TareasDao
import com.example.taskmanager.data.entities.*
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val tareasDao: TareasDao) {

    fun getAllTasks():
            Flow<List<TaskWithLabels>> = tareasDao.getAllTasks()
    fun getTaskById(id: Int):
            Flow<TaskWithLabels?> = tareasDao.getTaskById(id)
    fun searchTasks(query: String):
            Flow<List<TaskWithLabels>> = tareasDao.searchTasks(query)
    fun getTasksByStatus(status: Status):
            Flow<List<TaskWithLabels>> = tareasDao.getTasksByStatus(status)
    fun getTasksByPriority(priority: Priority):
            Flow<List<TaskWithLabels>> = tareasDao.getTasksByPriority(priority)
    fun getTasksByLabel(labelId: Int):
            Flow<List<TaskWithLabels>> = tareasDao.getTasksByLabel(labelId)

    suspend fun insertTask(tareas: Tareas):
            Long = tareasDao.insertTask(tareas)
    suspend fun updateTask(tareas: Tareas) =
        tareasDao.updateTask(tareas)
    suspend fun deleteTask(tareas: Tareas) =
        tareasDao.deleteTask(tareas)

    suspend fun updateTaskLabels(taskId: Int, labelIds: List<Int>) {
        tareasDao.deleteAllLabelsFromTask(taskId)
        labelIds.forEach { labelId ->
            tareasDao.insertTaskLabelCrossRef(TaskLabelCrossRef(taskId, labelId))
        }
    }
}

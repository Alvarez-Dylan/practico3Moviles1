package com.example.taskmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.entities.TaskWithLabels
import com.example.taskmanager.repositories.TaskRepository
import com.example.taskmanager.ui.states.TaskDetailState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskRepository: TaskRepository,
    private val taskId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    init {
        cargarTarea()
    }

    private fun cargarTarea() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                taskRepository.getTaskById(taskId).collect { tareaConEtiquetas ->
                    if (tareaConEtiquetas != null) {
                        _state.update {
                            it.copy(
                                taskWithLabels = tareaConEtiquetas,
                                isLoading = false,
                                error = null
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                taskWithLabels = null,
                                isLoading = false,
                                error = "Tarea no encontrada"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar la tarea: ${e.message}"
                    )
                }
            }
        }
    }

    fun recargarTarea() {
        cargarTarea()
    }
}

class TaskDetailViewModelFactory(
    private val taskRepository: TaskRepository,
    private val taskId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TaskDetailViewModel(taskRepository, taskId) as T
}
package com.example.taskmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.entities.*
import com.example.taskmanager.repositories.LabelRepository
import com.example.taskmanager.repositories.TaskRepository
import com.example.taskmanager.ui.states.TaskFormState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskFormViewModel(
    private val taskRepository: TaskRepository,
    private val labelRepository: LabelRepository,
    private val taskId: Int? = null
) : ViewModel() {


    private val _state = MutableStateFlow(TaskFormState())
    val state: StateFlow<TaskFormState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            labelRepository.getAllLabels().collect { labels ->
                _state.update { it.copy(availableEtiquetas = labels) }
            }
        }
        taskId?.let { loadTask(it) }
    }

    private fun loadTask(id: Int) {
        viewModelScope.launch {
            taskRepository.getTaskById(id).first()?.let { tareaConEtiquetas ->
                _state.update {
                    it.copy(
                        taskId = tareaConEtiquetas.tareas.id,
                        title = tareaConEtiquetas.tareas.title,
                        description = tareaConEtiquetas.tareas.description,
                        dueDate = tareaConEtiquetas.tareas.dueDate,
                        priority = tareaConEtiquetas.tareas.priority,
                        status = tareaConEtiquetas.tareas.status,
                        selectedLabelIds = tareaConEtiquetas.etiquetas.map { etiquetaIndividual -> etiquetaIndividual.id }.toSet()
                    )
                }
            }
        }
    }

    fun onTitleChange(title: String) =
        _state.update { it.copy(title = title, titleError = null) }
    fun onDescriptionChange(desc: String) =
        _state.update { it.copy(description = desc) }
    fun onDueDateChange(date: Long?) =
        _state.update { it.copy(dueDate = date) }
    fun onPriorityChange(priority: Priority) =
        _state.update { it.copy(priority = priority) }
    fun onStatusChange(status: Status) =
        _state.update { it.copy(status = status) }

    fun onLabelToggle(labelId: Int) {
        val current = _state.value.selectedLabelIds
        _state.update {
            it.copy(selectedLabelIds =
                if (current.contains(labelId))
                    current - labelId
                else
                    current + labelId)
        }
    }

    fun saveTask() {
        val estadoActual = _state.value
        if (estadoActual.title.isBlank()) {
            _state.update { it.copy(titleError = "El título es obligatorio") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val tareas = Tareas(
                id = estadoActual.taskId ?: 0,
                title = estadoActual.title.trim(),
                description = estadoActual.description.trim(),
                dueDate = estadoActual.dueDate,
                priority = estadoActual.priority,
                status = estadoActual.status
            )
            val savedId: Int = if (estadoActual.taskId == null) {
                taskRepository.insertTask(tareas).toInt()
            } else {
                taskRepository.updateTask(tareas); estadoActual.taskId
            }
            taskRepository.updateTaskLabels(savedId, estadoActual.selectedLabelIds.toList())
            _state.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}

class TaskFormViewModelFactory(
    private val taskRepo: TaskRepository,
    private val labelRepo: LabelRepository,
    private val taskId: Int? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TaskFormViewModel(taskRepo, labelRepo, taskId) as T
}

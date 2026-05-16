package com.example.taskmanager.ui.states

import com.example.taskmanager.data.entities.Etiquetas
import com.example.taskmanager.data.entities.Priority
import com.example.taskmanager.data.entities.Status

data class TaskFormState(
    val taskId: Int? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.PENDING,
    val selectedLabelIds: Set<Int> = emptySet(),
    val availableEtiquetas: List<Etiquetas> = emptyList(),
    val titleError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

package com.example.taskmanager.ui.states

import com.example.taskmanager.data.entities.Etiquetas
import com.example.taskmanager.data.entities.Priority
import com.example.taskmanager.data.entities.Status
import com.example.taskmanager.data.entities.TaskWithLabels

enum class SortOrder {
    CREATED_DESC, CREATED_ASC, DUE_DATE, PRIORITY, TITLE
}

data class HomeState(
    val tasks: List<TaskWithLabels> = emptyList(),
    val etiquetas: List<Etiquetas> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: Status? = null,
    val filterPriority: Priority? = null,
    val filterLabelId: Int? = null,
    val sortOrder: SortOrder = SortOrder.CREATED_DESC,
    val isLoading: Boolean = false
)
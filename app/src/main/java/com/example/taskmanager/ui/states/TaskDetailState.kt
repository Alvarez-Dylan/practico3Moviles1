package com.example.taskmanager.ui.states

import com.example.taskmanager.data.entities.TaskWithLabels

data class TaskDetailState(
    val taskWithLabels: TaskWithLabels? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
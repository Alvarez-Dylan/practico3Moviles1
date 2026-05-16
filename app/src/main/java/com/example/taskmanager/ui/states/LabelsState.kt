package com.example.taskmanager.ui.states

import com.example.taskmanager.data.entities.Etiquetas

data class LabelsState(
    val etiquetas: List<Etiquetas> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingEtiquetas: Etiquetas? = null,
    val newLabelName: String = "",
    val newLabelColor: String = "#6200EE",
    val labelNameError: String? = null
)

package com.example.taskmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.entities.Etiquetas
import com.example.taskmanager.repositories.LabelRepository
import com.example.taskmanager.ui.states.LabelsState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LabelsViewModel(private val labelRepository: LabelRepository) : ViewModel() {

    private val _state = MutableStateFlow(LabelsState())
    val state: StateFlow<LabelsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            labelRepository.getAllLabels().collect { labels ->
                _state.update { it.copy(etiquetas = labels) }
            }
        }
    }

    fun onShowAddDialog() =
        _state.update { it.copy(
            showAddDialog = true,
            editingEtiquetas = null,
            newLabelName = "",
            newLabelColor = "#6200EE")
        }
    fun onShowEditDialog(etiquetas: Etiquetas) =
        _state.update { it.copy(
            showAddDialog = true,
            editingEtiquetas = etiquetas,
            newLabelName = etiquetas.name,
            newLabelColor = etiquetas.color)
        }
    fun onDismissDialog() =
        _state.update { it.copy(
            showAddDialog = false,
            editingEtiquetas = null,
            newLabelName = "",
            newLabelColor = "#6200EE",
            labelNameError = null)
        }
    fun onLabelNameChange(name: String) =
        _state.update { it.copy(
            newLabelName = name,
            labelNameError = null)
        }
    fun onLabelColorChange(color: String) =
        _state.update { it.copy(newLabelColor = color) }

    fun saveLabel() {
        val estadoActual = _state.value
        if (estadoActual.newLabelName.isBlank()) {
            _state.update { it.copy(labelNameError = "El nombre es obligatorio") }
            return
        }
        viewModelScope.launch {
            val etiquetas = Etiquetas(
                id = estadoActual.editingEtiquetas?.id ?: 0,
                name = estadoActual.newLabelName.trim(),
                color = estadoActual.newLabelColor)

            if (estadoActual.editingEtiquetas == null)
                labelRepository.insertLabel(etiquetas)
            else labelRepository.updateLabel(etiquetas)
            onDismissDialog()
        }
    }

    fun deleteLabel(etiquetas: Etiquetas) {
        viewModelScope.launch {
            labelRepository.deleteLabel(etiquetas)
        }
    }
}

class LabelsViewModelFactory(
    private val labelRepo: LabelRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        LabelsViewModel(labelRepo) as T
}

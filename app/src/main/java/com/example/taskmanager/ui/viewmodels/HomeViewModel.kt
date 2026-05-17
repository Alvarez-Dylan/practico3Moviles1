package com.example.taskmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.entities.*
import com.example.taskmanager.repositories.LabelRepository
import com.example.taskmanager.repositories.TaskRepository
import com.example.taskmanager.ui.states.HomeState
import com.example.taskmanager.ui.states.SortOrder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val labelRepository: LabelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var searchQuery = ""
    private var filterStatus: Status? = null
    private var filterPriority: Priority? = null
    private var filterLabelId: Int? = null
    private var sortOrder = SortOrder.CREATED_DESC
    private var todasLasTareas: List<TaskWithLabels> = emptyList()
    private var etiquetasList: List<Etiquetas> = emptyList()

    init {
        viewModelScope.launch {
            labelRepository.getAllLabels().collect { listaDeEtiquetas ->
                etiquetasList = listaDeEtiquetas
                actualizarListaDeTareas()
            }
        }

        viewModelScope.launch {
            taskRepository.getAllTasks().collect { listaDesdeRoom ->
                todasLasTareas = listaDesdeRoom
                actualizarListaDeTareas()
            }
        }
    }

    private fun actualizarListaDeTareas() {
        var listaFiltrada = todasLasTareas

        if (searchQuery.isNotBlank()) {
            listaFiltrada = listaFiltrada.filter {
                it.tareas.title.contains(searchQuery, ignoreCase = true)
            }
        }

        if (filterStatus != null) {
            listaFiltrada = listaFiltrada.filter { it.tareas.status == filterStatus }
        }

        if (filterPriority != null) {
            listaFiltrada = listaFiltrada.filter { it.tareas.priority == filterPriority }
        }

        if (filterLabelId != null) {
            listaFiltrada = listaFiltrada.filter { relacion ->
                relacion.etiquetas.any { etiqueta -> etiqueta.id == filterLabelId }
            }
        }

        val listaOrdenada = ordenarTareas(listaFiltrada, sortOrder)

        _state.update { estadoActual ->
            estadoActual.copy(
                tasks = listaOrdenada,
                etiquetas = etiquetasList,
                searchQuery = searchQuery,
                filterStatus = filterStatus,
                filterPriority = filterPriority,
                filterLabelId = filterLabelId,
                sortOrder = sortOrder
            )
        }
    }

    private fun ordenarTareas(tareas: List<TaskWithLabels>, orden: SortOrder): List<TaskWithLabels> {
        return when (orden) {
            SortOrder.CREATED_DESC -> tareas.sortedByDescending { it.tareas.createdAt }
            SortOrder.CREATED_ASC -> tareas.sortedBy { it.tareas.createdAt }
            SortOrder.DUE_DATE -> tareas.sortedWith(compareBy(nullsLast()) { it.tareas.dueDate })
            SortOrder.PRIORITY -> tareas.sortedBy { it.tareas.priority.ordinal }
            SortOrder.TITLE -> tareas.sortedBy { it.tareas.title.lowercase() }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        filterStatus = null
        filterPriority = null
        filterLabelId = null
        actualizarListaDeTareas()
    }

    fun onFilterStatus(status: Status?) {
        filterStatus = status
        searchQuery = ""
        filterPriority = null
        filterLabelId = null
        actualizarListaDeTareas()
    }

    fun onFilterPriority(priority: Priority?) {
        filterPriority = priority
        searchQuery = ""
        filterStatus = null
        filterLabelId = null
        actualizarListaDeTareas()
    }

    fun onFilterLabel(labelId: Int?) {
        filterLabelId = labelId
        searchQuery = ""
        filterStatus = null
        filterPriority = null
        actualizarListaDeTareas()
    }

    fun onSortOrderChange(orden: SortOrder) {
        sortOrder = orden
        actualizarListaDeTareas()
    }

    fun onToggleTaskStatus(tarea: Tareas) {
        viewModelScope.launch {
            val nuevoEstado = if (tarea.status == Status.PENDING) Status.COMPLETED else Status.PENDING
            taskRepository.updateTask(tarea.copy(status = nuevoEstado))
        }
    }

    fun onDeleteTask(tarea: Tareas) {
        viewModelScope.launch {
            taskRepository.deleteTask(tarea)
        }
    }
}

class HomeViewModelFactory(
    private val taskRepo: TaskRepository,
    private val labelRepo: LabelRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        HomeViewModel(taskRepo, labelRepo) as T
}
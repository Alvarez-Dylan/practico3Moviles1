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
    private var etiquetasList: List<Etiquetas> = emptyList()

    init {
        actualizarListaDeTareas()
        // Cargar las etiquetas al iniciar
        viewModelScope.launch {
            labelRepository.getAllLabels().collect { listaDeEtiquetas ->
                etiquetasList = listaDeEtiquetas
                actualizarListaDeTareas()
            }
        }

    }

    // Función principal que actualiza la lista según los filtros actuales
    private fun actualizarListaDeTareas() {
        viewModelScope.launch {
            val tareasFlow = when {
                searchQuery.isNotBlank() -> taskRepository.searchTasks(searchQuery)
                filterStatus != null -> taskRepository.getTasksByStatus(filterStatus!!)
                filterPriority != null -> taskRepository.getTasksByPriority(filterPriority!!)
                filterLabelId != null -> taskRepository.getTasksByLabel(filterLabelId!!)
                else -> taskRepository.getAllTasks()
            }

            // Obtener los resultados y ordenar
            tareasFlow.collect { listaOriginal ->
                val listaOrdenada = ordenarTareas(listaOriginal, sortOrder)

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
        }
    }

    // Ordena la lista según el criterio seleccionado
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
        _state.update { estadoActual ->
            estadoActual.copy(
                searchQuery = query,
                tasks = estadoActual.tasks
            )
        }
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

    // Cambiar estado de una tarea (pendiente ↔ completada)
    fun onToggleTaskStatus(tarea: Tareas) {
        viewModelScope.launch {
            val nuevoEstado = if (tarea.status == Status.PENDING) {
                Status.COMPLETED
            } else {
                Status.PENDING
            }
            taskRepository.updateTask(tarea.copy(status = nuevoEstado))
            actualizarListaDeTareas()
        }
    }

    fun onDeleteTask(tarea: Tareas) {
        viewModelScope.launch {
            taskRepository.deleteTask(tarea)
            actualizarListaDeTareas()
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
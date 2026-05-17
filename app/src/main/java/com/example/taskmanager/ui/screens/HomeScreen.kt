package com.example.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanager.TaskManagerApplication
import com.example.taskmanager.data.entities.Priority
import com.example.taskmanager.data.entities.Status
import com.example.taskmanager.data.entities.Tareas
import com.example.taskmanager.ui.components.TaskCard
import com.example.taskmanager.ui.states.SortOrder
import com.example.taskmanager.ui.viewmodels.HomeViewModel
import com.example.taskmanager.ui.viewmodels.HomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    application: TaskManagerApplication,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateToLabels: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {

    val vm: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(application.taskRepository, application.labelRepository)
    )
    val state by vm.state.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf<Tareas?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    showDeleteDialog?.let { task ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Deseas eliminar \"${task.title}\"?") },
            confirmButton = {
                TextButton(onClick = { vm.onDeleteTask(task); showDeleteDialog = null }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, "Filtros")
                    }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, "Ordenar")
                    }
                    IconButton(onClick = onNavigateToLabels) {
                        Icon(Icons.Default.Label, "Etiquetas")
                    }

                    DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                        Text("  Ordenar por", style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                        SortOrder.entries.forEach { order ->
                            val label = when (order) {
                                SortOrder.CREATED_DESC -> "Creación (reciente)"
                                SortOrder.CREATED_ASC  -> "Creación (antiguo)"
                                SortOrder.DUE_DATE     -> "Fecha de vencimiento"
                                SortOrder.PRIORITY     -> "Prioridad"
                                SortOrder.TITLE        -> "Título"
                            }
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = { vm.onSortOrderChange(order); showSortMenu = false },
                                leadingIcon = {
                                    if (state.sortOrder == order)
                                        Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                                    else Spacer(Modifier.size(16.dp))
                                }
                            )
                        }
                    }

                    DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                        Text("  Filtrar por estado", style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                        listOf(null to "Todos", Status.PENDING to "Pendiente", Status.COMPLETED to "Completada").forEach { (s, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = { vm.onFilterStatus(s); showFilterMenu = false },
                                leadingIcon = {
                                    if (state.filterStatus == s) Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                                    else Spacer(Modifier.size(16.dp))
                                }
                            )
                        }
                        HorizontalDivider()
                        Text("  Filtrar por prioridad", style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                        listOf(null to "Todas", Priority.HIGH to "Alta", Priority.MEDIUM to "Media", Priority.LOW to "Baja").forEach { (p, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = { vm.onFilterPriority(p); showFilterMenu = false },
                                leadingIcon = {
                                    if (state.filterPriority == p) Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                                    else Spacer(Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                Icon(Icons.Default.Add, "Nueva tarea")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = vm::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar tareas...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (state.searchQuery.isNotBlank())
                        IconButton(onClick = { vm.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, "Limpiar")
                        }
                },
                singleLine = true
            )

            if (state.etiquetas.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.filterLabelId == null,
                            onClick = { vm.onFilterLabel(null) },
                            label = { Text("Todas") }
                        )
                    }
                    items(state.etiquetas) { label ->
                        FilterChip(
                            selected = state.filterLabelId == label.id,
                            onClick = { vm.onFilterLabel(label.id) },
                            label = { Text(label.name) }
                        )
                    }
                }
            }

            if (state.tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Assignment, null, Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Text("No hay tareas", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Toca + para crear una nueva", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tasks, key = { it.tareas.id }) { taskWithLabels ->
                        TaskCard(
                            taskWithLabels = taskWithLabels,
                            onToggleStatus = { vm.onToggleTaskStatus(taskWithLabels.tareas) },
                            onEdit = { onNavigateToForm(taskWithLabels.tareas.id) },
                            onDelete = { showDeleteDialog = taskWithLabels.tareas },
                            onViewDetail = { onNavigateToDetail(taskWithLabels.tareas.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}
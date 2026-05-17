package com.example.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanager.TaskManagerApplication
import com.example.taskmanager.data.entities.Priority
import com.example.taskmanager.data.entities.Status
import com.example.taskmanager.ui.viewmodels.TaskFormViewModel
import com.example.taskmanager.ui.viewmodels.TaskFormViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    application: TaskManagerApplication,
    taskId: Int?,
    onNavigateBack: () -> Unit
) {

    val vm: TaskFormViewModel = viewModel(
        factory = TaskFormViewModelFactory(application.taskRepository, application.labelRepository, taskId)
    )
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.dueDate ?: System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.onDueDateChange(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                if (state.dueDate != null) {
                    TextButton(onClick = { vm.onDueDateChange(null); showDatePicker = false }) {
                        Text("Quitar fecha")
                    }
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Nueva Tarea" else "Editar Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = vm::saveTask, enabled = !state.isLoading) {
                        if (state.isLoading) CircularProgressIndicator(Modifier.size(16.dp))
                        else Text("Guardar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            OutlinedTextField(
                value = state.title,
                onValueChange = vm::onTitleChange,
                label = { Text("Título ") },
                isError = state.titleError != null,
                supportingText = state.titleError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Descripción
            OutlinedTextField(
                value = state.description,
                onValueChange = vm::onDescriptionChange,
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Fecha de vencimiento
            OutlinedTextField(
                value = state.dueDate?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                } ?: "",
                onValueChange = {},
                label = { Text("Fecha de vencimiento (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, "Seleccionar fecha")
                    }
                }
            )

            // Prioridad
            Text("Prioridad", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Priority.HIGH to "Alta", Priority.MEDIUM to "Media", Priority.LOW to "Baja").forEach { (Prioridad, label) ->
                    FilterChip(
                        selected = state.priority == Prioridad,
                        onClick = { vm.onPriorityChange(Prioridad) },
                        label = { Text(label) }
                    )
                }
            }

            // Estado
            Text("Estado", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Status.PENDING to "Pendiente", Status.COMPLETED to "Completada").forEach { (estadoActual, label) ->
                    FilterChip(
                        selected = state.status == estadoActual,
                        onClick = { vm.onStatusChange(estadoActual) },
                        label = { Text(label) },
                        enabled = estadoActual == Status.PENDING || taskId != null
                    )
                }
            }

            // Etiquetas
            if (state.availableEtiquetas.isNotEmpty()) {
                Text("Etiquetas", style = MaterialTheme.typography.labelLarge)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    state.availableEtiquetas.forEach { label ->
                        val selected = state.selectedLabelIds.contains(label.id)
                        FilterChip(
                            selected = selected,
                            onClick = { vm.onLabelToggle(label.id) },
                            label = { Text(label.name) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

package com.example.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanager.TaskManagerApplication
import com.example.taskmanager.data.entities.Etiquetas
import com.example.taskmanager.ui.viewmodels.LabelsViewModel
import com.example.taskmanager.ui.viewmodels.LabelsViewModelFactory

val PRESET_COLORS = listOf(
    "#F44336","#E91E63","#9C27B0","#673AB7",
    "#3F51B5","#2196F3","#03A9F4","#00BCD4",
    "#009688","#4CAF50","#8BC34A","#CDDC39",
    "#FFC107","#FF9800","#FF5722","#795548"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelsScreen(
    application: TaskManagerApplication,
    onNavigateBack: () -> Unit
) {

    val vm: LabelsViewModel = viewModel(
        factory = LabelsViewModelFactory(application.labelRepository)
    )
    val state by vm.state.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<Etiquetas?>(null) }

    deleteTarget?.let { label ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Eliminar etiqueta") },
            text = { Text("¿Eliminar \"${label.name}\"? Se desasociará de todas las tareas.") },
            confirmButton = {
                TextButton(onClick = { vm.deleteLabel(label); deleteTarget = null }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") } }
        )
    }

    if (state.showAddDialog) {
        AlertDialog(
            onDismissRequest = vm::onDismissDialog,
            title = { Text(if (state.editingEtiquetas == null) "Nueva etiqueta" else "Editar etiqueta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.newLabelName,
                        onValueChange = vm::onLabelNameChange,
                        label = { Text("Nombre ") },
                        isError = state.labelNameError != null,
                        supportingText = state.labelNameError?.let { { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Color", style = MaterialTheme.typography.labelMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PRESET_COLORS.chunked(8).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { hex ->
                                    val color = try { Color(android.graphics.Color.parseColor(hex)) }
                                             catch (e: Exception) { Color.Gray }
                                    IconButton(
                                        onClick = { vm.onLabelColorChange(hex) },
                                        modifier = Modifier.size(32.dp).clip(CircleShape)
                                    ) {
                                        Surface(
                                            color = color,
                                            shape = CircleShape,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            if (state.newLabelColor == hex) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.Check, null,
                                                        Modifier.size(16.dp),
                                                        tint = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = vm::saveLabel) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = vm::onDismissDialog) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etiquetas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = vm::onShowAddDialog) {
                Icon(Icons.Default.Add, "Nueva etiqueta")
            }
        }
    ) { padding ->
        if (state.etiquetas.isEmpty()) {
            Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Label, null, Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    Text("No hay etiquetas", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Toca + para crear una", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.etiquetas, key = { it.id }) { label ->
                    val color = try { Color(android.graphics.Color.parseColor(label.color)) }
                                catch (e: Exception) { MaterialTheme.colorScheme.primary }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = color,
                                shape = CircleShape,
                                modifier = Modifier.size(24.dp)
                            ) {}
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = label.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { vm.onShowEditDialog(label) }) {
                                Icon(Icons.Default.Edit, "Editar")
                            }
                            IconButton(onClick = { deleteTarget = label }) {
                                Icon(Icons.Default.Delete, "Eliminar",
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

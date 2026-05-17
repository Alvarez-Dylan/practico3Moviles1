package com.example.taskmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanager.data.entities.Status
import com.example.taskmanager.data.entities.TaskWithLabels
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskCard(
    taskWithLabels: TaskWithLabels,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task = taskWithLabels.tareas
    val isCompleted = task.status == Status.COMPLETED
    val alpha = if (isCompleted) 0.55f else 1f

    // Estado para controlar si el menú está expandido
    var expanded by remember { mutableStateOf(false) }

    // Etiquetas visibles (primeras 3) y ocultas (restantes)
    val visibleLabels = taskWithLabels.etiquetas.take(3)
    val hiddenLabels = taskWithLabels.etiquetas.drop(3)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleStatus() },
                modifier = Modifier.padding(top = 2.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .clickable { onViewDetail() }
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    modifier = Modifier.alpha(alpha),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alpha(alpha)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    PriorityIndicator(priority = task.priority)

                    task.dueDate?.let { dueDate ->
                        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(dueDate))
                        val isOverdue = dueDate < System.currentTimeMillis() && !isCompleted
                        val tint = if (isOverdue) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, null, Modifier.size(12.dp), tint = tint)
                            Spacer(Modifier.width(2.dp))
                            Text(text = dateStr, style = MaterialTheme.typography.labelSmall, color = tint)
                        }
                    }
                }

                if (taskWithLabels.etiquetas.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mostrar primeras 3 etiquetas
                        visibleLabels.forEach { LabelChip(etiquetas = it) }

                        // Mostrar botón +N si hay más etiquetas
                        if (hiddenLabels.isNotEmpty()) {
                            Box {
                                // Botón +N
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    modifier = Modifier
                                        .height(24.dp)
                                        .wrapContentWidth()
                                        .clickable { expanded = true }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.MoreHoriz,
                                            contentDescription = "Más etiquetas",
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${hiddenLabels.size}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                // Dropdown menu con las etiquetas ocultas
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(4.dp)
                                ) {
                                    hiddenLabels.forEach { label ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Círculo de color
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .clip(RoundedCornerShape(50))
                                                            .background(
                                                                try {
                                                                    Color(android.graphics.Color.parseColor(label.color))
                                                                } catch (e: Exception) {
                                                                    MaterialTheme.colorScheme.primary
                                                                }
                                                            )
                                                    )
                                                    Text(
                                                        text = label.name,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            },
                                            onClick = { expanded = false } // Solo cierra el menú (opcional)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, "Editar", Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, "Eliminar", Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
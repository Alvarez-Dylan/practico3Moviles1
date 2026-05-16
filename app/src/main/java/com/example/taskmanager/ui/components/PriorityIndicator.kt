package com.example.taskmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.entities.Priority
import com.example.taskmanager.ui.theme.PriorityHigh
import com.example.taskmanager.ui.theme.PriorityLow
import com.example.taskmanager.ui.theme.PriorityMedium

@Composable
fun PriorityIndicator(priority: Priority, modifier: Modifier = Modifier) {
    val (color, label) = when (priority) {
        Priority.HIGH   -> PriorityHigh   to "Alta"
        Priority.MEDIUM -> PriorityMedium to "Media"
        Priority.LOW    -> PriorityLow    to "Baja"
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}


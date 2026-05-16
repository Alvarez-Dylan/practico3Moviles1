package com.example.taskmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.entities.Etiquetas

@Composable
fun LabelChip(etiquetas: Etiquetas, modifier: Modifier = Modifier) {
    val color = try {
        Color(android.graphics.Color.parseColor(etiquetas.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = etiquetas.name, style = MaterialTheme.typography.labelSmall, color = color)
    }
}


package com.example.taskmanager.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithLabels(
    @Embedded val tareas: Tareas,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskLabelCrossRef::class,
            parentColumn = "taskId",
            entityColumn = "labelId"
        )
    )
    val etiquetas: List<Etiquetas>
)

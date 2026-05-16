package com.example.taskmanager.data.entities

import androidx.room.Entity

@Entity(
    tableName = "task_label_cross_ref",
    primaryKeys = ["taskId", "labelId"]

)
data class TaskLabelCrossRef(
    val taskId: Int,
    val labelId: Int
)

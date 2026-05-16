package com.example.taskmanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority { HIGH, MEDIUM, LOW }
enum class Status { PENDING, COMPLETED }

@Entity(tableName = "tasks")
data class Tareas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.PENDING,
    val createdAt: Long = System.currentTimeMillis()
) {

}

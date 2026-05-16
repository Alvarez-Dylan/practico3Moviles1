package com.example.taskmanager

import android.app.Application
import com.example.taskmanager.data.db.AppDatabase
import com.example.taskmanager.repositories.LabelRepository
import com.example.taskmanager.repositories.TaskRepository

class TaskManagerApplication : Application() {

    val database by lazy {
        AppDatabase.getDatabase(this)
    }
    val taskRepository by lazy {
        TaskRepository(database.taskDao())
    }
    val labelRepository by lazy {
        LabelRepository(database.labelDao())
    }
}

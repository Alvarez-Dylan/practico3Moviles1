package com.example.taskmanager.data.db

import android.content.Context
import androidx.room.*
import com.example.taskmanager.data.daos.EtiquetasDao
import com.example.taskmanager.data.daos.TareasDao
import com.example.taskmanager.data.entities.Etiquetas
import com.example.taskmanager.data.entities.Tareas
import com.example.taskmanager.data.entities.TaskLabelCrossRef

@Database(
    entities = [Tareas::class, Etiquetas::class, TaskLabelCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TareasDao
    abstract fun labelDao(): EtiquetasDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_manager_db"
                ).build().also { INSTANCE = it }
            }
    }
}

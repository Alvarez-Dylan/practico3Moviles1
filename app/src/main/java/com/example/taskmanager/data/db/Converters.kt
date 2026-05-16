package com.example.taskmanager.data.db

import androidx.room.TypeConverter
import com.example.taskmanager.data.entities.Priority
import com.example.taskmanager.data.entities.Status

class Converters {

    @TypeConverter fun fromPriority(prioridad: Priority):
            String = prioridad.name
    @TypeConverter fun toPriority(priorityValue: String):
            Priority = Priority.valueOf(priorityValue)
    @TypeConverter fun fromStatus(estado: Status):
            String = estado.name
    @TypeConverter fun toStatus(statusValue: String):
            Status = Status.valueOf(statusValue)

}

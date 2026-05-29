package com.example.taskcatalyst.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isUrgent: Boolean,
    val isImportant: Boolean,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false
)

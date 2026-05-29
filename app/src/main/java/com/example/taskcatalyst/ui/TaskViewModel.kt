package com.example.taskcatalyst.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskcatalyst.data.AppDatabase
import com.example.taskcatalyst.data.Task
import com.example.taskcatalyst.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    val q1Tasks: StateFlow<List<Task>>
    val q2Tasks: StateFlow<List<Task>>
    val q3Tasks: StateFlow<List<Task>>
    val q4Tasks: StateFlow<List<Task>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)

        val allTasks = repository.allActiveTasks

        q1Tasks = allTasks.map { tasks ->
            tasks.filter { it.isUrgent && it.isImportant }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        q2Tasks = allTasks.map { tasks ->
            tasks.filter { !it.isUrgent && it.isImportant }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        q3Tasks = allTasks.map { tasks ->
            tasks.filter { it.isUrgent && !it.isImportant }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        q4Tasks = allTasks.map { tasks ->
            tasks.filter { !it.isUrgent && !it.isImportant }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun addTask(title: String, description: String, isUrgent: Boolean, isImportant: Boolean, dueDate: Long?) {
        viewModelScope.launch {
            repository.insert(Task(title = title, description = description, isUrgent = isUrgent, isImportant = isImportant, dueDate = dueDate))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun toggleCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateCompletionStatus(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return repository.getTaskById(taskId)
    }
}

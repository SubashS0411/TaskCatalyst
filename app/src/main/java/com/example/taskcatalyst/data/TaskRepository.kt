package com.example.taskcatalyst.data

import com.example.taskcatalyst.api.RetrofitClient
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allActiveTasks: Flow<List<Task>> = taskDao.getAllActiveTasks()
    private val apiService = RetrofitClient.instance

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
        // Optional: Sync to backend
        // try { apiService.createTask(task) } catch (e: Exception) { /* handle error */ }
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun updateCompletionStatus(taskId: Int, isCompleted: Boolean) {
        taskDao.updateCompletionStatus(taskId, isCompleted)
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }
}

package com.example.taskcatalyst.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allActiveTasks: Flow<List<Task>> = taskDao.getAllActiveTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
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

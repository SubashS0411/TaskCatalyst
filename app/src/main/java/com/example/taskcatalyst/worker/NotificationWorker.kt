package com.example.taskcatalyst.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskcatalyst.R
import com.example.taskcatalyst.data.AppDatabase
import kotlinx.coroutines.flow.first

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val taskDao = database.taskDao()

        val now = System.currentTimeMillis()
        val fifteenMinutesLater = now + 15 * 60 * 1000
        
        val upcomingTasks = taskDao.getTasksDueBetween(now, fifteenMinutesLater)
        
        if (upcomingTasks.isNotEmpty()) {
            val taskNames = upcomingTasks.joinToString { it.title }
            showNotification("Upcoming Tasks", "Due soon: $taskNames")
        }

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Task Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}

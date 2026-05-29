package com.example.taskcatalyst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskcatalyst.ui.TaskViewModel
import com.example.taskcatalyst.ui.screens.AddTaskScreen
import com.example.taskcatalyst.ui.screens.DashboardScreen
import com.example.taskcatalyst.ui.screens.FocusScreen
import com.example.taskcatalyst.ui.theme.TaskCatalystTheme
import com.example.taskcatalyst.worker.NotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupNotificationWorker()

        setContent {
            TaskCatalystTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskCatalystApp()
                }
            }
        }
    }

    private fun setupNotificationWorker() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "TaskNotificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun TaskCatalystApp() {
    val navController = rememberNavController()
    val viewModel: TaskViewModel = viewModel()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onAddTaskClick = { navController.navigate("add_task") },
                onTaskClick = { /* Handle edit if needed */ },
                onStartFocus = { task ->
                    navController.navigate("focus/${task.id}")
                }
            )
        }
        composable("add_task") {
            AddTaskScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "focus/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            var task by remember { mutableStateOf<com.example.taskcatalyst.data.Task?>(null) }
            
            LaunchedEffect(taskId) {
                task = viewModel.getTaskById(taskId)
            }
            
            task?.let { currentTask ->
                FocusScreen(
                    task = currentTask,
                    onClose = { navController.popBackStack() },
                    onComplete = {
                        viewModel.toggleCompletion(currentTask)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

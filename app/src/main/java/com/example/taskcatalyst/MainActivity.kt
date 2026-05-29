package com.example.taskcatalyst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskcatalyst.ui.TaskViewModel
import com.example.taskcatalyst.ui.screens.AddTaskScreen
import com.example.taskcatalyst.ui.screens.DashboardScreen
import com.example.taskcatalyst.ui.screens.FocusScreen
import com.example.taskcatalyst.ui.theme.TaskCatalystTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            val taskId = backStackEntry.arguments?.getInt("taskId")
            // In a real app, we'd fetch the task by ID from the VM
            // For simplicity, we'll find it in the current lists or pass it differently
            // Here we'll just use a placeholder or handle the logic to find the task
            val task = viewModel.q1Tasks.value.find { it.id == taskId }
                ?: viewModel.q2Tasks.value.find { it.id == taskId }
            
            if (task != null) {
                FocusScreen(
                    task = task,
                    onClose = { navController.popBackStack() },
                    onComplete = {
                        viewModel.toggleCompletion(task)
                        navController.popBackStack()
                    }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}

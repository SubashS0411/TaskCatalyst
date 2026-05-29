package com.example.taskcatalyst.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskcatalyst.data.Task
import com.example.taskcatalyst.ui.TaskViewModel
import com.example.taskcatalyst.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TaskViewModel,
    onAddTaskClick: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onStartFocus: (Task) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("TaskCatalyst") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Quadrant(
                    title = "Do First (Q1)",
                    tasksState = viewModel.q1Tasks,
                    color = Q1Color,
                    modifier = Modifier.weight(1f),
                    onTaskClick = onTaskClick,
                    onToggleComplete = { viewModel.toggleCompletion(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onStartFocus = onStartFocus
                )
                Quadrant(
                    title = "Schedule (Q2)",
                    tasksState = viewModel.q2Tasks,
                    color = Q2Color,
                    modifier = Modifier.weight(1f),
                    onTaskClick = onTaskClick,
                    onToggleComplete = { viewModel.toggleCompletion(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onStartFocus = onStartFocus
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                Quadrant(
                    title = "Minimize (Q3)",
                    tasksState = viewModel.q3Tasks,
                    color = Q3Color,
                    modifier = Modifier.weight(1f),
                    onTaskClick = onTaskClick,
                    onToggleComplete = { viewModel.toggleCompletion(it) },
                    onDeleteTask = { viewModel.deleteTask(it) }
                )
                Quadrant(
                    title = "Eliminate (Q4)",
                    tasksState = viewModel.q4Tasks,
                    color = Q4Color,
                    modifier = Modifier.weight(1f),
                    onTaskClick = onTaskClick,
                    onToggleComplete = { viewModel.toggleCompletion(it) },
                    onDeleteTask = { viewModel.deleteTask(it) }
                )
            }
        }
    }
}

@Composable
fun Quadrant(
    title: String,
    tasksState: kotlinx.coroutines.flow.StateFlow<List<Task>>,
    color: Color,
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onStartFocus: ((Task) -> Unit)? = null
) {
    val tasks by tasksState.collectAsState()

    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onClick = { onTaskClick(task) },
                            onToggleComplete = { onToggleComplete(task) },
                            onDelete = { onDeleteTask(task) },
                            onStartFocus = onStartFocus?.let { { it(task) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onStartFocus: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (task.description.isNotEmpty()) {
                    Text(text = task.description, fontSize = 12.sp, maxLines = 1)
                }
            }
            if (onStartFocus != null) {
                IconButton(onClick = onStartFocus) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Focus", tint = Color.Blue)
                }
            }
            IconButton(onClick = onToggleComplete) {
                Icon(Icons.Default.Check, contentDescription = "Complete", tint = Color.Green)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

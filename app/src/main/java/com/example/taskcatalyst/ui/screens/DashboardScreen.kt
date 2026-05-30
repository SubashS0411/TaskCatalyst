package com.example.taskcatalyst.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskcatalyst.data.Task
import com.example.taskcatalyst.ui.TaskViewModel
import com.example.taskcatalyst.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    q1Tasks: kotlinx.coroutines.flow.StateFlow<List<Task>>,
    q2Tasks: kotlinx.coroutines.flow.StateFlow<List<Task>>,
    q3Tasks: kotlinx.coroutines.flow.StateFlow<List<Task>>,
    q4Tasks: kotlinx.coroutines.flow.StateFlow<List<Task>>,
    onAddTaskClick: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onStartFocus: (Task) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "TaskCatalyst", 
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(36.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(8.dp)
        ) {
            Row(modifier = Modifier.weight(1f)) {
                QuadrantCard(
                    title = "DO FIRST",
                    subtitle = "Urgent & Important",
                    icon = Icons.Default.PriorityHigh,
                    tasksState = q1Tasks,
                    headerColor = Q1Color,
                    backgroundColor = Q1Background,
                    modifier = Modifier.weight(1f),
                    onTaskClick = onTaskClick,
                    onToggleComplete = onToggleComplete,
                    onDeleteTask = onDeleteTask,
                    onStartFocus = onStartFocus
                )
                QuadrantCard(
                    title = "SCHEDULE",
                    subtitle = "Important, Not Urgent",
                    icon = Icons.Default.Event,
                    tasksState = q2Tasks,
                    headerColor = Q2Color,
                    backgroundColor = Q2Background,
                    modifier = Modifier.weight(1f),
                    onTaskClick = onTaskClick,
                    onToggleComplete = onToggleComplete,
                    onDeleteTask = onDeleteTask,
                    onStartFocus = onStartFocus
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                QuadrantCard(
                    title = "DELEGATE",
                    subtitle = "Urgent, Not Important",
                    icon = Icons.Default.People,
                    tasksState = q3Tasks,
                    headerColor = Q3Color,
                    backgroundColor = Q3Background,
                    modifier = Modifier.weight(1f),
                    onTaskClick = onTaskClick,
                    onToggleComplete = onToggleComplete,
                    onDeleteTask = onDeleteTask
                )
                QuadrantCard(
                    title = "ELIMINATE",
                    subtitle = "Neither",
                    icon = Icons.Default.DeleteSweep,
                    tasksState = q4Tasks,
                    headerColor = Q4Color,
                    backgroundColor = Q4Background,
                    modifier = Modifier.weight(1f),
                    onTaskClick = onTaskClick,
                    onToggleComplete = onToggleComplete,
                    onDeleteTask = onDeleteTask
                )
            }
        }
    }
}

@Composable
fun QuadrantCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tasksState: kotlinx.coroutines.flow.StateFlow<List<Task>>,
    headerColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onStartFocus: ((Task) -> Unit)? = null
) {
    val tasks by tasksState.collectAsState()

    Card(
        modifier = modifier.padding(6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, headerColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }
            
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(tasks) { task ->
                        EnhancedTaskItem(
                            task = task,
                            accentColor = headerColor,
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
fun EnhancedTaskItem(
    task: Task,
    accentColor: Color,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onStartFocus: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 24.dp)
                    .background(accentColor, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Row {
                if (onStartFocus != null) {
                    IconButton(onClick = onStartFocus, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Focus", tint = accentColor, modifier = Modifier.size(16.dp))
                    }
                }
                IconButton(onClick = onToggleComplete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Complete", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE57373), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val dummyTasks = kotlinx.coroutines.flow.MutableStateFlow(
        listOf(
            Task(1, "Important Task", "Details", true, true),
            Task(2, "Urgent Task", "Details", true, false)
        )
    )
    MaterialTheme {
        DashboardScreen(
            q1Tasks = dummyTasks,
            q2Tasks = dummyTasks,
            q3Tasks = dummyTasks,
            q4Tasks = dummyTasks,
            onAddTaskClick = {},
            onTaskClick = {},
            onToggleComplete = {},
            onDeleteTask = {},
            onStartFocus = {}
        )
    }
}

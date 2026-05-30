package com.example.taskcatalyst.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskcatalyst.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    taskId: Int? = null,
    getTaskById: (suspend (Int) -> com.example.taskcatalyst.data.Task?)? = null,
    onAddTask: (String, String, Boolean, Boolean, Long?) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(false) }
    var isImportant by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (taskId != null && !isInitialized && getTaskById != null) {
            val task = getTaskById(taskId)
            task?.let {
                title = it.title
                description = it.description
                isUrgent = it.isUrgent
                isImportant = it.isImportant
                dueDate = it.dueDate
            }
            isInitialized = true
        }
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            dueDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (taskId == null) "Create New Task" else "Edit Task", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("What needs to be done?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Details (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            // Due Date Section
            Surface(
                onClick = { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Due Date", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = dueDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) } ?: "Set a deadline (optional)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (dueDate == null) Color.Gray else MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (dueDate != null) {
                        IconButton(onClick = { dueDate = null }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear date", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Text(
                "Priority Matrix",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrioritySwitch(
                        label = "Is it Urgent?",
                        subtitle = "Requires immediate attention",
                        checked = isUrgent,
                        onCheckedChange = { isUrgent = it },
                        color = Color(0xFFFF8A80)
                    )
                    
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    PrioritySwitch(
                        label = "Is it Important?",
                        subtitle = "Contributes to long-term goals",
                        checked = isImportant,
                        onCheckedChange = { isImportant = it },
                        color = Color(0xFF81C784)
                    )
                }
            }

            val quadrantText = when {
                isUrgent && isImportant -> "Quadrant 1: Do It Now"
                !isUrgent && isImportant -> "Quadrant 2: Schedule It"
                isUrgent && !isImportant -> "Quadrant 3: Delegate It"
                else -> "Quadrant 4: Eliminate It"
            }

            val quadrantColor = when {
                isUrgent && isImportant -> Q1Color
                !isUrgent && isImportant -> Q2Color
                isUrgent && !isImportant -> Q3Color
                else -> Q4Color
            }

            Surface(
                color = quadrantColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "This task will be placed in $quadrantText",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddTask(title, description, isUrgent, isImportant, dueDate)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (taskId == null) "Create Task" else "Update Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTaskPreview() {
    TaskCatalystTheme {
        AddTaskScreen(onAddTask = { _, _, _, _, _ -> }, onBack = {})
    }
}

@Composable
fun PrioritySwitch(
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = color,
                checkedTrackColor = color.copy(alpha = 0.5f)
            )
        )
    }
}

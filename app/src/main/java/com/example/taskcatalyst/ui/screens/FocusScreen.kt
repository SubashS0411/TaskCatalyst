package com.example.taskcatalyst.ui.screens

import android.media.RingtoneManager
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskcatalyst.data.Task
import com.example.taskcatalyst.ui.theme.TaskCatalystTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    task: Task,
    onClose: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val focusTime = 25 * 60
    val breakTime = 5 * 60
    var isBreak by remember { mutableStateOf(false) }
    var totalTime by remember { mutableIntStateOf(focusTime) }
    var timeLeft by remember { mutableIntStateOf(totalTime) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning, isBreak) {
        while (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        if (timeLeft == 0) {
            isRunning = false
            playAlarm(context)
            if (!isBreak) {
                // Focus ended, start break automatically or wait for user?
                // PRD says "5-minute break logic after timer completion"
                // Let's toggle to break mode
                isBreak = true
                totalTime = breakTime
                timeLeft = breakTime
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Focus Timer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                color = if (isBreak) Color(0xFF81C784).copy(alpha = 0.3f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = CircleShape
            ) {
                Text(
                    text = if (isBreak) "Break Time" else "Q${getQuadrantNumber(task)}: ${task.title}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    color = if (isBreak) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                )
            }
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(280.dp)
            ) {
                val progress = timeLeft.toFloat() / totalTime
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    label = "TimerProgress"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        style = Stroke(width = 12.dp.toPx())
                    )
                    drawArc(
                        color = if (isBreak) Color(0xFF81C784) else Color(0xFF4CAF50),
                        startAngle = -90f,
                        sweepAngle = 360 * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "REMAINING",
                        letterSpacing = 2.sp,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = { 
                        if (isBreak) {
                            isBreak = false
                            totalTime = focusTime
                            timeLeft = focusTime
                        } else {
                            timeLeft = totalTime
                        }
                        isRunning = false
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(32.dp))
                }

                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier
                        .width(140.dp)
                        .height(64.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
                        contentColor = if (isRunning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (isRunning) "PAUSE" else "RESUME",
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                OutlinedIconButton(
                    onClick = onClose,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Abort", modifier = Modifier.size(32.dp))
                }
            }

            AnimatedVisibility(visible = timeLeft == 0 || isBreak) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("COMPLETED TASK", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FocusScreenPreview() {
    TaskCatalystTheme {
        FocusScreen(
            task = Task(
                id = 1,
                title = "Study for Exam",
                description = "Chapter 1-3",
                isUrgent = true,
                isImportant = true
            ),
            onClose = {},
            onComplete = {}
        )
    }
}

private fun getQuadrantNumber(task: Task): Int {
    return when {
        task.isUrgent && task.isImportant -> 1
        !task.isUrgent && task.isImportant -> 2
        task.isUrgent && !task.isImportant -> 3
        else -> 4
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

private fun playAlarm(context: android.content.Context) {
    try {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val r = RingtoneManager.getRingtone(context, notification)
        r.play()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

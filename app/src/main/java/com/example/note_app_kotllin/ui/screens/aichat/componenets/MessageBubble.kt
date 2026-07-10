package com.example.note_app_kotllin.ui.screens.aichat.componenets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.note_app_kotllin.core.constants.BorderRadiuses
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.core.constants.Spaces
import com.example.note_app_kotllin.core.enums.MessageType
import com.example.note_app_kotllin.core.enums.Purpose
import com.example.note_app_kotllin.core.extensions.toTimeLabel
import com.example.note_app_kotllin.domain.models.Task
import kotlinx.coroutines.delay

@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    text: String,
    tasks: List<Task> = emptyList(),
    createdAt: Long,
    type: MessageType
) {
    val isUser = type == MessageType.User

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (isUser) BorderRadiuses.Medium else 0.dp,
                    topEnd = if (isUser) 0.dp else BorderRadiuses.Medium,
                    bottomStart = BorderRadiuses.Medium,
                    bottomEnd = BorderRadiuses.Medium
                ),
                color = if (isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(modifier = Modifier.padding(Paddings.Small)) {
                    if (text.isNotBlank()) {
                        if (isUser) {
                            Text(
                                text = text,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            TypewriterText(
                                fullText = text,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if (tasks.isNotEmpty()) {
                        if (text.isNotBlank()) Spacer(Modifier.height(Spaces.Tiny))
                        TaskCountChips(tasks = tasks)
                    }
                }
            }
            Text(
                text = createdAt.toTimeLabel(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Paddings.ExtraMini, start = Paddings.Mini, end = Paddings.Mini)
            )
        }
    }
}

@Composable
private fun TypewriterText(
    fullText: String,
    color: Color,
    charDelayMillis: Long = 12L
) {
    var visibleCharCount by remember(fullText) { mutableStateOf(0) }

    LaunchedEffect(fullText) {
        visibleCharCount = 0
        fullText.forEachIndexed { index, _ ->
            visibleCharCount = index + 1
            delay(charDelayMillis)
        }
    }

    Text(
        text = fullText.take(visibleCharCount),
        color = color
    )
}

@Composable
private fun TaskCountChips(tasks: List<Task>) {
    val createdCount = tasks.count { it.purpose == Purpose.CreateNote || it.purpose == Purpose.CreateTodo }
    val updatedCount = tasks.count { it.purpose == Purpose.UpdateNote || it.purpose == Purpose.UpdateTodo }
    val deletedCount = tasks.count { it.purpose == Purpose.DeleteNote || it.purpose == Purpose.DeleteTodo }

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        if (createdCount > 0) CountChip("+$createdCount", MaterialTheme.colorScheme.primary)
        if (updatedCount > 0) CountChip("±$updatedCount", MaterialTheme.colorScheme.tertiary)
        if (deletedCount > 0) CountChip("-$deletedCount", MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun CountChip(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.14f)) {
        Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal =Paddings.ExtraSmall, vertical = Paddings.Mini))
    }
}
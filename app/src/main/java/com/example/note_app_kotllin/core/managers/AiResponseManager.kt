package com.example.note_app_kotllin.core.managers

import com.example.note_app_kotllin.core.enums.MessageType
import com.example.note_app_kotllin.core.enums.Purpose
import com.example.note_app_kotllin.core.exceptions.AiException
import com.example.note_app_kotllin.data.models.response.OpenAiResponse
import com.example.note_app_kotllin.domain.models.Message
import com.example.note_app_kotllin.domain.models.Note
import com.example.note_app_kotllin.domain.models.Task
import com.example.note_app_kotllin.domain.models.Todo
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiResponseManager @Inject constructor() {
    val TASK_PREFIXES = setOf(
        "CREATENOTE", "CREATETODO", "UPDATENOTE", "UPDATETODO", "DELETENOTE", "DELETETODO"
    )

    fun parse(response: OpenAiResponse): Message {
        val outputItem = response.output.lastOrNull { it.phase == "final_answer" }
            ?: throw AiException.EmptyResponse()

        val rawText = outputItem.content
            .firstOrNull { it.type == "output_text" }
            ?.text

        if (rawText.isNullOrBlank()) {
            throw AiException.EmptyResponse()
        }

        val (messageText, tasks) = parseContent(rawText)

        return Message(
            id = UUID.randomUUID().toString(),
            type = outputItem.role.toMessageType(),
            message = messageText,
            tasks = tasks,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun parseContent(rawText: String): Pair<String, List<Task>> {
        val tasks = mutableListOf<Task>()
        val messageLines = mutableListOf<String>()

        rawText.lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { line ->
                val prefix = line.substringBefore("|")
                when {
                    prefix in TASK_PREFIXES -> line.toTaskOrNull()?.let { tasks.add(it) }
                    prefix == "OUTOFSCOPE" -> messageLines.add(line.substringAfter("|").trim())
                    prefix == "CLARIFY" -> messageLines.add(line.substringAfter("|").trim())
                    else -> messageLines.add(line)
                }
            }

        return messageLines.joinToString("\n") to tasks
    }

    private fun String?.toMessageType(): MessageType = when (this) {
        "assistant" -> MessageType.Ai
        "user" -> MessageType.User
        "developer", "system" -> MessageType.System
        else -> MessageType.Ai
    }

    private fun String.toTaskOrNull(): Task? {
        val parts = this.split("|")
        return when (parts.getOrNull(0)) {
            "CREATENOTE" -> {
                val title = parts.getOrNull(1) ?: return null
                val content = parts.getOrNull(2).orEmpty()
                Task(
                    purpose = Purpose.CreateNote,
                    note = Note(id = "", title = title, content = content),
                    todo = null, noteId = null, todoId = null
                )
            }
            "CREATETODO" -> {
                val description = parts.getOrNull(1) ?: return null
                Task(
                    purpose = Purpose.CreateTodo,
                    note = null,
                    todo = Todo(id = "", description = description),
                    noteId = null, todoId = null
                )
            }
            "UPDATENOTE" -> {
                val id = parts.getOrNull(1) ?: return null
                val title = parts.getOrNull(2).orEmpty()
                val content = parts.getOrNull(3).orEmpty()
                Task(
                    purpose = Purpose.UpdateNote,
                    note = Note(id = id, title = title, content = content),
                    todo = null, noteId = id, todoId = null
                )
            }
            "UPDATETODO" -> {
                val id = parts.getOrNull(1) ?: return null
                val description = parts.getOrNull(2).orEmpty()
                val completed = parts.getOrNull(3)?.toBoolean() ?: false
                Task(
                    purpose = Purpose.UpdateTodo,
                    note = null,
                    todo = Todo(id = id, description = description, isCompleted = completed),
                    noteId = null, todoId = id
                )
            }
            "DELETENOTE" -> {
                val id = parts.getOrNull(1) ?: return null
                Task(purpose = Purpose.DeleteNote, note = null, todo = null, noteId = id, todoId = null)
            }
            "DELETETODO" -> {
                val id = parts.getOrNull(1) ?: return null
                Task(purpose = Purpose.DeleteTodo, note = null, todo = null, noteId = null, todoId = id)
            }
            else -> null
        }
    }
}
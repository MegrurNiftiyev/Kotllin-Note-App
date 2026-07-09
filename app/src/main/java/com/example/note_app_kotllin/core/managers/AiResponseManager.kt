package com.example.note_app_kotllin.core.managers
import com.example.note_app_kotllin.core.enums.Purpose
import com.example.note_app_kotllin.domain.models.Note
import com.example.note_app_kotllin.domain.models.Task
import com.example.note_app_kotllin.domain.models.Todo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiResponseManager @Inject constructor() {

    fun parse(rawText: String): List<Task> {
        return rawText.lineSequence()
            .filter { it.isNotBlank() }
            .mapNotNull { line -> line.trim().toTaskOrNull() }
            .toList()
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
                    todo = null,
                    noteId = null,
                    todoId = null
                )
            }
            "CREATETODO" -> {
                val description = parts.getOrNull(1) ?: return null
                Task(
                    purpose = Purpose.CreateTodo,
                    note = null,
                    todo = Todo(id = "", description = description, isCompleted = false),
                    noteId = null,
                    todoId = null
                )
            }
            "UPDATENOTE" -> {
                val id = parts.getOrNull(1) ?: return null
                val title = parts.getOrNull(2).orEmpty()
                val content = parts.getOrNull(3).orEmpty()
                Task(
                    purpose = Purpose.UpdateNote,
                    note = Note(id = id, title = title, content = content),
                    todo = null,
                    noteId = id,
                    todoId = null
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
                    noteId = null,
                    todoId = id
                )
            }
            "DELETENOTE" -> {
                val id = parts.getOrNull(1) ?: return null
                Task(
                    purpose = Purpose.DeleteNote,
                    note = null,
                    todo = null,
                    noteId = id,
                    todoId = null
                )
            }
            "DELETETODO" -> {
                val id = parts.getOrNull(1) ?: return null
                Task(
                    purpose = Purpose.DeleteTodo,
                    note = null,
                    todo = null,
                    noteId = null,
                    todoId = id
                )
            }
            else -> null
        }
    }
}
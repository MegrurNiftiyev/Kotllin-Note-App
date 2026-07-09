package com.example.note_app_kotllin.core.managers
import com.example.note_app_kotllin.core.enums.MessageType
import com.example.note_app_kotllin.core.extensions.toAiFormat
import com.example.note_app_kotllin.data.models.request.InputItem
import com.example.note_app_kotllin.domain.models.Message
import com.example.note_app_kotllin.domain.models.Note
import com.example.note_app_kotllin.domain.models.Todo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiInputManager @Inject constructor() {

    fun build(
        notes: List<Note>,
        todos: List<Todo>,
        history: List<Message>,
        newUserMessage: String
    ): List<InputItem> {
        val contextBlock = buildContextBlock(notes, todos)

        return buildList {
            add(InputItem(role = "developer", content = contextBlock))
            history.forEach { msg -> add(InputItem(role = msg.type.toApiRole(), content = msg.message)) }
            add(InputItem(role = "user", content = newUserMessage))
        }
    }

    private fun buildContextBlock(notes: List<Note>, todos: List<Todo>): String = buildString {
        appendLine("Mövcud qeyd və tapşırıqlar:")
        notes.forEach { appendLine(it.toAiFormat()) }
        todos.forEach { appendLine(it.toAiFormat()) }
    }

    private fun MessageType.toApiRole(): String = when (this) {
        MessageType.User -> "user"
        MessageType.Ai -> "assistant"
        MessageType.System -> "developer"
    }
}
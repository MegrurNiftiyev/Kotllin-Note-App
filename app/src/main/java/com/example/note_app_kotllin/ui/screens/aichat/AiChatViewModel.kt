package com.example.note_app_kotllin.ui.screens.aichat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.enums.MessageType
import com.example.note_app_kotllin.core.enums.Purpose
import com.example.note_app_kotllin.core.exceptions.AiException
import com.example.note_app_kotllin.core.exceptions.NetworkException
import com.example.note_app_kotllin.core.util.UiText
import com.example.note_app_kotllin.domain.models.Message
import com.example.note_app_kotllin.domain.models.Task
import com.example.note_app_kotllin.domain.repositories.IAiRepository
import com.example.note_app_kotllin.domain.repositories.INotesRepository
import com.example.note_app_kotllin.domain.repositories.ITodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val HISTORY_LIMIT = 10

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val aiRepository: IAiRepository,
    private val notesRepository: INotesRepository,
    private val todoRepository: ITodoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatState())
    val state = _state.asStateFlow()

    private val notes = notesRepository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val todos = todoRepository.getAllTodos()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onInputChanged(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun onSendClicked() {
        val userMessageText = _state.value.inputText.trim()
        if (userMessageText.isBlank() || _state.value.isLoading) return

        val recentHistory = _state.value.messages.takeLast(HISTORY_LIMIT)

        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            type = MessageType.User,
            message = userMessageText,
            tasks = emptyList(),
            createdAt = System.currentTimeMillis()
        )

        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch(IO) {
            aiRepository.sendMessage(
                userMessage = userMessageText,
                notes = notes.value,
                todos = todos.value,
                history = recentHistory
            ).onSuccess { aiMessage ->
                executeTasks(aiMessage.tasks)
                _state.update { s -> s.copy(messages = s.messages + aiMessage, isLoading = false) }
            }.onFailure { e ->
                val errorText = e.toUiText().asString(context)
                val errorMessage = Message(
                    id = UUID.randomUUID().toString(),
                    type = MessageType.Ai,
                    message = errorText,
                    tasks = emptyList(),
                    createdAt = System.currentTimeMillis()
                )
                _state.update { s ->
                    s.copy(
                        messages = s.messages + errorMessage,
                        isLoading = false,
                        error = errorText
                    )
                }
            }
        }
    }

    private fun Throwable.toUiText(): UiText = when (this) {
        is AiException.ValidationError -> UiText.StringResource(R.string.error_ai_validation)
        is AiException.Unauthorized -> UiText.StringResource(R.string.error_ai_unauthorized)
        is AiException.RateLimited -> UiText.StringResource(R.string.error_ai_rate_limited)
        is AiException.ServerError -> UiText.StringResource(R.string.error_ai_server_error)
        is AiException.EmptyResponse -> UiText.StringResource(R.string.error_ai_empty_response)
        is NetworkException.NoInternet -> UiText.StringResource(R.string.error_no_internet)
        else -> UiText.StringResource(R.string.error_unknown)
    }

    private suspend fun executeTasks(tasks: List<Task>) {
        tasks.forEach { task ->
            try {
                when (task.purpose) {
                    Purpose.CreateNote -> {
                        val note = task.note ?: return@forEach
                        notesRepository.createNote(title = note.title, content = note.content)
                    }
                    Purpose.CreateTodo -> {
                        val todo = task.todo ?: return@forEach
                        todoRepository.createTodo(description = todo.description, isCompleted = false)
                    }
                    Purpose.UpdateNote -> {
                        val note = task.note ?: return@forEach
                        val id = task.noteId ?: return@forEach
                        notesRepository.updateNote(id = id, title = note.title, content = note.content)
                    }
                    Purpose.UpdateTodo -> {
                        val todo = task.todo ?: return@forEach
                        val id = task.todoId ?: return@forEach
                        todoRepository.updateTodo(id = id, description = todo.description, isCompleted = todo.isCompleted)
                    }
                    Purpose.DeleteNote -> {
                        val id = task.noteId ?: return@forEach
                        notesRepository.deleteNote(id)
                    }
                    Purpose.DeleteTodo -> {
                        val id = task.todoId ?: return@forEach
                        todoRepository.deleteTodo(id)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}
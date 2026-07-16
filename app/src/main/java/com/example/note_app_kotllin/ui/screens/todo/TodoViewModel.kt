package com.example.note_app_kotllin.ui.screens.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note_app_kotllin.core.managers.AppNetworkManager
import com.example.note_app_kotllin.domain.models.Todo
import com.example.note_app_kotllin.domain.repositories.ITodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: ITodoRepository,
    private val networkManager: AppNetworkManager,
) : ViewModel() {

    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()

    private val changedTodos = mutableMapOf<String, Todo>()
    private val deletedTodos = mutableSetOf<String>()

    init {
        listenLocalTodos()
        syncTodos()
        listenConnectivity()
    }

    private fun listenConnectivity() {
        viewModelScope.launch(IO) {
            networkManager.isConnected
                .drop(1)
                .filter { it }
                .collect { syncTodos() }
        }
    }

    private fun listenLocalTodos() {
        viewModelScope.launch(IO) {
            todoRepository.getAllTodos().collect { todos ->
                _state.update { it.copy(todos = todos) }
            }
        }
    }

    private fun syncTodos() {
        viewModelScope.launch(IO) {
            _state.update { it.copy(isLoading = true) }
            todoRepository.syncTodos()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun createTodo() {
        if (_state.value.draftTodo != null) return

        val now = System.currentTimeMillis()
        val draft = Todo(
            id = "",
            description = "",
            isCompleted = false,
            createdAt = now,
            updatedAt = now,
            isSynced = false
        )
        _state.update { it.copy(draftTodo = draft, focusedTodoId = "", focusedText = "") }
    }

    fun onFocusGained(id: String, currentText: String) {
        _state.update { it.copy(focusedTodoId = id, focusedText = currentText) }
    }

    fun onTextChange(text: String) {
        _state.update { it.copy(focusedText = text) }
    }

    fun handleFocusLost(id: String, finalText: String, isCompleted: Boolean) {
        if (id.isEmpty()) {
            val trimmed = finalText.trim()
            _state.update { it.copy(draftTodo = null, focusedTodoId = null, focusedText = "") }
            if (trimmed.isNotEmpty()) {
                viewModelScope.launch(IO) { todoRepository.createTodo(trimmed, false) }
            }
            return
        }

        val currentTodo = _state.value.todos.find { it.id == id }
        val trimmed = finalText.trim()
        val unchanged = currentTodo != null && currentTodo.description == trimmed

        if (!unchanged) {
            if (trimmed.isEmpty()) {
                deleteTodo(id)
            } else if (currentTodo != null) {
                val updated = currentTodo.copy(description = trimmed, isCompleted = isCompleted)
                changedTodos[id] = updated
                _state.update { st -> st.copy(todos = st.todos.map { if (it.id == id) updated else it }) }

                viewModelScope.launch(IO) {
                    todoRepository.updateTodo(id, trimmed, isCompleted)
                }
            }
        }
        _state.update { it.copy(focusedTodoId = null, focusedText = "") }
    }

    fun updateTodoCompletion(id: String, isCompleted: Boolean) {
        val currentTodo = _state.value.todos.find { it.id == id } ?: return
        val updated = currentTodo.copy(isCompleted = isCompleted)

        changedTodos[id] = updated
        _state.update { st -> st.copy(todos = st.todos.map { if (it.id == id) updated else it }) }

        viewModelScope.launch(IO) {
            todoRepository.updateTodo(id, updated.description, isCompleted)
        }
    }

    fun deleteTodo(id: String) {
        changedTodos.remove(id)
        deletedTodos.add(id)

        if (_state.value.focusedTodoId == id) {
            _state.update { it.copy(focusedTodoId = null, focusedText = "") }
        }
        _state.update { it.copy(todos = it.todos.filterNot { t -> t.id == id }) }

        viewModelScope.launch(IO) {
            todoRepository.deleteTodo(id)
        }
    }

    fun handleScreenExit() {
        val current = _state.value
        val focusedId = current.focusedTodoId

        if (focusedId == "") {
            val trimmed = current.focusedText.trim()
            _state.update { it.copy(draftTodo = null, focusedTodoId = null, focusedText = "") }
            if (trimmed.isNotEmpty()) {
                viewModelScope.launch(IO) { todoRepository.createTodo(trimmed, false) }
            }
        } else if (!focusedId.isNullOrEmpty()) {
            handleFocusLost(
                id = focusedId,
                finalText = current.focusedText,
                isCompleted = current.todos.find { it.id == focusedId }?.isCompleted ?: false
            )
        }

        val deletions = deletedTodos.toSet()
        deletedTodos.clear()
        deletions.forEach { id ->
            viewModelScope.launch(IO) {
                todoRepository.deleteTodo(id)
            }
        }

        val changes = changedTodos.toMap()
        changedTodos.clear()
        changes.forEach { (id, todo) ->
            viewModelScope.launch(IO) {
                todoRepository.updateTodo(id, todo.description, todo.isCompleted)
            }
        }
    }
}
package com.example.note_app_kotllin.domain.repositories

import com.example.note_app_kotllin.domain.models.Note
import com.example.note_app_kotllin.domain.models.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    fun getAllTodos(): Flow<List<Todo>>
    suspend fun createTodo(description: String, isCompleted: Boolean): Todo
    suspend fun updateTodo(id: String, description: String, isCompleted: Boolean)
    suspend fun deleteTodo(id: String)
    suspend fun syncTodos(): Result<Unit>
}
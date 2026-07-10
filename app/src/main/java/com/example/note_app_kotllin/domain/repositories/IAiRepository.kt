package com.example.note_app_kotllin.domain.repositories

import com.example.note_app_kotllin.domain.models.Message
import com.example.note_app_kotllin.domain.models.Note
import com.example.note_app_kotllin.domain.models.Task
import com.example.note_app_kotllin.domain.models.Todo


interface IAiRepository {
    suspend fun sendMessage(
        userMessage: String,
        notes: List<Note>,
        todos: List<Todo>,
        history: List<Message>
    ): Result<Message>
}
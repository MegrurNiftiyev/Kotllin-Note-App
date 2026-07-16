package com.example.note_app_kotllin.domain.models

import com.example.note_app_kotllin.core.enums.MessageType

data class Message(
    val id: String,
    val type: MessageType,
    val message: String,
    val tasks: List<Task>,
    val createdAt: Long
)
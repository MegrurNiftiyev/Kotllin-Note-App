package com.example.note_app_kotllin.ui.screens.ai_chat

import com.example.note_app_kotllin.domain.models.Message

data class AiChatState(
    val isLoading: Boolean = false,
    val inputText: String = "",
    val messages: List<Message> = emptyList(),
    val messageAnimationCompletedIds: Set<String> = emptySet()

)
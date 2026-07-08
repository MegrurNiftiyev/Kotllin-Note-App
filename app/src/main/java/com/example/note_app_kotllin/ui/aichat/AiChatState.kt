package com.example.note_app_kotllin.ui.aichat

import com.example.note_app_kotllin.domain.models.AiMessages
import com.example.note_app_kotllin.domain.models.UserMessage

data class AiChatState (
   val isLoading: Boolean = false,
   val userMessages:List<UserMessage> =emptyList(),
   val aiMessages:List<AiMessages> =emptyList(),

)
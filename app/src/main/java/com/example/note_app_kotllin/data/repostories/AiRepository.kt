package com.example.note_app_kotllin.data.repostories

import com.example.note_app_kotllin.core.constants.AppConfigs
import com.example.note_app_kotllin.core.managers.AiInputManager
import com.example.note_app_kotllin.core.managers.AiResponseManager
import com.example.note_app_kotllin.core.managers.PromptManager
import com.example.note_app_kotllin.data.datasoruces.remote.datasources.AiRemoteDataSource
import com.example.note_app_kotllin.data.models.request.OpenAiRequest
import com.example.note_app_kotllin.domain.models.Message
import com.example.note_app_kotllin.domain.models.Note
import com.example.note_app_kotllin.domain.models.Todo
import com.example.note_app_kotllin.domain.repositories.IAiRepository
import javax.inject.Inject

class AiRepository @Inject constructor(
    private val aiRemoteDataSource: AiRemoteDataSource,
    private val aiInputManager: AiInputManager,
    private val aiResponseManager: AiResponseManager,
    private val promptManager: PromptManager
) : IAiRepository {

    override suspend fun sendMessage(
        userMessage: String,
        notes: List<Note>,
        todos: List<Todo>,
        history: List<Message>
    ): Result<Message> {
        return try {
            val request = OpenAiRequest(
                instructions = promptManager.getSystemInstructions(),
                input = aiInputManager.build(notes, todos, history, userMessage),
                model = AppConfigs.OPEN_AI_MODEL
            )

            val response = aiRemoteDataSource.sendChatRequest(request)
            val message = aiResponseManager.parse(response)

            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
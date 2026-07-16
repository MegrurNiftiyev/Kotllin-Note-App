package com.example.note_app_kotllin.data.datasoruces.remote.services

import com.example.note_app_kotllin.data.models.request.OpenAiRequest
import com.example.note_app_kotllin.data.models.response.OpenAiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiApiService {

    @POST("v1/responses")
    suspend fun generateResponse(
        @Body request: OpenAiRequest
    ): OpenAiResponse

}
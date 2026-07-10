package com.example.note_app_kotllin.data.datasoruces.remote.datasources

import com.example.note_app_kotllin.core.exceptions.AiException
import com.example.note_app_kotllin.core.exceptions.NetworkException
import com.example.note_app_kotllin.data.datasoruces.remote.services.OpenAiApiService
import com.example.note_app_kotllin.data.models.request.OpenAiRequest
import com.example.note_app_kotllin.data.models.response.OpenAiResponse
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AiRemoteDataSource @Inject constructor(
    private val openAiApiService: OpenAiApiService
) {

    suspend fun sendChatRequest(request: OpenAiRequest): OpenAiResponse {
        return try {
            openAiApiService.generateResponse(request)
        } catch (e: HttpException) {
            throw when (e.code()) {
                400 -> AiException.ValidationError()
                401 -> AiException.Unauthorized()
                429 -> AiException.RateLimited()
                500 -> AiException.ServerError()
                else -> AiException.Unknown()
            }
        } catch (e: IOException) {
            throw NetworkException.NoInternet()
        } catch (e: Exception) {
            throw AiException.Unknown()
        }
    }
}
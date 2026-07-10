package com.example.note_app_kotllin.core.exceptions

sealed class AiException(override val message: String) : Exception(message) {
    data class ValidationError(override val message: String = "Invalid request to AI service") : AiException(message)
    data class Unauthorized(override val message: String = "Missing or invalid API key") : AiException(message)
    data class RateLimited(override val message: String = "Too many requests, try again later") : AiException(message)
    data class ServerError(override val message: String = "Something went wrong on AI server") : AiException(message)
    data class EmptyResponse(override val message: String = "AI returned an empty response") : AiException(message)
    data class Unknown(override val message: String = "An unexpected error occurred") : AiException(message)
}
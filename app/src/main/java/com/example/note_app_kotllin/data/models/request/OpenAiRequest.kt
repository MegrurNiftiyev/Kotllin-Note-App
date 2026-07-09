package com.example.note_app_kotllin.data.models.request
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiRequest(
    val model: String = "gpt-5.4-mini",
    val instructions: String,
    val input: List<InputItem>,
    val temperature: Double = 1.0,
    @SerialName("max_output_tokens")
    val maxOutputTokens: Int? = null
){

}

@Serializable
data class InputItem(
    val role: String,
    val content: String
)
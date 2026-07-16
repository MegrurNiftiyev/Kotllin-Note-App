package com.example.note_app_kotllin.data.models.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class OpenAiResponse(
    val id: String,
    val status: String,
    val output: List<OutputItem>,
    val usage: Usage? = null
) {
    @Serializable
    data class OutputItem(
        val id: String,
        val type: String,
        val status: String,
        val content: List<ContentItem> = emptyList(),
        val phase: String? = null,
        val role: String? = null
    )

    @Serializable
    data class ContentItem(
        val type: String,
        val text: String? = null
    )

    @Serializable
    data class Usage(
        @SerialName("input_tokens")
        val inputTokens: Int,
        @SerialName("output_tokens")
        val outputTokens: Int,
        @SerialName("total_tokens")
        val totalTokens: Int
    )
}
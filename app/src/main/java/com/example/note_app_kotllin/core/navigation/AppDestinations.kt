package com.example.note_app_kotllin.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Splash

@Serializable
object Register

@Serializable
object Login


@Serializable
object Notes

@Serializable
object Settings

@Serializable
data class NoteDetail(
    val id: String,
    val title: String,
    val subtitle: String,
    val isSynced: Boolean
)


@Serializable
object Todos

@Serializable
object AiChat
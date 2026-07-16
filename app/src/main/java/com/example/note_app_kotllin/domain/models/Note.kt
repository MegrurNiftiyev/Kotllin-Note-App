package com.example.note_app_kotllin.domain.models

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long=System.currentTimeMillis(),
    val updatedAt: Long=System.currentTimeMillis(),
    val isSynced: Boolean=false

)
package com.example.note_app_kotllin.domain.models

data class AiMessages (
    val id: String,
    val message:String,
    val tasks:List<Task>,
    val createdAt:Long
)
package com.example.note_app_kotllin.domain.models

import com.example.note_app_kotllin.core.enums.Purpose

data class Task(
    val purpose: Purpose,
    val note: Note?,
    val todo: Todo?,
    val noteId: String?,
    val todoId: String?,
    )
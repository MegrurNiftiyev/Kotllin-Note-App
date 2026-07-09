package com.example.note_app_kotllin.core.extensions

import com.example.note_app_kotllin.domain.models.Note
import com.example.note_app_kotllin.domain.models.Todo

fun Note.toAiFormat(): String = "NOTE|$id|$title"

fun Todo.toAiFormat(): String = "TODO|$id|$description|$isCompleted"
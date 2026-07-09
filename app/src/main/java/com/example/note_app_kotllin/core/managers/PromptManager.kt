package com.example.note_app_kotllin.core.managers

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val systemInstructions: String by lazy {
        context.assets.open("prompts/system_instructions.txt")
            .bufferedReader()
            .use { it.readText() }
    }

    fun getSystemInstructions(): String = systemInstructions
}
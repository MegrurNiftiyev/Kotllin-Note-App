package com.example.note_app_kotllin.ui.screens.auth.register

import com.example.note_app_kotllin.core.util.UiText

data class RegisterState (
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: UiText? = null,
    val userNameError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val confirmPasswordError: UiText? = null,

)
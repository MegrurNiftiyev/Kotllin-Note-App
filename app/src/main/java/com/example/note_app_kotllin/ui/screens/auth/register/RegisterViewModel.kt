package com.example.note_app_kotllin.ui.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.exceptions.AuthException
import com.example.note_app_kotllin.core.exceptions.NetworkException
import com.example.note_app_kotllin.core.extensions.isValidEmail
import com.example.note_app_kotllin.core.extensions.isValidPassword
import com.example.note_app_kotllin.core.extensions.isValidUserName
import com.example.note_app_kotllin.core.util.UiText
import com.example.note_app_kotllin.domain.repositories.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: IAuthRepository
): ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun register(userName: String, email: String, password: String, confirmPassword: String) {
        if (!validate(userName, email, password, confirmPassword) || _state.value.isSuccess||_state.value.isLoading) return

        viewModelScope.launch(IO) {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.register(userName, email, password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    val error = when (e) {
                        is AuthException.EmailAlreadyExists -> UiText.StringResource(R.string.error_email_exists)
                        is AuthException.TooManyRequests -> UiText.StringResource(R.string.error_too_many_requests)
                        is NetworkException.NoInternet -> UiText.StringResource(R.string.error_no_internet)
                        else -> UiText.StringResource(R.string.error_unknown)
                    }
                    _state.update { it.copy(isLoading = false, errorMessage = error) }
                }
        }
    }

    private fun validate(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val userNameError = when {
            userName.isBlank() -> UiText.StringResource(R.string.error_empty_username)
            !userName.isValidUserName() -> UiText.StringResource(R.string.error_invalid_username)
            else -> null
        }

        val emailError = when {
            email.isBlank() -> UiText.StringResource(R.string.error_empty_email)
            !email.isValidEmail() -> UiText.StringResource(R.string.error_invalid_email)
            else -> null
        }

        val passwordError = when {
            password.isBlank() -> UiText.StringResource(R.string.error_empty_password)
            !password.isValidPassword() -> UiText.StringResource(R.string.error_invalid_password)
            else -> null
        }

        val confirmPasswordError = when {
            confirmPassword.isBlank() -> UiText.StringResource(R.string.error_empty_confirm_password)
            confirmPassword != password -> UiText.StringResource(R.string.error_passwords_not_match)
            else -> null
        }

        _state.update {
            it.copy(
                userNameError = userNameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        return listOf(userNameError, emailError, passwordError, confirmPasswordError)
            .all { it == null }
    }
}
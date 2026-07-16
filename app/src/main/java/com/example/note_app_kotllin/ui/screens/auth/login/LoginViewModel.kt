package com.example.note_app_kotllin.ui.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.exceptions.AuthException
import com.example.note_app_kotllin.core.exceptions.NetworkException
import com.example.note_app_kotllin.core.extensions.isValidEmail
import com.example.note_app_kotllin.core.extensions.isValidPassword
import com.example.note_app_kotllin.core.util.UiText
import com.example.note_app_kotllin.domain.repositories.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers.IO

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {


    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun login(email: String, password: String) {
        if (!validate(email, password)|| _state.value.isSuccess || _state.value.isLoading) return
        viewModelScope.launch(IO) {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.login(email, password).onSuccess {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                val error = when (e) {
                    is AuthException.InvalidCredentials -> UiText.StringResource(R.string.error_invalid_credentials)
                    is AuthException.TooManyRequests -> UiText.StringResource(R.string.error_too_many_requests)
                    is NetworkException.NoInternet -> UiText.StringResource(R.string.error_no_internet)
                    else -> UiText.StringResource(R.string.error_unknown)
                }
                _state.update {
                    it.copy(isLoading = false, errorMessage = error)
                }
            }
        }
    }


    private fun validate(
        email: String,
        password: String,
    ): Boolean {


        val emailError = when {
            email.isBlank() -> UiText.StringResource(R.string.error_empty_email)
            !email.isValidEmail() -> UiText.StringResource(R.string.error_invalid_email)
            else -> null
        }

        val passwordError = when {
            password.isBlank() -> UiText.StringResource(R.string.error_empty_password)
            !password.isValidPassword() -> UiText.StringResource(R.string.error_invalid_credentials)
            else -> null
        }



        _state.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
            )
        }

        return listOf(emailError, passwordError).all { it == null }
    }
}
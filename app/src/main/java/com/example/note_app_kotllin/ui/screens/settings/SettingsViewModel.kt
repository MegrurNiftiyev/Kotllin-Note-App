package com.example.note_app_kotllin.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note_app_kotllin.domain.repositories.IAuthRepository
import com.example.note_app_kotllin.domain.repositories.ISettingsRepository
import com.example.note_app_kotllin.domain.repositories.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository,
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()


    init {
        getThemeMode()
        getUser()
        syncUser()
    }
    fun getThemeMode(){
        viewModelScope.launch {
            settingsRepository.isDarkMode.collect { darkMode ->
                _state.update { it.copy(isDarkMode = darkMode) }
            }
        }
    }
    fun changeThemeMode() {
        viewModelScope.launch {
            val currentMode = _state.value.isDarkMode
            settingsRepository.changeTheme(!currentMode)
        }
    }

    fun openLanguageSheet() {
        _state.update { it.copy(isLanguageSheetOpen = true) }
    }

    fun closeLanguageSheet() {
        _state.update { it.copy(isLanguageSheetOpen = false) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun getUser() {
        viewModelScope.launch {
            val user = userRepository.getUser() ?: return@launch
            _state.update { it.copy(userName = user.name, userEmail = user.email) }
        }
    }

    fun syncUser() {
        viewModelScope.launch(IO) {
            userRepository.syncUser().onSuccess { getUser() }
        }
    }
}


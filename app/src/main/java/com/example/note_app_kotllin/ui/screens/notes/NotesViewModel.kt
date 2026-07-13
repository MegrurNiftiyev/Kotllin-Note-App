package com.example.note_app_kotllin.ui.screens.notes
import kotlinx.coroutines.Dispatchers.IO

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note_app_kotllin.core.managers.AppNetworkManager
import com.example.note_app_kotllin.domain.repositories.INotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: INotesRepository,
    private val networkManager: AppNetworkManager

) : ViewModel() {

    private val _state = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> = _state.asStateFlow()

    init {
        listenLocalNotes()
        syncNotes()
        listenConnectivity()
    }


    private fun listenConnectivity() {
        viewModelScope.launch(IO) {
            networkManager.isConnected
                .drop(1)
                .filter { it }
                .collect { syncNotes() }
        }
    }
    private fun listenLocalNotes() {
        viewModelScope.launch(IO) {
            notesRepository.getAllNotes().collect { notes ->
                _state.update { it.copy(notes = notes) }
            }
        }
    }

    private fun syncNotes() {
        viewModelScope.launch(IO) {
            _state.update { it.copy(isLoading = true) }
            notesRepository.syncNotes()
                .onSuccess { _state.update { it.copy(isLoading = false, errorMessage = null) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) } }
        }
    }

    fun refreshNotes() {
        viewModelScope.launch (IO){
            _state.update { it.copy(isRefreshing = true) }
            notesRepository.syncNotes()
                .onSuccess { _state.update { it.copy(isRefreshing = false, errorMessage = null) } }
                .onFailure { e -> _state.update { it.copy(isRefreshing = false, errorMessage = e.localizedMessage) } }
        }
    }
}
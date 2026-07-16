package com.example.note_app_kotllin.ui.screens.note_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note_app_kotllin.domain.repositories.INotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val notesRepository: INotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NoteDetailState())
    val state: StateFlow<NoteDetailState> = _state.asStateFlow()

    fun handleSaveOrDelete(
        id: String,
        initialTitle: String,
        initialContent: String,
        currentTitle: String,
        currentContent: String
    ) {
        val trimmedTitle = currentTitle.trim()
        val trimmedContent = currentContent.trim()

        if (trimmedTitle == initialTitle.trim() && trimmedContent == initialContent.trim()) {
            return
        }

        if (trimmedTitle.isEmpty() && trimmedContent.isEmpty()) {
            if (id.isNotEmpty()) {
                viewModelScope.launch {
                    withContext(NonCancellable) {
                        notesRepository.deleteNote(id)
                    }
                }
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            withContext(NonCancellable) {
                if (id.isEmpty()) {
                    notesRepository.createNote(trimmedTitle, trimmedContent)
                        .onSuccess { _state.update { it.copy(isSynced = true, isSaving = false) } }
                        .onFailure { _state.update { it.copy(isSynced = false, isSaving = false) } }
                } else {
                    notesRepository.updateNote(id, trimmedTitle, trimmedContent)
                        .onSuccess { _state.update { it.copy(isSynced = true, isSaving = false) } }
                        .onFailure { _state.update { it.copy(isSynced = false, isSaving = false) } }
                }
            }
        }
    }

    fun deleteNote(id: String) {
        if (id.isNotEmpty()) {
            viewModelScope.launch {
                withContext(NonCancellable) {
                    notesRepository.deleteNote(id)
                }
            }
        }
    }
}
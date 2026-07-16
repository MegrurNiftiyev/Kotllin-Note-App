package com.example.note_app_kotllin.data.repostories

import com.example.note_app_kotllin.core.extensions.addLocalBanner
import com.example.note_app_kotllin.core.extensions.isLocal
import com.example.note_app_kotllin.data.datasoruces.local.NoteLocalDataSource
import com.example.note_app_kotllin.data.datasoruces.local.room.entities.NoteEntity
import com.example.note_app_kotllin.data.datasoruces.remote.datasources.NoteRemoteDataSource
import com.example.note_app_kotllin.domain.models.Note
import com.example.note_app_kotllin.domain.repositories.INotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(
    private val noteRemoteDataSource: NoteRemoteDataSource,
    private val noteLocalDataSource: NoteLocalDataSource
) : INotesRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return noteLocalDataSource.getAllNotes()
            .map { entities -> entities.map { it.toDomainNote() } }
    }

    override suspend fun syncNotes(): Result<Unit> {
        return try {
            val locallyDeletedNotes = noteLocalDataSource.getLocallyDeletedNotes()
            locallyDeletedNotes.forEach { localNote ->
                try {
                    val isNewNote = localNote.id.isLocal()

                    if (isNewNote) {
                        noteLocalDataSource.deleteNoteById(localNote.id)
                    } else {
                        noteRemoteDataSource.deleteNoteById(localNote.id)
                        noteLocalDataSource.deleteNoteById(localNote.id)
                    }
                } catch (e: Exception) {

                }
            }

            val unsyncedNotes = noteLocalDataSource.getAllNotes().first().filter { !it.isSynced }
            unsyncedNotes.forEach { localNote ->
                try {
                    val isNewNote = localNote.id.isLocal()

                    if (isNewNote) {
                        val response = noteRemoteDataSource.createNote(localNote.title, localNote.content)
                        val serverNote = response.data.note
                        noteLocalDataSource.deleteNoteById(localNote.id)
                        noteLocalDataSource.insertNote(serverNote.toEntityNote(isSynced = true).copy(createdAt = localNote.createdAt))
                    } else {
                        val response = noteRemoteDataSource.updateNote(localNote.id, localNote.title, localNote.content)
                        val serverNote = response.data.note
                        noteLocalDataSource.insertNote(serverNote.toEntityNote(isSynced = true).copy(createdAt = localNote.createdAt))
                    }
                } catch (e: Exception) {

                }
            }

            val response = noteRemoteDataSource.getAllNotes()

            val entities = response.data.notes.mapNotNull { remoteNote ->
                val localExisting = noteLocalDataSource.getNoteById(remoteNote.id)
                if (localExisting != null && !localExisting.isSynced) return@mapNotNull null
                remoteNote.toEntityNote(isSynced = true).copy(
                    createdAt = localExisting?.createdAt ?: System.currentTimeMillis()
                )
            }
            noteLocalDataSource.insertNotes(entities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNoteById(id: String): Result<Note> {
        return try {
            val response = noteRemoteDataSource.getNoteById(id)
            val noteDto = response.data.note
            val localExisting = noteLocalDataSource.getNoteById(id)

            noteLocalDataSource.insertNote(
                noteDto.toEntityNote(isSynced = true).copy(
                    createdAt = localExisting?.createdAt ?: System.currentTimeMillis()
                )
            )
            Result.success(noteDto.toDomainNote())
        } catch (e: Exception) {
            val localNote = noteLocalDataSource.getNoteById(id)
            if (localNote != null) {
                Result.success(localNote.toDomainNote())
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun createNote(title: String, content: String): Result<Note> {
        val localId = UUID.randomUUID().toString().addLocalBanner()
        val currentTime = System.currentTimeMillis()

        val initialLocalEntity = NoteEntity(
            id = localId,
            title = title,
            content = content,
            createdAt = currentTime,
            updatedAt = currentTime,
            isSynced = false,
            isDeleted = false
        )
        noteLocalDataSource.insertNote(initialLocalEntity)

        return try {
            val response = noteRemoteDataSource.createNote(title, content)
            val noteDto = response.data.note

            noteLocalDataSource.deleteNoteById(localId)
            noteLocalDataSource.insertNote(noteDto.toEntityNote(isSynced = true).copy(createdAt = currentTime))

            Result.success(noteDto.toDomainNote())
        } catch (e: Exception) {
            Result.success(initialLocalEntity.toDomainNote())
        }
    }

    override suspend fun updateNote(id: String, title: String, content: String): Result<Note> {
        val currentTime = System.currentTimeMillis()

        val existingNote = noteLocalDataSource.getNoteById(id)
        val originalCreatedAt = existingNote?.createdAt ?: currentTime

        val updatedLocalEntity = NoteEntity(
            id = id,
            title = title,
            content = content,
            createdAt = originalCreatedAt,
            updatedAt = currentTime,
            isSynced = false,
            isDeleted = false
        )
        noteLocalDataSource.insertNote(updatedLocalEntity)

        return try {
            val response = noteRemoteDataSource.updateNote(id, title, content)
            val noteDto = response.data.note

            noteLocalDataSource.insertNote(noteDto.toEntityNote(isSynced = true).copy(createdAt = originalCreatedAt))
            Result.success(noteDto.toDomainNote())
        } catch (e: Exception) {
            Result.success(updatedLocalEntity.toDomainNote())
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        return try {
            noteRemoteDataSource.deleteNoteById(id)
            noteLocalDataSource.deleteNoteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            val localNote = noteLocalDataSource.getNoteById(id)
            if (localNote != null) {
                noteLocalDataSource.insertNote(localNote.copy(isDeleted = true, isSynced = false))
            }
            Result.success(Unit)
        }
    }

    override suspend fun deleteAllNotes(): Result<Unit> {
        return try {
            noteRemoteDataSource.deleteAllNotes()
            noteLocalDataSource.deleteAllNotes()
            Result.success(Unit)
        } catch (e: Exception) {
            noteLocalDataSource.deleteAllNotes()
            Result.success(Unit)
        }
    }
}
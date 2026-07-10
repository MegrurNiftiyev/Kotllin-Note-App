package com.example.note_app_kotllin.core.di

import com.example.note_app_kotllin.data.repostories.AiRepository
import com.example.note_app_kotllin.data.repostories.AuthRepository
import com.example.note_app_kotllin.data.repostories.NotesRepository
import com.example.note_app_kotllin.data.repostories.SettingsRepository
import com.example.note_app_kotllin.data.repostories.TodoRepository
import com.example.note_app_kotllin.data.repostories.UserRepository
import com.example.note_app_kotllin.domain.repositories.IAiRepository
import com.example.note_app_kotllin.domain.repositories.IAuthRepository
import com.example.note_app_kotllin.domain.repositories.INotesRepository
import com.example.note_app_kotllin.domain.repositories.ISettingsRepository
import com.example.note_app_kotllin.domain.repositories.ITodoRepository
import com.example.note_app_kotllin.domain.repositories.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindSettingsRepository(
        impl: SettingsRepository
    ): ISettingsRepository


    @Binds
    abstract  fun bindAuthRepository(
        impl: AuthRepository
    ): IAuthRepository

    @Binds
    abstract  fun bindUserRepository(
        impl: UserRepository
    ): IUserRepository

    @Binds
    abstract  fun bindNoteRepository(
        impl: NotesRepository
    ): INotesRepository

    @Binds
    abstract  fun bindTodoRepository(
        impl: TodoRepository
    ): ITodoRepository

    @Binds
    abstract  fun bindAiRepository(
        impl: AiRepository
    ): IAiRepository

}
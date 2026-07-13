package com.example.note_app_kotllin.domain.repositories

import com.example.note_app_kotllin.domain.models.User
import kotlinx.coroutines.flow.Flow


interface IUserRepository {
    suspend fun getUser(): User?
    suspend fun  syncUser(): Result<Unit>

}
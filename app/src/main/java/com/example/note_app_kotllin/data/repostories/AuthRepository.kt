package com.example.note_app_kotllin.data.repostories

import com.example.note_app_kotllin.core.constants.CacheKeys
import com.example.note_app_kotllin.core.exceptions.AuthException
import com.example.note_app_kotllin.core.exceptions.NetworkException
import com.example.note_app_kotllin.core.managers.EncryptedCacheManager
import com.example.note_app_kotllin.data.datasoruces.local.NoteLocalDataSource
import com.example.note_app_kotllin.data.datasoruces.local.TodoLocalDataSource
import com.example.note_app_kotllin.data.datasoruces.local.UserLocalDataSource
import com.example.note_app_kotllin.data.datasoruces.remote.datasources.AuthRemoteDataSource
import com.example.note_app_kotllin.domain.models.User
import com.example.note_app_kotllin.domain.repositories.IAuthRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val encryptedCacheManager: EncryptedCacheManager,
    private val localDataSource: UserLocalDataSource,
    private val noteLocalDataSource: NoteLocalDataSource,
    private val todoLocalDataSource: TodoLocalDataSource

) : IAuthRepository {

    override suspend fun register(userName: String, email: String, password: String): Result<User> {
        return try {
            val response = remoteDataSource.register(userName, email, password)

            encryptedCacheManager.saveSecureString(
                CacheKeys.ACCESS_TOKEN, response.data.accessToken
            )
            encryptedCacheManager.saveSecureString(
                CacheKeys.REFRESH_TOKEN, response.data.refreshToken
            )

            val user = response.data.user
            localDataSource.saveUser(user.toEntityUser())
            Result.success(user.toDomainUser())
        } catch (e: AuthException) {
            Result.failure(e)
        } catch (e: NetworkException) {
            Result.failure(e)
        }
    }

    override suspend fun login(
        email: String, password: String
    ): Result<User> {
        return try {
            val response = remoteDataSource.login(email, password)
            encryptedCacheManager.saveSecureString(
                CacheKeys.ACCESS_TOKEN, response.data.accessToken
            )

            encryptedCacheManager.saveSecureString(
                CacheKeys.REFRESH_TOKEN, response.data.refreshToken
            )

            val user = response.data.user
            localDataSource.saveUser(user.toEntityUser())
            Result.success(user.toDomainUser())

        } catch (e: AuthException) {
            Result.failure(e)
        } catch (e: NetworkException) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = encryptedCacheManager.getSecureString(
                CacheKeys.REFRESH_TOKEN,
            )
            if (refreshToken == null) return Result.failure(AuthException.TokenNotFound())
            remoteDataSource.logout(refreshToken)
            encryptedCacheManager.clearAllCache()
            localDataSource.clearUser()
            noteLocalDataSource.deleteAllNotes()
            todoLocalDataSource.deleteAllTodos()
            Result.success(Unit)
        } catch (e: AuthException) {
            Result.failure(e)
        } catch (e: NetworkException) {
            Result.failure(e)
        }
    }
}
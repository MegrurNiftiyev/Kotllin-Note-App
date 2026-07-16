package com.example.note_app_kotllin.data.repostories

import com.example.note_app_kotllin.data.datasoruces.local.UserLocalDataSource
import com.example.note_app_kotllin.data.datasoruces.remote.datasources.UserRemoteDataSource
import com.example.note_app_kotllin.domain.models.User
import com.example.note_app_kotllin.domain.repositories.IUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : IUserRepository {

    override suspend fun getUser(): User? {
        return userLocalDataSource.getUser()?.toDomainUser()
    }

    override suspend fun syncUser(): Result<Unit> {
        return try {
            val response = userRemoteDataSource.getUser()
            val userRemote = response.data.toEntityUser()

            val userCache= userLocalDataSource.getUser()?.toDomainUser()
           if(userCache!=userRemote)
            userLocalDataSource.saveUser(userRemote)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}


package com.example.note_app_kotllin.core.interceptors

import com.example.note_app_kotllin.core.constants.CacheKeys
import com.example.note_app_kotllin.core.managers.EncryptedCacheManager
import com.example.note_app_kotllin.data.datasoruces.remote.datasources.AuthRemoteDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val encryptedCacheManager: EncryptedCacheManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (getRetryCount(response) >= 2) {
            return null
        }

        synchronized(this) {
            val oldToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val savedToken = encryptedCacheManager.getSecureString(CacheKeys.ACCESS_TOKEN)

            if (savedToken != null && savedToken != oldToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $savedToken")
                    .build()
            }

            val refreshToken = encryptedCacheManager.getSecureString(CacheKeys.REFRESH_TOKEN) ?: return null

            return runBlocking {
                try {
                    val refreshResult = authRemoteDataSource.refresh(refreshToken)

                    encryptedCacheManager.saveSecureString(CacheKeys.ACCESS_TOKEN, refreshResult.data.accessToken)
                    encryptedCacheManager.saveSecureString(CacheKeys.REFRESH_TOKEN, refreshResult.data.refreshToken)

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${refreshResult.data.accessToken}")
                        .build()
                } catch (e: Exception) {
                    encryptedCacheManager.removeSecureKey(CacheKeys.ACCESS_TOKEN)
                    encryptedCacheManager.removeSecureKey(CacheKeys.REFRESH_TOKEN)
                    null
                }
            }
        }
    }

    private fun getRetryCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
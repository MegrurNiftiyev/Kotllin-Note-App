package com.example.note_app_kotllin.core.interceptors

import com.example.note_app_kotllin.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAiInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authenticatedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(authenticatedRequest)
    }
}
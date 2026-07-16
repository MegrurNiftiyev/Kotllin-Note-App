package com.example.note_app_kotllin.core.managers

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.note_app_kotllin.core.constants.CacheKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        CacheKeys.SECURE_APP_CACHE,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSecureString(key: String, value: String) {
        encryptedPrefs.edit(commit = true) { putString(key, value) }
    }

    fun getSecureString(key: String, default: String? = null): String? {
        return encryptedPrefs.getString(key, default)
    }

    fun removeSecureKey(key: String) {
        encryptedPrefs.edit(commit = true) { remove(key) }
    }

     fun clearAllCache() {
        encryptedPrefs.edit(commit = true) { clear() }
    }
}
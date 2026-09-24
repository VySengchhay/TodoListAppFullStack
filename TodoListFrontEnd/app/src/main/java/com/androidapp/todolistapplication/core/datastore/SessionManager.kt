package com.androidapp.todolistapplication.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.androidapp.todolistapplication.core.security.AesCipher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val aesCipher: AesCipher
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACCESS_TOKEN]?.let { decryptOrNull(it) }
    }

    val isLoggedInFlow: Flow<Boolean> = accessTokenFlow.map { it != null }

    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = aesCipher.encrypt(accessToken)
            prefs[Keys.REFRESH_TOKEN] = aesCipher.encrypt(refreshToken)
            prefs[Keys.USER_ID] = aesCipher.encrypt(userId)
        }
    }

    suspend fun getAccessToken(): String? = accessTokenFlow.first()

    suspend fun getRefreshToken(): String? =
        context.dataStore.data.first()[Keys.REFRESH_TOKEN]?.let { decryptOrNull(it) }

    suspend fun getUserId(): String? =
        context.dataStore.data.first()[Keys.USER_ID]?.let { decryptOrNull(it) }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    // A value that can't be decrypted (e.g. restored from a backup onto a device whose
    // Keystore doesn't have the key) is treated as "no session" instead of crashing on launch.
    private fun decryptOrNull(value: String): String? =
        runCatching { aesCipher.decrypt(value) }.getOrNull()
}
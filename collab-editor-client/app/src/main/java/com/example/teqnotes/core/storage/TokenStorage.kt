package com.example.teqnotes.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
    }
    data class UserInfo(val id: Int, val email: String, val name: String)

    suspend fun getAccessToken(): String? = context.dataStore.data.first()[Keys.ACCESS_TOKEN]
    suspend fun getRefreshToken(): String? = context.dataStore.data.first()[Keys.REFRESH_TOKEN]

    fun getCurrentUser(): Flow<UserInfo?> = context.dataStore.data.map { prefs ->
        val id = prefs[Keys.USER_ID]?.toIntOrNull() ?: return@map null
        UserInfo(id, prefs[Keys.USER_EMAIL] ?: "", prefs[Keys.USER_NAME] ?: "")
    }

    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACCESS_TOKEN] != null
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUserInfo(id: Int, email: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = id.toString()
            prefs[Keys.USER_EMAIL] = email
            prefs[Keys.USER_NAME] = name
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
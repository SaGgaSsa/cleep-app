package dev.cleep.app.feature.auth.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.cleep.app.feature.auth.domain.AuthSession
import dev.cleep.app.feature.auth.domain.AuthUser

class SessionStorage(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun readSession(): AuthSession? {
        val apiKey = prefs.getString(KEY_API_KEY, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        return AuthSession(
            apiKey = apiKey,
            user = AuthUser(
                email = email,
                displayName = prefs.getString(KEY_DISPLAY_NAME, null),
                photoUrl = prefs.getString(KEY_PHOTO_URL, null),
            ),
        )
    }

    fun saveSession(session: AuthSession) {
        prefs.edit()
            .putString(KEY_API_KEY, session.apiKey)
            .putString(KEY_EMAIL, session.user.email)
            .putString(KEY_DISPLAY_NAME, session.user.displayName)
            .putString(KEY_PHOTO_URL, session.user.photoUrl)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun readApiKey(): String? = prefs.getString(KEY_API_KEY, null)

    private companion object {
        const val FILE_NAME = "cleep_secure_session"
        const val KEY_API_KEY = "api_key"
        const val KEY_EMAIL = "user_email"
        const val KEY_DISPLAY_NAME = "user_display_name"
        const val KEY_PHOTO_URL = "user_photo_url"
    }
}

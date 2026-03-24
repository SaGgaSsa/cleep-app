package dev.cleep.app.feature.auth.data

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dev.cleep.app.BuildConfig
import dev.cleep.app.feature.auth.domain.AuthUser
import java.security.SecureRandom
import java.util.Base64

class GoogleAuthClient(
    private val appContext: Context,
) {
    private val credentialManager by lazy { CredentialManager.create(appContext) }

    suspend fun signIn(activity: Activity): AuthUser {
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            serverClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID,
        )
            .setNonce(generateNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val result = try {
            credentialManager.getCredential(
                context = activity,
                request = request,
            )
        } catch (error: GetCredentialException) {
            throw mapSignInError(error)
        }

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type in setOf(
                GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL,
                GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_SIWG_CREDENTIAL,
            )
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            return AuthUser(
                email = googleCredential.id,
                displayName = googleCredential.displayName,
                photoUrl = googleCredential.profilePictureUri?.toString(),
                idToken = googleCredential.idToken,
            )
        }

        error("Unsupported credential response: ${credential.type}")
    }

    suspend fun clearSession() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    private fun generateNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(randomBytes)
    }

    private fun mapSignInError(error: GetCredentialException): Throwable {
        val message = error.message.orEmpty()
        if (
            message.contains("account reauth failed", ignoreCase = true) ||
            message.contains("error 16", ignoreCase = true)
        ) {
            return IllegalStateException(
                "Google login is not configured for package ${appContext.packageName}. Check the Android OAuth client package name, signing SHA, and GOOGLE_SERVER_CLIENT_ID.",
                error,
            )
        }

        return error
    }
}

package dev.cleep.app.feature.auth.data

import android.app.Activity
import dev.cleep.app.feature.auth.domain.AuthRepository
import dev.cleep.app.feature.auth.domain.AuthSession
import dev.cleep.app.feature.auth.domain.AuthUser
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionStorage: SessionStorage,
    private val googleAuthClient: GoogleAuthClient,
    private val gitHubAuthClient: GitHubAuthClient,
    private val authStatusPoller: AuthStatusPoller,
) : AuthRepository {
    override suspend fun restoreSession(): AuthSession? = sessionStorage.readSession()

    override suspend fun signIn(activity: Activity): AuthSession {
        val user = googleAuthClient.signIn(activity)
        val idToken = user.idToken ?: error("Google sign-in did not return an idToken")
        return try {
            val response = authApi.register(
                RegisterRequest(
                    email = user.email,
                    idToken = idToken,
                ),
            )
            AuthSession(
                apiKey = response.apiKey,
                user = user.copy(idToken = null),
            ).also(sessionStorage::saveSession)
        } catch (exception: HttpException) {
            val body = exception.response()?.errorBody()?.string()
            val message = buildString {
                append("Register failed")
                append(" (")
                append(exception.code())
                append(")")
                if (!body.isNullOrBlank()) {
                    append(": ")
                    append(body)
                }
            }
            throw IllegalStateException(message, exception)
        }
    }

    override suspend fun signInWithGitHub(activity: Activity): AuthSession {
        val state = gitHubAuthClient.signIn(activity)
        val response = authStatusPoller.poll(state)
        return AuthSession(
            apiKey = response.apiKey,
            user = AuthUser(
                email = response.email,
                displayName = response.displayName,
                photoUrl = response.photoUrl,
            ),
        ).also(sessionStorage::saveSession)
    }

    override suspend fun signOut() {
        sessionStorage.clearSession()
        runCatching {
            googleAuthClient.clearSession()
        }
    }
}

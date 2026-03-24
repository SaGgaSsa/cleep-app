package dev.cleep.app.feature.auth.domain

import android.app.Activity

interface AuthRepository {
    suspend fun restoreSession(): AuthSession?
    suspend fun signIn(activity: Activity): AuthSession
    suspend fun signInWithGitHub(activity: Activity): AuthSession
    suspend fun signOut()
}

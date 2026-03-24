package dev.cleep.app.feature.auth.domain

data class AuthUser(
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val idToken: String? = null,
)

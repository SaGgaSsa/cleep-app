package dev.cleep.app.feature.auth.domain

data class AuthSession(
    val apiKey: String,
    val user: AuthUser,
)

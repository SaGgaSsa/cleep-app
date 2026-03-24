package dev.cleep.app.feature.auth.presentation

import dev.cleep.app.feature.auth.domain.AuthUser

data class AuthUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val user: AuthUser? = null,
    val errorMessage: String? = null,
)

package dev.cleep.app.feature.auth.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.cleep.app.feature.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val session = authRepository.restoreSession()
            _state.value = if (session == null) {
                AuthUiState(isLoading = false)
            } else {
                AuthUiState(
                    isLoading = false,
                    isAuthenticated = true,
                    user = session.user,
                )
            }
        }
    }

    fun signIn(activity: Activity) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                authRepository.signIn(activity)
            }.onSuccess { session ->
                _state.value = AuthUiState(
                    isLoading = false,
                    isAuthenticated = true,
                    user = session.user,
                )
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        user = null,
                        errorMessage = error.message ?: "Unknown auth error",
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _state.value = AuthUiState(isLoading = false)
        }
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AuthViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return AuthViewModel(authRepository) as T
    }
}

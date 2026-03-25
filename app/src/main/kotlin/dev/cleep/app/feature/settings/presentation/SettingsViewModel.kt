package dev.cleep.app.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.cleep.app.feature.settings.domain.SettingsUsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val repository: SettingsUsageRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    suspend fun refresh() {
        _state.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
            )
        }

        runCatching { repository.getUsage() }
            .onSuccess { usage ->
                _state.value = SettingsUiState(
                    usage = usage,
                    isLoading = false,
                )
            }
            .onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load usage",
                    )
                }
            }
    }

    fun clear() {
        _state.value = SettingsUiState()
    }
}

class SettingsViewModelFactory(
    private val repository: SettingsUsageRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SettingsViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(repository) as T
    }
}

package dev.cleep.app.feature.cleeps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.cleep.app.feature.cleeps.domain.Cleep
import dev.cleep.app.feature.cleeps.domain.CleepsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CleepsViewModel(
    private val repository: CleepsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CleepsUiState())
    val state: StateFlow<CleepsUiState> = _state.asStateFlow()

    suspend fun refresh() {
        val hasItems = _state.value.items.isNotEmpty()
        _state.update {
            it.copy(
                isLoading = !hasItems,
                isRefreshing = hasItems,
                errorMessage = null,
            )
        }

        runCatching { repository.getCleeps() }
            .onSuccess { cleeps ->
                _state.value = CleepsUiState(items = cleeps)
            }
            .onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = error.message ?: "Failed to load cleeps",
                    )
                }
            }
    }

    suspend fun createCleep(content: String): Result<Cleep> {
        _state.update { it.copy(isCreating = true, errorMessage = null) }
        return runCatching {
            repository.createCleep(content.trim())
        }.onSuccess { cleep ->
            _state.update {
                it.copy(
                    isCreating = false,
                    items = listOf(cleep) + it.items,
                )
            }
        }.onFailure { error ->
            _state.update {
                it.copy(
                    isCreating = false,
                    errorMessage = error.message ?: "Failed to create cleep",
                )
            }
        }
    }

    suspend fun deleteCleep(id: String): Result<Unit> {
        val previousItems = _state.value.items
        _state.update {
            it.copy(
                items = it.items.filterNot { cleep -> cleep.id == id },
                deletingIds = it.deletingIds + id,
                errorMessage = null,
            )
        }

        return runCatching {
            repository.deleteCleep(id)
        }.onSuccess {
            _state.update { it.copy(deletingIds = it.deletingIds - id) }
        }.onFailure { error ->
            _state.update {
                it.copy(
                    items = previousItems,
                    deletingIds = it.deletingIds - id,
                    errorMessage = error.message ?: "Failed to delete cleep",
                )
            }
        }
    }

    fun clear() {
        _state.value = CleepsUiState()
    }
}

class CleepsViewModelFactory(
    private val repository: CleepsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(CleepsViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return CleepsViewModel(repository) as T
    }
}

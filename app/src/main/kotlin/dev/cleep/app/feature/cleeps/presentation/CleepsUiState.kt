package dev.cleep.app.feature.cleeps.presentation

import dev.cleep.app.feature.cleeps.domain.Cleep

data class CleepsUiState(
    val items: List<Cleep> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isCreating: Boolean = false,
    val deletingIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
) {
    val hasLoaded: Boolean get() = isLoading.not() && isRefreshing.not()
}

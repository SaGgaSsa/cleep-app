package dev.cleep.app.feature.settings.presentation

import dev.cleep.app.feature.settings.domain.SettingsUsage

data class SettingsUiState(
    val usage: SettingsUsage? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

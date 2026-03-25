package dev.cleep.app.feature.settings.domain

interface SettingsUsageRepository {
    suspend fun getUsage(): SettingsUsage
}

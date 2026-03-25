package dev.cleep.app.feature.settings.data

import dev.cleep.app.feature.settings.domain.SettingsUsage
import dev.cleep.app.feature.settings.domain.SettingsUsageRepository

class SettingsRepositoryImpl(
    private val api: SettingsApi,
) : SettingsUsageRepository {
    override suspend fun getUsage(): SettingsUsage = api.getUsage().toDomain()
}

private fun UsageResponseDto.toDomain(): SettingsUsage = SettingsUsage(
    activeCleepsUsed = usage.activeCleeps,
    activeCleepsLimit = limits.activeCleeps,
    dailyCleepsUsed = usage.dailyCleeps,
    dailyCleepsLimit = limits.dailyCleeps,
    activeProjectsUsed = usage.activeProjects,
    activeProjectsLimit = limits.activeProjects,
    historyUsed = usage.historyCount,
    historyLimit = limits.historyRetention,
)

package dev.cleep.app.feature.settings.domain

data class SettingsUsage(
    val dailyCleepsUsed: Int,
    val dailyCleepsLimit: Int,
    val activeProjectsUsed: Int,
    val activeProjectsLimit: Int?,
    val historyUsed: Int,
    val historyLimit: Int,
)

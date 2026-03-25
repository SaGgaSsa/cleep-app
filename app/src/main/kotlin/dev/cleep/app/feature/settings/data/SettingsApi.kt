package dev.cleep.app.feature.settings.data

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface SettingsApi {
    @GET("usage")
    suspend fun getUsage(): UsageResponseDto
}

@Serializable
data class UsageResponseDto(
    val limits: UsageLimitsDto,
    val usage: UsageValuesDto,
)

@Serializable
data class UsageLimitsDto(
    val dailyCleeps: Int,
    val activeProjects: Int? = null,
    val historyRetention: Int,
)

@Serializable
data class UsageValuesDto(
    val dailyCleeps: Int,
    val activeProjects: Int,
    val historyCount: Int,
)

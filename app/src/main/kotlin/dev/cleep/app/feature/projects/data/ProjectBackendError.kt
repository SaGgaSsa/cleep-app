package dev.cleep.app.feature.projects.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException

@OptIn(ExperimentalSerializationApi::class)
private val projectBackendErrorJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

@Serializable
private data class ProjectBackendErrorDto(
    val error: String,
)

fun parseProjectBackendError(exception: HttpException, fallbackLabel: String): IllegalStateException {
    val body = exception.response()?.errorBody()?.string().orEmpty()
    val backendError = runCatching {
        projectBackendErrorJson.decodeFromString<ProjectBackendErrorDto>(body)
    }.getOrNull()

    val message = backendError?.error ?: "$fallbackLabel (${exception.code()})"
    return IllegalStateException(message, exception)
}

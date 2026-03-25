package dev.cleep.app.feature.cleeps.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalSerializationApi::class)
private val backendErrorJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

@Serializable
private data class BackendErrorDto(
    val error: String,
    val code: String? = null,
    val limitType: String? = null,
    val limit: Int? = null,
    val current: Int? = null,
    val resetAt: String? = null,
)

fun parseCleepBackendError(exception: HttpException, fallbackLabel: String): IllegalStateException {
    val body = exception.response()?.errorBody()?.string().orEmpty()
    val backendError = body.decodeBackendError()

    val message = if (backendError != null) {
        when {
            backendError.limitType == "dailyCleeps" && backendError.resetAt != null -> {
                "${backendError.error}. Resets at ${backendError.resetAt.toUtcResetLabel()}."
            }
            else -> backendError.error
        }
    } else {
        "$fallbackLabel (${exception.code()})"
    }

    return IllegalStateException(message, exception)
}

private fun String.decodeBackendError(): BackendErrorDto? = runCatching {
    backendErrorJson.decodeFromString<BackendErrorDto>(this)
}.getOrNull()

private fun String.toUtcResetLabel(): String = try {
    DateTimeFormatter.ofPattern("HH:mm 'UTC'")
        .format(Instant.parse(this).atZone(java.time.ZoneOffset.UTC))
} catch (_: DateTimeParseException) {
    this
}

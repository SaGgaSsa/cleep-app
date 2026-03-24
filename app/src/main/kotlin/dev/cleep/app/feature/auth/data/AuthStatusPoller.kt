package dev.cleep.app.feature.auth.data

import android.os.SystemClock
import kotlinx.coroutines.delay
import retrofit2.HttpException
class AuthStatusPoller(
    private val authApi: AuthApi,
    private val pollIntervalMillis: Long = 2_000,
    private val timeoutMillis: Long = 3 * 60_000,
    private val timeProvider: () -> Long = SystemClock::elapsedRealtime,
    private val sleeper: suspend (Long) -> Unit = { delay(it) },
) {
    suspend fun poll(state: String): AuthStatusResponse {
        val deadline = timeProvider() + timeoutMillis

        while (timeProvider() < deadline) {
            try {
                val response = authApi.authStatus(state)
                when {
                    response.code() == 202 -> Unit
                    response.isSuccessful -> {
                        val body = response.body()
                            ?: throw IllegalStateException("GitHub auth status returned an empty response")
                        if (body.status.equals("pending", ignoreCase = true)) {
                            Unit
                        } else if (!body.apiKey.isNullOrBlank() && !body.email.isNullOrBlank()) {
                            return body
                        } else {
                            throw IllegalStateException("GitHub auth status response is missing session data")
                        }
                    }
                    else -> throw HttpException(response)
                }
            } catch (error: HttpException) {
                if (error.code() != 404) {
                    throw error
                }
            }

            val remaining = deadline - timeProvider()
            if (remaining > 0) {
                sleeper(minOf(pollIntervalMillis, remaining))
            }
        }

        throw IllegalStateException("GitHub login timed out after ${timeoutMillis / 1000} seconds")
    }
}

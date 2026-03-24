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
                return authApi.authStatus(state)
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

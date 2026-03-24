package dev.cleep.app.feature.auth.data

import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AuthStatusPollerTest {
    @Test
    fun `returns auth status after pending responses`() = runTest {
        val api = FakeAuthApi(
            authStatusResponses = mutableListOf(
                pendingResponse(),
                pendingResponse(),
                AuthStatusResponse(
                    apiKey = "api-key",
                    email = "dev@cleep.app",
                    displayName = "Cleep Dev",
                ),
            ),
        )
        var now = 0L
        val poller = AuthStatusPoller(
            authApi = api,
            pollIntervalMillis = 2_000,
            timeoutMillis = 10_000,
            timeProvider = { now },
            sleeper = { delayMillis -> now += delayMillis },
        )

        val result = poller.poll("state-123")

        assertEquals("api-key", result.apiKey)
        assertEquals("dev@cleep.app", result.email)
        assertEquals(3, api.authStatusCallCount)
    }

    @Test
    fun `throws timeout when auth never completes`() = runTest {
        val api = FakeAuthApi(
            authStatusResponses = MutableList(10) { pendingResponse() },
        )
        var now = 0L
        val poller = AuthStatusPoller(
            authApi = api,
            pollIntervalMillis = 2_000,
            timeoutMillis = 5_000,
            timeProvider = { now },
            sleeper = { delayMillis -> now += delayMillis },
        )

        try {
            poller.poll("state-123")
            fail("Expected timeout")
        } catch (error: IllegalStateException) {
            assertEquals("GitHub login timed out after 5 seconds", error.message)
        }
    }

    @Test
    fun `rethrows non pending http errors`() = runTest {
        val api = FakeAuthApi(
            authStatusResponses = mutableListOf(serverErrorResponse()),
        )
        val poller = AuthStatusPoller(
            authApi = api,
            pollIntervalMillis = 2_000,
            timeoutMillis = 5_000,
            timeProvider = { 0L },
            sleeper = { _ -> },
        )

        try {
            poller.poll("state-123")
            fail("Expected HttpException")
        } catch (error: HttpException) {
            assertEquals(500, error.code())
        }
    }

    private fun pendingResponse(): HttpException = httpException(404)

    private fun serverErrorResponse(): HttpException = httpException(500)

    private fun httpException(code: Int): HttpException {
        return HttpException(
            Response.error<Unit>(
                code,
                "".toResponseBody("text/plain".toMediaType()),
            ),
        )
    }
}

private class FakeAuthApi(
    private val authStatusResponses: MutableList<Any>,
) : AuthApi {
    var authStatusCallCount: Int = 0
        private set

    override suspend fun register(request: RegisterRequest): RegisterResponse {
        error("Not used in this test")
    }

    override suspend fun authStatus(state: String): AuthStatusResponse {
        authStatusCallCount += 1
        return when (val response = authStatusResponses.removeFirst()) {
            is AuthStatusResponse -> response
            is HttpException -> throw response
            else -> error("Unsupported fake response: $response")
        }
    }
}

package dev.cleep.app.feature.cleeps.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class CleepBackendErrorTest {
    @Test
    fun `uses backend error message for tier limit responses`() {
        val exception = httpException(
            code = 409,
            body = """
                {
                  "error": "Daily cleep limit reached for free tier",
                  "code": "tier_limit_reached",
                  "limitType": "dailyCleeps",
                  "resetAt": "2026-03-26T00:00:00.000Z"
                }
            """.trimIndent(),
        )

        val error = parseCleepBackendError(exception, fallbackLabel = "Create failed")

        assertEquals(
            "Daily cleep limit reached for free tier. Resets at 00:00 UTC.",
            error.message,
        )
    }

    @Test
    fun `falls back when backend error body is not valid json`() {
        val exception = httpException(code = 409, body = "nope")

        val error = parseCleepBackendError(exception, fallbackLabel = "Create failed")

        assertEquals("Create failed (409)", error.message)
    }

    private fun httpException(code: Int, body: String): HttpException {
        val responseBody = body.toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, responseBody))
    }
}

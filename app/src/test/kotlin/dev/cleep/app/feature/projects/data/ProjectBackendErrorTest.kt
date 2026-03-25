package dev.cleep.app.feature.projects.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ProjectBackendErrorTest {
    @Test
    fun `uses backend error message when response contains json error`() {
        val exception = httpException(
            code = 409,
            body = """{"error":"Cannot delete a project that has cleeps associated"}""",
        )

        val error = parseProjectBackendError(exception, fallbackLabel = "Delete project failed")

        assertEquals("Cannot delete a project that has cleeps associated", error.message)
    }

    @Test
    fun `falls back when backend error body is not valid json`() {
        val exception = httpException(code = 400, body = "nope")

        val error = parseProjectBackendError(exception, fallbackLabel = "Create project failed")

        assertEquals("Create project failed (400)", error.message)
    }

    private fun httpException(code: Int, body: String): HttpException {
        val responseBody = body.toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, responseBody))
    }
}

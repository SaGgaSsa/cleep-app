package dev.cleep.app.feature.auth.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalSerializationApi::class)
class AuthStatusResponseTest {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun `decodes github profile aliases`() {
        val response = json.decodeFromString<AuthStatusResponse>(
            """
            {
              "apiKey": "clp_test",
              "email": "dev@cleep.app",
              "login": "cleep-dev",
              "avatar_url": "https://avatars.githubusercontent.com/u/1?v=4"
            }
            """.trimIndent(),
        )

        assertEquals("clp_test", response.apiKey)
        assertEquals("dev@cleep.app", response.email)
        assertEquals("cleep-dev", response.displayName)
        assertEquals("https://avatars.githubusercontent.com/u/1?v=4", response.photoUrl)
    }
}

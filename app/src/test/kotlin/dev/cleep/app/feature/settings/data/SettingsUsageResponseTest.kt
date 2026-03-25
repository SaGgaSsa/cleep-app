package dev.cleep.app.feature.settings.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalSerializationApi::class)
class SettingsUsageResponseTest {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun `decodes usage response including unlimited projects`() {
        val response = json.decodeFromString<UsageResponseDto>(
            """
            {
              "limits": {
                "dailyCleeps": 100,
                "activeProjects": null,
                "historyRetention": 500
              },
              "usage": {
                "dailyCleeps": 12,
                "activeProjects": 7,
                "historyCount": 42
              }
            }
            """.trimIndent(),
        )

        assertEquals(100, response.limits.dailyCleeps)
        assertNull(response.limits.activeProjects)
        assertEquals(500, response.limits.historyRetention)
        assertEquals(12, response.usage.dailyCleeps)
        assertEquals(7, response.usage.activeProjects)
        assertEquals(42, response.usage.historyCount)
    }
}

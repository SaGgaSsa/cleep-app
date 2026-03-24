package dev.cleep.app.feature.auth.data

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import dev.cleep.app.BuildConfig
import java.util.UUID

class GitHubAuthClient {
    fun signIn(activity: Activity): String {
        val state = UUID.randomUUID().toString()
        val authUri = BuildConfig.BASE_URL.ensureTrailingSlash()
            .let(Uri::parse)
            .buildUpon()
            .appendPath("auth")
            .appendPath("github")
            .appendQueryParameter("state", state)
            .build()

        CustomTabsIntent.Builder()
            .build()
            .launchUrl(activity, authUri)

        return state
    }
}

private fun String.ensureTrailingSlash(): String = if (endsWith("/")) this else "$this/"

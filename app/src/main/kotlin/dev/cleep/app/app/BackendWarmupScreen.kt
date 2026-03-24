package dev.cleep.app.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.cleep.app.R
import dev.cleep.app.feature.auth.presentation.AuthHeroScreen

@Composable
fun BackendWarmupScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
) {
    AuthHeroScreen(
        statusText = stringResource(R.string.backend_warmup_status),
        taglineText = stringResource(R.string.backend_warmup_tagline),
        loading = isLoading,
        errorText = errorMessage?.let { stringResource(R.string.common_error, it) },
        footerContent = if (isLoading) {
            null
        } else {
            {
                HeroRetryButton(onRetry = onRetry)
            }
        },
    )
}

@Composable
private fun HeroRetryButton(onRetry: () -> Unit) {
    dev.cleep.app.core.designsystem.components.CleepPrimaryButton(
        text = stringResource(R.string.feed_retry),
        onClick = onRetry,
    )
}

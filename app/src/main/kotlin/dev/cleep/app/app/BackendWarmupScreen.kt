package dev.cleep.app.app

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
import dev.cleep.app.core.designsystem.components.CleepScreenScaffold
import dev.cleep.app.core.designsystem.components.CleepSectionLabel
import dev.cleep.app.core.designsystem.theme.CleepSpacing

@Composable
fun BackendWarmupScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
) {
    CleepScreenScaffold(verticalSpacing = CleepSpacing.space8) {
        CleepSectionLabel(text = stringResource(R.string.backend_warmup_section_title))
        Text(
            text = stringResource(R.string.backend_warmup_title),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        CleepPanel(color = MaterialTheme.colorScheme.surfaceContainer) {
            Text(
                text = stringResource(R.string.backend_warmup_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = stringResource(R.string.common_error, errorMessage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (!isLoading) {
            CleepPrimaryButton(
                text = stringResource(R.string.feed_retry),
                onClick = onRetry,
            )
        }
    }
}

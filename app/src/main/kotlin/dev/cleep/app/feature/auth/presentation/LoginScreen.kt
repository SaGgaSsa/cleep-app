package dev.cleep.app.feature.auth.presentation

import android.app.Activity
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
import dev.cleep.app.core.designsystem.components.CleepScreenScaffold
import dev.cleep.app.core.designsystem.components.CleepSectionLabel
import dev.cleep.app.core.designsystem.theme.CleepSpacing

@Composable
fun LoginScreen(
    state: AuthUiState,
    onContinueClick: (Activity) -> Unit,
) {
    val activity = LocalContext.current as? Activity

    CleepScreenScaffold(verticalSpacing = CleepSpacing.space8) {
        CleepSectionLabel(text = stringResource(R.string.login_section_auth))
        Text(
            text = stringResource(R.string.login_tagline),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        CleepPanel(color = MaterialTheme.colorScheme.surfaceContainer) {
            Text(
                text = stringResource(R.string.login_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (state.errorMessage != null) {
            Text(
                text = stringResource(R.string.common_error, state.errorMessage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        CleepPrimaryButton(
            text = stringResource(R.string.login_continue_with_google),
            onClick = { activity?.let(onContinueClick) },
            enabled = !state.isLoading && activity != null,
            trailingContent = {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "[GOOGLE]",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            },
        )
    }
}

package dev.cleep.app.feature.auth.presentation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.cleep.app.R

@Composable
fun LoginScreen(
    state: AuthUiState,
    onGoogleClick: (Activity) -> Unit,
    onGitHubClick: (Activity) -> Unit,
) {
    val activity = LocalContext.current as? Activity

    AuthHeroScreen(
        statusText = stringResource(R.string.login_status),
        taglineText = stringResource(R.string.login_tagline),
        primaryActionLabel = stringResource(R.string.login_continue_with_google),
        onPrimaryAction = { activity?.let(onGoogleClick) },
        primaryActionEnabled = activity != null && !state.isLoading,
        primaryActionLoading = state.isLoading,
        tertiaryActionLabel = stringResource(R.string.login_continue_with_github),
        onTertiaryAction = { activity?.let(onGitHubClick) },
        tertiaryActionEnabled = activity != null && !state.isLoading,
        secondaryActionLabel = stringResource(R.string.login_use_access_key),
        onSecondaryAction = {},
        secondaryActionEnabled = false,
        errorText = state.errorMessage?.let { stringResource(R.string.common_error, it) },
    )
}

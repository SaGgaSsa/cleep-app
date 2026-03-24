package dev.cleep.app.feature.cleeps.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
import dev.cleep.app.core.designsystem.components.CleepScreenScaffold
import dev.cleep.app.core.designsystem.components.CleepSectionLabel
import dev.cleep.app.core.designsystem.components.CleepUnderlineField
import dev.cleep.app.core.designsystem.theme.CleepSpacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    state: CleepsUiState,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onCreateCleep: suspend (String) -> Result<Unit>,
    modifier: Modifier = Modifier,
) {
    var content by rememberSaveable { mutableStateOf("") }
    val trimmedContent = content.trim()
    val savedMessage = stringResource(R.string.home_saved_message)
    val genericCreateError = stringResource(R.string.common_error, "Create failed")

    CleepScreenScaffold(
        modifier = modifier,
        verticalSpacing = CleepSpacing.space8,
    ) {
        CleepSectionLabel(text = stringResource(R.string.home_section_new_cleep))
        Text(
            text = stringResource(R.string.home_section_new_cleep).uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        CleepPanel(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            CleepUnderlineField(
                value = content,
                onValueChange = { content = it },
                placeholder = stringResource(R.string.home_input_hint),
                minLines = 12,
                maxLines = 18,
                keyboardOptions = KeyboardOptions.Default,
            )
        }
        CleepPrimaryButton(
            text = stringResource(R.string.home_save_cleep),
            onClick = {
                scope.launch {
                    onCreateCleep(trimmedContent)
                        .onSuccess {
                            content = ""
                            snackbarHostState.showSnackbar(message = savedMessage)
                        }
                        .onFailure { error ->
                            snackbarHostState.showSnackbar(
                                message = error.message ?: genericCreateError,
                            )
                        }
                }
            },
            enabled = trimmedContent.isNotEmpty() && !state.isCreating,
            trailingContent = {
                Text(
                    text = if (state.isCreating) "[SENDING]" else "[SAVE]",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            },
        )
    }
}

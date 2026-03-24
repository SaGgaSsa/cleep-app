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
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
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
    val genericCreateError = stringResource(R.string.common_error, "Create failed")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = CleepSpacing.space6,
                top = CleepSpacing.space8,
                end = CleepSpacing.space6,
                bottom = CleepSpacing.space3,
            ),
        verticalArrangement = Arrangement.spacedBy(CleepSpacing.space3),
    ) {
        Text(
            text = stringResource(R.string.home_section_new_cleep).uppercase(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        CleepPanel(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            contentPadding = PaddingValues(0.dp),
        ) {
            CleepUnderlineField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxSize(),
                placeholder = stringResource(R.string.home_input_hint),
                minLines = 12,
                maxLines = Int.MAX_VALUE,
                keyboardOptions = KeyboardOptions.Default,
                showIndicator = false,
            )
        }

        CleepPrimaryButton(
            text = stringResource(R.string.home_save_cleep),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                scope.launch {
                    onCreateCleep(trimmedContent)
                        .onSuccess {
                            content = ""
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

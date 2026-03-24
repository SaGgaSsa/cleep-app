package dev.cleep.app.feature.cleeps.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.cleep.app.R
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.home_section_new_cleep),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text(stringResource(R.string.home_input_hint)) },
            minLines = 8,
            maxLines = 12,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
        )
        Button(
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Icon(Icons.Outlined.ArrowUpward, contentDescription = null)
            Text(
                text = stringResource(R.string.home_save_cleep),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

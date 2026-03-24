package dev.cleep.app.feature.cleeps.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.cleep.app.R
import dev.cleep.app.feature.cleeps.domain.Cleep
import java.time.ZoneId
import java.time.format.FormatStyle
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CleepsListScreen(
    state: CleepsUiState,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onRefresh: suspend () -> Unit,
    onDelete: suspend (String) -> Result<Unit>,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
    val deleteErrorTemplate = stringResource(R.string.feed_delete_error, "%s")
    val deleteSuccess = stringResource(R.string.feed_delete_success)
    val deleteLoading = stringResource(R.string.feed_delete_loading)
    val formatter = remember(locale) {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(locale)
            .withZone(ZoneId.systemDefault())
    }
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.feed_section_subtitle),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { scope.launch { onRefresh() } }) {
                Icon(Icons.Outlined.Refresh, contentDescription = stringResource(R.string.feed_retry))
            }
        }

        when {
            state.isLoading && state.items.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null && state.items.isEmpty() -> {
                StatusCard(
                    title = stringResource(R.string.feed_error_title),
                    description = state.errorMessage,
                    actionLabel = stringResource(R.string.feed_retry),
                    onActionClick = { scope.launch { onRefresh() } },
                )
            }

            state.items.isEmpty() -> {
                StatusCard(
                    title = stringResource(R.string.feed_empty_title),
                    description = stringResource(R.string.feed_empty_description),
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        items = state.items,
                        key = { it.id },
                    ) { cleep ->
                        CleepCard(
                            cleep = cleep,
                            formatter = formatter,
                            isDeleting = cleep.id in state.deletingIds,
                            deleteLoading = deleteLoading,
                            onDeleteClick = { pendingDeleteId = cleep.id },
                        )
                    }
                }
            }
        }
    }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(stringResource(R.string.feed_delete_title)) },
            text = { Text(stringResource(R.string.feed_delete_confirmation)) },
            confirmButton = {
                Button(
                    onClick = {
                        val id = pendingDeleteId ?: return@Button
                        pendingDeleteId = null
                        scope.launch {
                            onDelete(id)
                                .onSuccess {
                                    snackbarHostState.showSnackbar(message = deleteSuccess)
                                }
                                .onFailure { error ->
                                    snackbarHostState.showSnackbar(
                                        message = deleteErrorTemplate.replace(
                                            "%s",
                                            error.message ?: "Delete failed",
                                        ),
                                    )
                                }
                        }
                    },
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingDeleteId = null }) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
        )
    }
}

@Composable
private fun StatusCard(
    title: String,
    description: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (actionLabel != null && onActionClick != null) {
                    Button(onClick = onActionClick) {
                        Text(actionLabel)
                    }
                }
            }
        }
    }
}

@Composable
private fun CleepCard(
    cleep: Cleep,
    formatter: DateTimeFormatter,
    isDeleting: Boolean,
    deleteLoading: String,
    onDeleteClick: () -> Unit,
) {
    ElevatedCard {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = formatter.format(cleep.createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = cleep.content,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedButton(
                onClick = onDeleteClick,
                enabled = !isDeleting,
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = null)
                Text(
                    text = if (isDeleting) deleteLoading else stringResource(R.string.feed_delete),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

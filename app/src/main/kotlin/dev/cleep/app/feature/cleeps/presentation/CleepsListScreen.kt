package dev.cleep.app.feature.cleeps.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
import dev.cleep.app.core.designsystem.components.CleepScreenScaffold
import dev.cleep.app.core.designsystem.components.CleepSecondaryButton
import dev.cleep.app.core.designsystem.components.CleepTextAction
import dev.cleep.app.core.designsystem.theme.CleepSpacing
import dev.cleep.app.feature.cleeps.domain.Cleep
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
    val deleteErrorTemplate = stringResource(R.string.feed_delete_error, "%s")
    val deleteLoading = stringResource(R.string.feed_delete_loading)
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    CleepScreenScaffold(
        modifier = modifier,
        verticalSpacing = CleepSpacing.space8,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.feed_section_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            CleepTextAction(
                text = stringResource(R.string.feed_retry),
                onClick = { scope.launch { onRefresh() } },
            )
        }

        when {
            state.isLoading && state.items.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                    verticalArrangement = Arrangement.spacedBy(CleepSpacing.space12),
                ) {
                    items(
                        items = state.items,
                        key = { it.id },
                    ) { cleep ->
                        CleepCard(
                            cleep = cleep,
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
                CleepPrimaryButton(
                    text = stringResource(R.string.common_delete),
                    onClick = {
                        val id = pendingDeleteId ?: return@CleepPrimaryButton
                        pendingDeleteId = null
                        scope.launch {
                            onDelete(id)
                                .onSuccess { }
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
                )
            },
            dismissButton = {
                CleepSecondaryButton(
                    text = stringResource(R.string.common_cancel),
                    onClick = { pendingDeleteId = null },
                )
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
        CleepPanel(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                verticalArrangement = Arrangement.spacedBy(CleepSpacing.space3),
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
                    CleepPrimaryButton(text = actionLabel, onClick = onActionClick)
                }
            }
        }
    }
}

@Composable
private fun CleepCard(
    cleep: Cleep,
    isDeleting: Boolean,
    deleteLoading: String,
    onDeleteClick: () -> Unit,
) {
    CleepPanel(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(CleepSpacing.space3),
        ) {
            Text(
                text = cleep.content,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val projectName = cleep.projectName?.trim()?.takeIf { it.isNotEmpty() }
                if (projectName != null) {
                    ProjectBadge(projectName = projectName)
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                Box(
                    modifier = Modifier.widthIn(min = 120.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    CleepSecondaryButton(
                        text = if (isDeleting) deleteLoading else stringResource(R.string.feed_delete),
                        onClick = onDeleteClick,
                        enabled = !isDeleting,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectBadge(projectName: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        Text(
            text = projectName.uppercase(),
            modifier = Modifier.padding(horizontal = CleepSpacing.space3, vertical = CleepSpacing.space1),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

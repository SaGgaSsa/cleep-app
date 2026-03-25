package dev.cleep.app.feature.projects.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
import dev.cleep.app.core.designsystem.components.CleepScreenScaffold
import dev.cleep.app.core.designsystem.components.CleepSecondaryButton
import dev.cleep.app.core.designsystem.components.CleepTextAction
import dev.cleep.app.core.designsystem.components.CleepUnderlineField
import dev.cleep.app.core.designsystem.theme.CleepSpacing
import dev.cleep.app.feature.projects.domain.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ProjectsScreen(
    state: ProjectsUiState,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onRefresh: suspend () -> Unit,
    onStartCreate: () -> Unit,
    onStartEdit: (Project) -> Unit,
    onDraftNameChange: (String) -> Unit,
    onSave: suspend () -> Unit,
    onDelete: suspend () -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmDelete by remember { mutableStateOf(false) }
    val errorTemplate = stringResource(R.string.common_error, "%s")
    val selectedProject = state.selectedProject
    val draftName = state.draftName
    val trimmedName = draftName.trim()
    val canSubmit = trimmedName.length in 3..20 &&
        PROJECT_NAME_PATTERN.matches(trimmedName) &&
        !state.isSaving &&
        !state.isDeleting

    LaunchedEffect(Unit) {
        if (state.items.isEmpty() && !state.isLoading) {
            onRefresh()
        }
    }

    CleepScreenScaffold(
        modifier = modifier,
        verticalSpacing = CleepSpacing.space6,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.projects_title),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            CleepTextAction(
                text = stringResource(R.string.projects_new),
                onClick = onStartCreate,
            )
        }

        state.errorMessage?.let { message ->
            Text(
                text = stringResource(R.string.common_error, message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth >= 760.dp

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space6),
                    verticalAlignment = Alignment.Top,
                ) {
                    ProjectsList(
                        state = state,
                        scope = scope,
                        onRefresh = onRefresh,
                        onSelectProject = onStartEdit,
                        modifier = Modifier.weight(1.2f),
                    )
                    ProjectEditor(
                        state = state,
                        draftName = draftName,
                        canSubmit = canSubmit,
                        onDraftNameChange = onDraftNameChange,
                        onStartCreate = onStartCreate,
                        scope = scope,
                        onSave = onSave,
                        onDeleteClick = { confirmDelete = true },
                        modifier = Modifier.weight(0.95f),
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(CleepSpacing.space6),
                ) {
                    ProjectEditor(
                        state = state,
                        draftName = draftName,
                        canSubmit = canSubmit,
                        onDraftNameChange = onDraftNameChange,
                        onStartCreate = onStartCreate,
                        scope = scope,
                        onSave = onSave,
                        onDeleteClick = { confirmDelete = true },
                    )
                    ProjectsList(
                        state = state,
                        scope = scope,
                        onRefresh = onRefresh,
                        onSelectProject = onStartEdit,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    if (confirmDelete && selectedProject != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.projects_delete_title)) },
            text = { Text(stringResource(R.string.projects_delete_confirmation, selectedProject.name)) },
            confirmButton = {
                CleepPrimaryButton(
                    text = stringResource(R.string.common_delete),
                    onClick = {
                        confirmDelete = false
                        scope.launch {
                            runCatching { onDelete() }
                                .onFailure { error ->
                                    snackbarHostState.showSnackbar(
                                        message = errorTemplate.replace("%s", error.message ?: "Delete failed"),
                                    )
                                }
                        }
                    },
                )
            },
            dismissButton = {
                CleepSecondaryButton(
                    text = stringResource(R.string.common_cancel),
                    onClick = { confirmDelete = false },
                )
            },
        )
    }
}

@Composable
private fun ProjectsList(
    state: ProjectsUiState,
    scope: CoroutineScope,
    onRefresh: suspend () -> Unit,
    onSelectProject: (Project) -> Unit,
    modifier: Modifier = Modifier,
) {
    CleepPanel(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(CleepSpacing.space4),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.projects_available, state.items.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                CleepTextAction(
                    text = stringResource(R.string.feed_retry),
                    onClick = { scope.launch { onRefresh() } },
                )
            }

            if (state.isLoading && state.items.isEmpty()) {
                Text(
                    text = stringResource(R.string.projects_loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else if (state.items.isEmpty()) {
                Text(
                    text = stringResource(R.string.projects_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(CleepSpacing.space3),
                ) {
                    items(state.items, key = { it.id }) { project ->
                        val selected = project.id == state.selectedProjectId
                        ProjectRow(
                            project = project,
                            selected = selected,
                            onClick = { onSelectProject(project) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectRow(
    project: Project,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
            )
            .clickable(onClick = onClick)
            .padding(CleepSpacing.space4),
        verticalArrangement = Arrangement.spacedBy(CleepSpacing.space2),
    ) {
        Text(
            text = project.name.uppercase(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.projects_created_at, project.createdAt.toDisplayDate()),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}

@Composable
private fun ProjectEditor(
    state: ProjectsUiState,
    draftName: String,
    canSubmit: Boolean,
    onDraftNameChange: (String) -> Unit,
    onStartCreate: () -> Unit,
    scope: CoroutineScope,
    onSave: suspend () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedProject = state.selectedProject

    CleepPanel(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 460.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(CleepSpacing.space4),
        ) {
            Text(
                text = if (selectedProject == null) {
                    stringResource(R.string.projects_create_title)
                } else {
                    stringResource(R.string.projects_edit_title)
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = if (selectedProject == null) "NEW_PROJECT" else selectedProject.name.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )

            CleepUnderlineField(
                value = draftName,
                onValueChange = onDraftNameChange,
                placeholder = stringResource(R.string.projects_name_placeholder),
                minLines = 1,
                maxLines = 1,
            )

            selectedProject?.let {
                Text(
                    text = stringResource(R.string.projects_created_at, it.createdAt.toDisplayDate()),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CleepPrimaryButton(
                    text = when {
                        state.isSaving -> stringResource(R.string.projects_saving)
                        selectedProject == null -> stringResource(R.string.projects_create_action)
                        else -> stringResource(R.string.projects_update_action)
                    },
                    modifier = Modifier.weight(1f),
                    onClick = { scope.launch { onSave() } },
                    enabled = canSubmit,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CleepSecondaryButton(
                    text = stringResource(R.string.projects_new),
                    modifier = Modifier.weight(1f),
                    onClick = onStartCreate,
                    enabled = !state.isSaving && !state.isDeleting,
                )
                if (selectedProject != null) {
                    CleepSecondaryButton(
                        text = if (state.isDeleting) {
                            stringResource(R.string.projects_deleting)
                        } else {
                            stringResource(R.string.common_delete)
                        },
                        modifier = Modifier.weight(1f),
                        onClick = onDeleteClick,
                        enabled = !state.isSaving && !state.isDeleting,
                    )
                }
            }
        }
    }
}

private val PROJECT_NAME_PATTERN = Regex("^[A-Za-z0-9 _-]+$")

private fun java.time.Instant.toDisplayDate(): String = DateTimeFormatter
    .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
    .withZone(ZoneId.systemDefault())
    .format(this)

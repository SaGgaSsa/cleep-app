package dev.cleep.app.feature.cleeps.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
import dev.cleep.app.core.designsystem.components.CleepUnderlineField
import dev.cleep.app.core.designsystem.theme.CleepSpacing
import dev.cleep.app.feature.projects.domain.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    state: CleepsUiState,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onCreateCleep: suspend (String) -> Result<Unit>,
    onSelectProject: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var content by rememberSaveable { mutableStateOf("") }
    val trimmedContent = content.trim()
    val genericCreateError = stringResource(R.string.common_error, "Create failed")
    val noProjectLabel = stringResource(R.string.home_project_no_project)

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_section_new_cleep).uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            ProjectSelector(
                projects = state.projects,
                selectedProjectName = state.selectedProjectName,
                noProjectLabel = noProjectLabel,
                enabled = !state.isCreating,
                onProjectSelected = onSelectProject,
            )
        }

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

@Composable
private fun ProjectSelector(
    projects: List<Project>,
    selectedProjectName: String?,
    noProjectLabel: String,
    enabled: Boolean,
    onProjectSelected: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val noProjectMenuLabel = "[$noProjectLabel]"
    val selectorWidth = 220.dp
    val menuWidth = 220.dp
    val selectedLabel = projects
        .firstOrNull { it.name == selectedProjectName }
        ?.name
        ?.uppercase()
        ?: noProjectLabel

    Box(
        modifier = Modifier.width(selectorWidth),
        contentAlignment = Alignment.TopEnd,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                )
                .clickable(enabled = enabled) { expanded = true }
                .padding(horizontal = CleepSpacing.space3, vertical = CleepSpacing.space2),
            horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedLabel,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "v",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = selectorWidth - menuWidth, y = 0.dp),
            modifier = Modifier
                .width(menuWidth)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = noProjectMenuLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                onClick = {
                    expanded = false
                    onProjectSelected(null)
                },
            )
            projects.forEach { project ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = project.name.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    onClick = {
                        expanded = false
                        onProjectSelected(project.name)
                    },
                )
            }
        }
    }
}

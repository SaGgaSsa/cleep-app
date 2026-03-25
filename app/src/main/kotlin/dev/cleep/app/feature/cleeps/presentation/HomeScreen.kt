package dev.cleep.app.feature.cleeps.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
    val minCleepLength = 3
    val canSave = trimmedContent.length >= minCleepLength && !state.isCreating
    val genericCreateError = stringResource(R.string.common_error, "Create failed")
    val noProjectLabel = stringResource(R.string.home_project_no_project)
    val editorInteractionSource = remember { MutableInteractionSource() }
    val editorFocused by editorInteractionSource.collectIsFocusedAsState()
    val editorPanelAlpha by animateFloatAsState(
        targetValue = if (editorFocused) 1f else 0.62f,
        animationSpec = tween(durationMillis = 180),
        label = "editorPanelAlpha",
    )

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
                enabled = !state.isCreating && state.projects.isNotEmpty(),
                onProjectSelected = onSelectProject,
            )
        }

        CleepPanel(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = if (editorFocused) 22.dp else 0.dp,
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.34f),
                )
                .border(
                    width = 1.dp,
                    color = if (editorFocused) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                    } else {
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.18f)
                    },
                ),
            color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = editorPanelAlpha),
            contentPadding = PaddingValues(0.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CleepUnderlineField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxSize(),
                    placeholder = stringResource(R.string.home_input_hint),
                    minLines = 12,
                    maxLines = Int.MAX_VALUE,
                    keyboardOptions = KeyboardOptions.Default,
                    showIndicator = false,
                    interactionSource = editorInteractionSource,
                )
                Text(
                    text = "CHAR_COUNT: ${content.length.toString().padStart(3, '0')}",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = CleepSpacing.space3, vertical = CleepSpacing.space2),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                )
            }
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
            enabled = canSave,
            trailingContent = {
                Text(
                    text = if (state.isCreating) "[SENDING]" else "->",
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
    val hasProjects = projects.isNotEmpty()
    val selectedProject = projects
        .firstOrNull { it.name == selectedProjectName }
    val selectedLabel = selectedProject?.name?.uppercase() ?: noProjectLabel
    val selectorTextColor = if (selectedProject == null) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.primary
    }
    val selectorEnabled = enabled && hasProjects

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
                .clickable(enabled = selectorEnabled) { expanded = true }
                .padding(horizontal = CleepSpacing.space3, vertical = CleepSpacing.space2),
            horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedLabel,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = selectorTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "v",
                style = MaterialTheme.typography.labelLarge,
                color = if (selectorEnabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                },
            )
        }

        DropdownMenu(
            expanded = expanded && selectorEnabled,
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

package dev.cleep.app.feature.projects.presentation

import dev.cleep.app.feature.projects.domain.Project

data class ProjectsUiState(
    val items: List<Project> = emptyList(),
    val selectedProjectId: String? = null,
    val draftName: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedProject: Project?
        get() = items.firstOrNull { it.id == selectedProjectId }
}

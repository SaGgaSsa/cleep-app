package dev.cleep.app.feature.projects.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.cleep.app.feature.projects.domain.Project
import dev.cleep.app.feature.projects.domain.ProjectsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProjectsViewModel(
    private val repository: ProjectsRepository,
    private val syncProjects: suspend () -> Unit,
) : ViewModel() {
    private val _state = MutableStateFlow(ProjectsUiState())
    val state: StateFlow<ProjectsUiState> = _state.asStateFlow()

    suspend fun refresh() {
        val hasItems = _state.value.items.isNotEmpty()
        _state.update {
            it.copy(
                isLoading = !hasItems,
                errorMessage = null,
            )
        }

        runCatching { repository.getProjects().sortedBy(Project::name) }
            .onSuccess { projects ->
                val selectedProject = _state.value.selectedProjectId
                    ?.let { selectedId -> projects.firstOrNull { it.id == selectedId } }
                _state.value = ProjectsUiState(
                    items = projects,
                    selectedProjectId = selectedProject?.id,
                    draftName = selectedProject?.name.orEmpty(),
                )
            }
            .onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load projects",
                    )
                }
            }
    }

    fun startCreate() {
        _state.update {
            it.copy(
                selectedProjectId = null,
                draftName = "",
                errorMessage = null,
            )
        }
    }

    fun startEdit(project: Project) {
        _state.update {
            it.copy(
                selectedProjectId = project.id,
                draftName = project.name,
                errorMessage = null,
            )
        }
    }

    fun updateDraftName(value: String) {
        _state.update { it.copy(draftName = value, errorMessage = null) }
    }

    suspend fun saveProject() {
        val state = _state.value
        val draftName = state.draftName.trim()
        if (draftName.length !in 3..20 || !PROJECT_NAME_PATTERN.matches(draftName)) {
            _state.update { it.copy(errorMessage = "Project name must be 3-20 valid characters") }
            return
        }

        _state.update { it.copy(isSaving = true, errorMessage = null) }

        val selectedProject = state.selectedProject
        runCatching {
            if (selectedProject == null) {
                repository.createProject(draftName)
            } else {
                repository.updateProject(selectedProject.name, draftName)
            }
        }.onSuccess { project ->
            val items = _state.value.items
                .filterNot { it.id == project.id }
                .plus(project)
                .sortedBy(Project::name)
            _state.value = ProjectsUiState(
                items = items,
                selectedProjectId = project.id,
                draftName = project.name,
            )
            syncProjects()
        }.onFailure { error ->
            _state.update {
                it.copy(
                    isSaving = false,
                    errorMessage = error.message ?: "Failed to save project",
                )
            }
        }
    }

    suspend fun deleteSelectedProject() {
        val selectedProject = _state.value.selectedProject ?: return

        _state.update { it.copy(isDeleting = true, errorMessage = null) }

        runCatching {
            repository.deleteProject(selectedProject.name)
        }.onSuccess {
            val items = _state.value.items.filterNot { it.id == selectedProject.id }
            _state.value = ProjectsUiState(
                items = items,
            )
            syncProjects()
        }.onFailure { error ->
            _state.update {
                it.copy(
                    isDeleting = false,
                    errorMessage = error.message ?: "Failed to delete project",
                )
            }
        }
    }

    fun clear() {
        _state.value = ProjectsUiState()
    }
}

private val PROJECT_NAME_PATTERN = Regex("^[A-Za-z0-9 _-]+$")

class ProjectsViewModelFactory(
    private val repository: ProjectsRepository,
    private val syncProjects: suspend () -> Unit,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ProjectsViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return ProjectsViewModel(repository, syncProjects) as T
    }
}

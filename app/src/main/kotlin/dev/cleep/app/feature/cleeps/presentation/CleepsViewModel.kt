package dev.cleep.app.feature.cleeps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.cleep.app.feature.cleeps.domain.Cleep
import dev.cleep.app.feature.cleeps.domain.CleepsRepository
import dev.cleep.app.feature.projects.domain.Project
import dev.cleep.app.feature.projects.domain.ProjectsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CleepsViewModel(
    private val repository: CleepsRepository,
    private val projectsRepository: ProjectsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CleepsUiState())
    val state: StateFlow<CleepsUiState> = _state.asStateFlow()

    suspend fun refresh() {
        val hasItems = _state.value.items.isNotEmpty()
        _state.update {
            it.copy(
                isLoading = !hasItems,
                isRefreshing = hasItems,
                errorMessage = null,
            )
        }

        val (cleepsResult, projectsResult) = coroutineScope {
            val cleepsDeferred = async { runCatching { repository.getCleeps() } }
            val projectsDeferred = async { runCatching { projectsRepository.getProjects() } }
            cleepsDeferred.await() to projectsDeferred.await()
        }

        cleepsResult.onSuccess { cleeps ->
            val projects = projectsResult.getOrDefault(emptyList())
            _state.update {
                CleepsUiState(
                    items = cleeps,
                    projects = projects,
                    selectedProjectName = resolveSelectedProjectName(
                        currentSelection = it.selectedProjectName,
                        projects = projects,
                    ),
                    errorMessage = projectsResult.exceptionOrNull()?.message,
                )
            }
        }.onFailure { error ->
            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    projects = projectsResult.getOrDefault(emptyList()),
                    selectedProjectName = resolveSelectedProjectName(
                        currentSelection = it.selectedProjectName,
                        projects = projectsResult.getOrDefault(emptyList()),
                    ),
                    errorMessage = error.message ?: "Failed to load cleeps",
                )
            }
        }
    }

    suspend fun createCleep(content: String): Result<Cleep> {
        _state.update { it.copy(isCreating = true, errorMessage = null) }
        val selectedProjectName = _state.value.selectedProjectName
        return runCatching {
            repository.createCleep(content.trim(), selectedProjectName)
        }.onSuccess { cleep ->
            _state.update {
                it.copy(
                    isCreating = false,
                    items = listOf(cleep) + it.items,
                )
            }
        }.onFailure { error ->
            _state.update {
                it.copy(
                    isCreating = false,
                    errorMessage = error.message ?: "Failed to create cleep",
                )
            }
        }
    }

    suspend fun deleteCleep(id: String): Result<Unit> {
        val previousItems = _state.value.items
        _state.update {
            it.copy(
                items = it.items.filterNot { cleep -> cleep.id == id },
                deletingIds = it.deletingIds + id,
                errorMessage = null,
            )
        }

        return runCatching {
            repository.deleteCleep(id)
        }.onSuccess {
            _state.update { it.copy(deletingIds = it.deletingIds - id) }
        }.onFailure { error ->
            _state.update {
                it.copy(
                    items = previousItems,
                    deletingIds = it.deletingIds - id,
                    errorMessage = error.message ?: "Failed to delete cleep",
                )
            }
        }
    }

    fun selectProject(projectName: String?) {
        _state.update {
            it.copy(
                selectedProjectName = resolveSelectedProjectName(
                    currentSelection = projectName,
                    projects = it.projects,
                ),
            )
        }
    }

    fun clear() {
        _state.value = CleepsUiState()
    }

    private fun resolveSelectedProjectName(
        currentSelection: String?,
        projects: List<Project>,
    ): String? {
        if (currentSelection == null) {
            return null
        }

        return projects.firstOrNull { it.name == currentSelection }?.name
    }
}

class CleepsViewModelFactory(
    private val repository: CleepsRepository,
    private val projectsRepository: ProjectsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(CleepsViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return CleepsViewModel(repository, projectsRepository) as T
    }
}

package dev.cleep.app.feature.projects.data

import dev.cleep.app.feature.projects.domain.Project
import dev.cleep.app.feature.projects.domain.ProjectsRepository
import java.time.Instant

class ProjectsRepositoryImpl(
    private val api: ProjectsApi,
) : ProjectsRepository {
    override suspend fun getProjects(): List<Project> = api.getProjects().projects.map(ProjectDto::toDomain)
}

private fun ProjectDto.toDomain(): Project = Project(
    id = id,
    name = name,
    createdAt = Instant.parse(createdAt),
)

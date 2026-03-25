package dev.cleep.app.feature.projects.data

import dev.cleep.app.feature.projects.domain.Project
import dev.cleep.app.feature.projects.domain.ProjectsRepository
import java.time.Instant
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import retrofit2.HttpException

class ProjectsRepositoryImpl(
    private val api: ProjectsApi,
) : ProjectsRepository {
    override suspend fun getProjects(): List<Project> = api.getProjects().projects.map(ProjectDto::toDomain)

    override suspend fun createProject(name: String): Project = try {
        api.createProject(ProjectUpsertRequest(name = name)).toDomain()
    } catch (exception: HttpException) {
        throw parseProjectBackendError(exception, fallbackLabel = "Create project failed")
    }

    override suspend fun updateProject(currentName: String, name: String): Project = try {
        api.updateProject(
            name = currentName.encodeForPath(),
            request = ProjectUpsertRequest(name = name),
        ).toDomain()
    } catch (exception: HttpException) {
        throw parseProjectBackendError(exception, fallbackLabel = "Update project failed")
    }

    override suspend fun deleteProject(name: String) {
        try {
            api.deleteProject(name.encodeForPath())
        } catch (exception: HttpException) {
            throw parseProjectBackendError(exception, fallbackLabel = "Delete project failed")
        }
    }
}

private fun ProjectDto.toDomain(): Project = Project(
    id = id,
    name = name,
    createdAt = Instant.parse(createdAt),
)

private fun String.encodeForPath(): String =
    URLEncoder.encode(this, StandardCharsets.UTF_8.toString()).replace("+", "%20")

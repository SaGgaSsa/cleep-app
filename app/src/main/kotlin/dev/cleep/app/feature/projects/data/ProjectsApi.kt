package dev.cleep.app.feature.projects.data

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface ProjectsApi {
    @GET("projects")
    suspend fun getProjects(): GetProjectsResponse
}

@Serializable
data class GetProjectsResponse(
    val projects: List<ProjectDto>,
    val count: Int,
)

@Serializable
data class ProjectDto(
    val id: String,
    val name: String,
    val createdAt: String,
)

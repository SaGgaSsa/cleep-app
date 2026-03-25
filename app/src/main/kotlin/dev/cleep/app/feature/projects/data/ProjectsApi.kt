package dev.cleep.app.feature.projects.data

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProjectsApi {
    @GET("projects")
    suspend fun getProjects(): GetProjectsResponse

    @POST("projects")
    suspend fun createProject(@Body request: ProjectUpsertRequest): ProjectDto

    @PUT("projects/{name}")
    suspend fun updateProject(
        @Path("name", encoded = true) name: String,
        @Body request: ProjectUpsertRequest,
    ): ProjectDto

    @DELETE("projects/{name}")
    suspend fun deleteProject(@Path("name", encoded = true) name: String)
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

@Serializable
data class ProjectUpsertRequest(
    val name: String,
)

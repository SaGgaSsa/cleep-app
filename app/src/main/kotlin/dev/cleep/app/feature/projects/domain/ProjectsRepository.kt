package dev.cleep.app.feature.projects.domain

interface ProjectsRepository {
    suspend fun getProjects(): List<Project>
    suspend fun createProject(name: String): Project
    suspend fun updateProject(currentName: String, name: String): Project
    suspend fun deleteProject(name: String)
}

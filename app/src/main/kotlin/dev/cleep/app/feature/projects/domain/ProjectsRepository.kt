package dev.cleep.app.feature.projects.domain

interface ProjectsRepository {
    suspend fun getProjects(): List<Project>
}

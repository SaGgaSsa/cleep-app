package dev.cleep.app.feature.projects.domain

import java.time.Instant

data class Project(
    val id: String,
    val name: String,
    val createdAt: Instant,
)

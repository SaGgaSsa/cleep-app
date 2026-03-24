package dev.cleep.app.feature.cleeps.domain

import java.time.Instant

data class Cleep(
    val id: String,
    val userId: String,
    val content: String,
    val projectName: String? = null,
    val createdAt: Instant,
)

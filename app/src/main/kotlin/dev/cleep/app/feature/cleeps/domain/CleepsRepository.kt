package dev.cleep.app.feature.cleeps.domain

interface CleepsRepository {
    suspend fun getCleeps(): List<Cleep>
    suspend fun createCleep(content: String): Cleep
    suspend fun deleteCleep(id: String)
}

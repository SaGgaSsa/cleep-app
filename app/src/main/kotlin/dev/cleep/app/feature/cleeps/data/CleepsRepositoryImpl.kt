package dev.cleep.app.feature.cleeps.data

import dev.cleep.app.feature.cleeps.domain.Cleep
import dev.cleep.app.feature.cleeps.domain.CleepsRepository
import java.time.Instant

class CleepsRepositoryImpl(
    private val api: CleepsApi,
) : CleepsRepository {
    override suspend fun getCleeps(): List<Cleep> = api.getCleeps()
        .cleeps
        .map(CleepDto::toDomain)
        .sortedByDescending(Cleep::createdAt)

    override suspend fun createCleep(content: String, projectName: String?): Cleep =
        api.createCleep(
            CreateCleepRequest(
                content = content,
                project = projectName,
            ),
        ).toDomain()

    override suspend fun deleteCleep(id: String) {
        api.deleteCleep(DeleteCleepRequest(ids = listOf(id)))
    }
}

private fun CleepDto.toDomain(): Cleep = Cleep(
    id = id,
    userId = userId,
    content = content,
    projectName = project?.name?.trim()?.takeIf(String::isNotEmpty),
    createdAt = Instant.parse(createdAt),
)

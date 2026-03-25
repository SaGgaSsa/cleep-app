package dev.cleep.app.feature.cleeps.data

import dev.cleep.app.feature.cleeps.domain.Cleep
import dev.cleep.app.feature.cleeps.domain.CleepsRepository
import java.time.Instant
import retrofit2.HttpException

class CleepsRepositoryImpl(
    private val api: CleepsApi,
) : CleepsRepository {
    override suspend fun getCleeps(): List<Cleep> = api.getCleeps()
        .cleeps
        .map(CleepDto::toDomain)
        .sortedByDescending(Cleep::createdAt)

    override suspend fun createCleep(content: String, projectName: String?): Cleep = try {
        api.createCleep(
            CreateCleepRequest(
                content = content,
                project = projectName,
            ),
        ).toDomain()
    } catch (exception: HttpException) {
        throw parseCleepBackendError(exception, fallbackLabel = "Create failed")
    }

    override suspend fun deleteCleep(id: String) {
        try {
            api.deleteCleep(DeleteCleepRequest(ids = listOf(id)))
        } catch (exception: HttpException) {
            throw parseCleepBackendError(exception, fallbackLabel = "Delete failed")
        }
    }
}

private fun CleepDto.toDomain(): Cleep = Cleep(
    id = id,
    userId = userId,
    content = content,
    projectName = project?.name?.trim()?.takeIf(String::isNotEmpty),
    createdAt = Instant.parse(createdAt),
)

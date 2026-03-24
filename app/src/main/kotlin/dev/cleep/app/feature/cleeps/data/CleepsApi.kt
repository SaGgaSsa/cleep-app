package dev.cleep.app.feature.cleeps.data

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface CleepsApi {
    @GET("cleeps")
    suspend fun getCleeps(): GetCleepsResponse

    @POST("cleeps")
    suspend fun createCleep(@Body request: CreateCleepRequest): CleepDto

    @DELETE("cleeps")
    suspend fun deleteCleep(@Body request: DeleteCleepRequest)
}

@Serializable
data class GetCleepsResponse(
    val cleeps: List<CleepDto>,
)

@Serializable
data class CreateCleepRequest(
    val content: String,
)

@Serializable
data class DeleteCleepRequest(
    val ids: List<String>,
)

@Serializable
data class CleepDto(
    val id: String,
    val userId: String,
    val content: String,
    val createdAt: String,
)

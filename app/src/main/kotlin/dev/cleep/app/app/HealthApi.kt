package dev.cleep.app.app

import retrofit2.Response
import retrofit2.http.GET

interface HealthApi {
    @GET("health")
    suspend fun health(): Response<Unit>
}

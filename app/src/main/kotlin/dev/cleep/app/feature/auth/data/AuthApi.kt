package dev.cleep.app.feature.auth.data

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}

@Serializable
data class RegisterRequest(
    val email: String,
    val idToken: String,
)

@Serializable
data class RegisterResponse(
    val apiKey: String,
)

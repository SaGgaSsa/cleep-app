package dev.cleep.app.feature.auth.data

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("auth/status")
    suspend fun authStatus(@Query("state") state: String): AuthStatusResponse
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

@Serializable
data class AuthStatusResponse(
    val apiKey: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
)

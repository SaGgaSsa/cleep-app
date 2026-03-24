package dev.cleep.app.feature.auth.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("auth/status")
    suspend fun authStatus(@Query("state") state: String): retrofit2.Response<AuthStatusResponse>
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
@OptIn(ExperimentalSerializationApi::class)
data class AuthStatusResponse(
    val status: String? = null,
    val apiKey: String? = null,
    val email: String? = null,
    @JsonNames("name", "login", "username", "userName")
    val displayName: String? = null,
    @JsonNames("avatarUrl", "avatar_url", "image", "picture", "photo", "photoURL")
    val photoUrl: String? = null,
)

package dev.cleep.app.app

import android.os.SystemClock
import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.cleep.app.BuildConfig
import dev.cleep.app.feature.auth.data.AuthApi
import dev.cleep.app.feature.auth.data.AuthRepositoryImpl
import dev.cleep.app.feature.auth.data.GoogleAuthClient
import dev.cleep.app.feature.auth.data.SessionStorage
import dev.cleep.app.feature.cleeps.data.CleepsApi
import dev.cleep.app.feature.cleeps.data.CleepsRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit

class AppContainer(context: Context) {
    private val sessionStorage = SessionStorage(context)

    private val authHeaderInterceptor = okhttp3.Interceptor { chain ->
        val apiKey = sessionStorage.readApiKey()
        val request = if (apiKey.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request()
                .newBuilder()
                .header("Authorization", "Bearer $apiKey")
                .build()
        }
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authHeaderInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL.ensureTrailingSlash())
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val authRepository = AuthRepositoryImpl(
        authApi = retrofit.create(AuthApi::class.java),
        sessionStorage = sessionStorage,
        googleAuthClient = GoogleAuthClient(context),
    )

    val cleepsRepository = CleepsRepositoryImpl(
        api = retrofit.create(CleepsApi::class.java),
    )

    private val healthApi = retrofit.create(HealthApi::class.java)

    suspend fun awaitBackendWarmup(
        maxWaitMillis: Long = 50_000,
        retryDelayMillis: Long = 5_000,
    ): Result<Unit> {
        val deadline = SystemClock.elapsedRealtime() + maxWaitMillis
        var lastError: Throwable? = null

        while (SystemClock.elapsedRealtime() < deadline) {
            try {
                val response = healthApi.health()
                if (response.isSuccessful) {
                    return Result.success(Unit)
                }
                lastError = IllegalStateException("Health check failed (${response.code()})")
            } catch (error: Throwable) {
                lastError = error
            }

            val remaining = deadline - SystemClock.elapsedRealtime()
            if (remaining > 0) {
                delay(minOf(retryDelayMillis, remaining))
            }
        }

        return Result.failure(
            lastError ?: IllegalStateException("Backend warmup timed out after ${maxWaitMillis / 1000} seconds"),
        )
    }
}

private fun String.ensureTrailingSlash(): String = if (endsWith("/")) this else "$this/"

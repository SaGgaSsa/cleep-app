package dev.cleep.app.app

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
}

private fun String.ensureTrailingSlash(): String = if (endsWith("/")) this else "$this/"

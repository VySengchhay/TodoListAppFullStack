package com.androidapp.todolistapplication.core.network

import com.androidapp.todolistapplication.core.datastore.SessionManager
import com.androidapp.todolistapplication.feature.auth.data.remote.AuthApiService
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
) : Authenticator {
    private val refreshApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.endsWith(ApiConstants.REFRESH)) {
            return null
        }

        if (responseCount(response) >= 2) {
            return null
        }

        return synchronized(lock) {
            runBlocking {
                val currentAccessToken = sessionManager.getAccessToken()
                val failedRequestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                if (currentAccessToken != null && currentAccessToken != failedRequestToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                val refreshToken = sessionManager.getRefreshToken() ?: run {
                    sessionManager.clearSession()
                    return@runBlocking null
                }

                val result = runCatching {
                    refreshApi.refresh(RefreshRequestDto(refreshToken))
                }.getOrNull()

                if (result == null) {
                    sessionManager.clearSession()
                    return@runBlocking null
                }

                sessionManager.saveSession(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    userId = sessionManager.getUserId().orEmpty()
                )

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${result.accessToken}")
                    .build()
            }
        }
    }

    private fun responseCount(response: okhttp3.Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

}
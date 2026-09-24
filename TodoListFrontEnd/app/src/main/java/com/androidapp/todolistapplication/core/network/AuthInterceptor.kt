package com.androidapp.todolistapplication.core.network

import com.androidapp.todolistapplication.core.datastore.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (isPublicPath(original.url.encodedPath)) {
            return chain.proceed(original)
        }

        val token = runBlocking { sessionManager.getAccessToken() }
        val request = if (token != null) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }

    private fun isPublicPath(path: String): Boolean =
        path.endsWith(ApiConstants.REGISTER) ||
                path.endsWith(ApiConstants.LOGIN) ||
                path.endsWith(ApiConstants.REFRESH) ||
                path.endsWith(ApiConstants.PASSWORD_RESET_REQUEST) ||
                path.endsWith(ApiConstants.PASSWORD_RESET_VERIFY) ||
                path.endsWith(ApiConstants.PASSWORD_RESET_CONFIRM)
}
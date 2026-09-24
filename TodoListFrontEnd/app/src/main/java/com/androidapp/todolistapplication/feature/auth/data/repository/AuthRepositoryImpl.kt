package com.androidapp.todolistapplication.feature.auth.data.repository

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.datastore.SessionManager
import com.androidapp.todolistapplication.core.network.NetworkErrorMapper
import com.androidapp.todolistapplication.feature.auth.data.remote.AuthApiService
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.LoginRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.LogoutRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.PasswordResetRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.PasswordResetVerifyRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.RegisterRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.PasswordResetConfirmDto
import com.androidapp.todolistapplication.feature.auth.data.remote.mapper.toDomain
import com.androidapp.todolistapplication.feature.auth.domain.model.AuthTokens
import com.androidapp.todolistapplication.feature.auth.domain.model.User
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String
    ): AppResult<Unit> = safeCall {
        authApiService.register(RegisterRequestDto(email, password))
    }

    override suspend fun login(
        email: String,
        password: String
    ): AppResult<AuthTokens> = safeCall {
        val response = authApiService.login(LoginRequestDto(email, password))
        val tokens = response.toDomain()

        sessionManager.saveSession(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            userId = ""
        )

        val me = try {
            authApiService.me()
        } catch (e: Exception) {
            sessionManager.clearSession()
            throw e
        }

        sessionManager.saveSession(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            userId = me.id
        )

        tokens
    }

    override suspend fun logout(): AppResult<Unit> = safeCall {
        val refreshToken = sessionManager.getRefreshToken()
        if (refreshToken != null) {
            runCatching { authApiService.logout(LogoutRequestDto(refreshToken)) }
        }
        sessionManager.clearSession()
    }

    override suspend fun requestPasswordReset(email: String): AppResult<Unit> = safeCall {
        authApiService.requestPasswordReset(PasswordResetRequestDto(email))
    }

    override suspend fun verifyPasswordResetOtp(
        email: String,
        otp: String
    ): AppResult<Unit> = safeCall {
        authApiService.verifyPasswordResetOtp(PasswordResetVerifyRequestDto(email, otp))
    }

    override suspend fun confirmPasswordReset(
        email: String,
        otp: String,
        newPassword: String
    ): AppResult<Unit> = safeCall {
        authApiService.confirmPasswordReset(PasswordResetConfirmDto(email, otp, newPassword))
    }

    override suspend fun getCurrentUser(): AppResult<User> = safeCall {
        authApiService.me().toDomain()
    }


    private suspend inline fun <T> safeCall(block: () -> T): AppResult<T> =
        try {
            AppResult.Success(block())
        } catch (e: Exception) {
            AppResult.Error(NetworkErrorMapper.toAppError(e))
        }
}
package com.androidapp.todolistapplication.feature.auth.domain.repository

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.model.AuthTokens
import com.androidapp.todolistapplication.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String,
    ) : AppResult<Unit>

    suspend fun login(
        email: String,
        password: String
    ) : AppResult<AuthTokens>

    suspend fun logout() : AppResult<Unit>

    suspend fun requestPasswordReset(
        email: String
    ) : AppResult<Unit>

    suspend fun verifyPasswordResetOtp(
        email: String,
        otp: String
    ) : AppResult<Unit>

    suspend fun confirmPasswordReset(
        email: String,
        otp: String,
        newPassword: String
    ) : AppResult<Unit>

    suspend fun getCurrentUser() : AppResult<User>
}
package com.androidapp.todolistapplication.fake

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.model.AuthTokens
import com.androidapp.todolistapplication.feature.auth.domain.model.User
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.delay
class FakeAuthRepository : AuthRepository {

    var registerResult: AppResult<Unit> = AppResult.Success(Unit)
    var loginResult: AppResult<AuthTokens> =
        AppResult.Success(AuthTokens(accessToken = "access", refreshToken = "refresh", expiresInSeconds = 3600))
    var logoutResult: AppResult<Unit> = AppResult.Success(Unit)
    var requestPasswordResetResult: AppResult<Unit> = AppResult.Success(Unit)
    var verifyPasswordResetOtpResult: AppResult<Unit> = AppResult.Success(Unit)
    var confirmPasswordResetResult: AppResult<Unit> = AppResult.Success(Unit)
    var currentUserResult: AppResult<User> = AppResult.Success(User(id = "user-1", email = "me@example.com"))

    var delayMillis: Long = 0
    var exceptionToThrow: Exception? = null

    data class Credentials(val email: String, val password: String)
    data class VerifyCall(val email: String, val otp: String)
    data class ConfirmCall(val email: String, val otp: String, val newPassword: String)

    val registerCalls = mutableListOf<Credentials>()
    val loginCalls = mutableListOf<Credentials>()
    var logoutCalls = 0
        private set
    val requestPasswordResetCalls = mutableListOf<String>()
    val verifyPasswordResetOtpCalls = mutableListOf<VerifyCall>()
    val confirmPasswordResetCalls = mutableListOf<ConfirmCall>()

    override suspend fun register(email: String, password: String): AppResult<Unit> {
        registerCalls += Credentials(email, password)
        return respond(registerResult)
    }

    override suspend fun login(email: String, password: String): AppResult<AuthTokens> {
        loginCalls += Credentials(email, password)
        return respond(loginResult)
    }

    override suspend fun logout(): AppResult<Unit> {
        logoutCalls++
        return respond(logoutResult)
    }

    override suspend fun requestPasswordReset(email: String): AppResult<Unit> {
        requestPasswordResetCalls += email
        return respond(requestPasswordResetResult)
    }

    override suspend fun verifyPasswordResetOtp(email: String, otp: String): AppResult<Unit> {
        verifyPasswordResetOtpCalls += VerifyCall(email, otp)
        return respond(verifyPasswordResetOtpResult)
    }

    override suspend fun confirmPasswordReset(email: String, otp: String, newPassword: String): AppResult<Unit> {
        confirmPasswordResetCalls += ConfirmCall(email, otp, newPassword)
        return respond(confirmPasswordResetResult)
    }

    override suspend fun getCurrentUser(): AppResult<User> = respond(currentUserResult)

    private suspend fun <T> respond(result: AppResult<T>): AppResult<T> {
        if (delayMillis > 0) delay(delayMillis)
        exceptionToThrow?.let { throw it }
        return result
    }
}

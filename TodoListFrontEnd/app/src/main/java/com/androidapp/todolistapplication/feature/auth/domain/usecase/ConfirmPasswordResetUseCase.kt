package com.androidapp.todolistapplication.feature.auth.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ConfirmPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseUseCase<ConfirmPasswordResetUseCase.Params, Unit>(ioDispatcher) {
    data class Params(
        val email: String,
        val otp: String,
        val newPassword: String
    )

    override suspend fun execute(params: Params): AppResult<Unit> =
        authRepository.confirmPasswordReset(
            email = params.email,
            otp = params.otp,
            newPassword = params.newPassword
        )

}
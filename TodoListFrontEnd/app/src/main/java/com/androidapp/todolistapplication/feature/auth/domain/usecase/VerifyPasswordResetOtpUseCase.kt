package com.androidapp.todolistapplication.feature.auth.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.core.common.IoDispatcher
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class VerifyPasswordResetOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseUseCase<VerifyPasswordResetOtpUseCase.Params, Unit>(ioDispatcher) {
    data class Params(
        val email: String,
        val otp: String
    )

    override suspend fun execute(params: Params): AppResult<Unit> =
        authRepository.verifyPasswordResetOtp(
            email = params.email,
            otp = params.otp
        )
}

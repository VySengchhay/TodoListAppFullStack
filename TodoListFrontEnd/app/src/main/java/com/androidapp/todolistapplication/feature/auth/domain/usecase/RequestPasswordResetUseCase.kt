package com.androidapp.todolistapplication.feature.auth.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import jakarta.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseUseCase<RequestPasswordResetUseCase.Params, Unit>(ioDispatcher) {
    data class Params(
        val email: String,
    )

    override suspend fun execute(params: Params): AppResult<Unit> = authRepository.requestPasswordReset(params.email)
}
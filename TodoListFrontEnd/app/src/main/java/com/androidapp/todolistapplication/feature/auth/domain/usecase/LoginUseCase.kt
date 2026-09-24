package com.androidapp.todolistapplication.feature.auth.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.feature.auth.domain.model.AuthTokens
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseUseCase<LoginUseCase.Params, AuthTokens>(ioDispatcher) {
    data class Params(
        val email: String,
        val password: String
    )

    override suspend fun execute(params: Params): AppResult<AuthTokens> {
        return authRepository.login(params.email, params.password)
    }
}
package com.androidapp.todolistapplication.feature.auth.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import com.androidapp.todolistapplication.core.common.BaseNoParamsUseCase
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseNoParamsUseCase<Unit>(ioDispatcher) {
    override suspend fun execute(): AppResult<Unit> = authRepository.logout()
}
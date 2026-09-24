package com.androidapp.todolistapplication.feature.auth.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import com.androidapp.todolistapplication.core.common.BaseNoParamsUseCase
import com.androidapp.todolistapplication.feature.auth.domain.model.User
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseNoParamsUseCase<User>(ioDispatcher) {
    override suspend fun execute(): AppResult<User> = authRepository.getCurrentUser()
}
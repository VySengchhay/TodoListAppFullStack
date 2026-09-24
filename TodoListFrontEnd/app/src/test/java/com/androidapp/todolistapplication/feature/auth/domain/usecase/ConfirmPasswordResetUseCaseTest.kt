package com.androidapp.todolistapplication.feature.auth.domain.usecase

import com.androidapp.todolistapplication.core.common.AppError
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.fake.FakeAuthRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ConfirmPasswordResetUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeAuthRepository()
    private val useCase = ConfirmPasswordResetUseCase(repository, dispatcher)

    private val params = ConfirmPasswordResetUseCase.Params(
        email = "me@example.com",
        otp = "123456",
        newPassword = "Abcdefg1"
    )

    @Test
    fun `passes every param through to the repository`() = runTest(dispatcher) {
        useCase(params)

        assertEquals(
            listOf(FakeAuthRepository.ConfirmCall("me@example.com", "123456", "Abcdefg1")),
            repository.confirmPasswordResetCalls
        )
    }

    @Test
    fun `returns the repository result unchanged`() = runTest(dispatcher) {
        repository.confirmPasswordResetResult = AppResult.Error(AppError.BadRequest("Invalid or expired OTP"))

        assertEquals(AppResult.Error(AppError.BadRequest("Invalid or expired OTP")), useCase(params))
    }

    @Test
    fun `BaseUseCase turns an unexpected exception into AppError Unknown`() = runTest(dispatcher) {
        repository.exceptionToThrow = IllegalStateException("database on fire")

        assertEquals(AppResult.Error(AppError.Unknown("database on fire")), useCase(params))
    }
}

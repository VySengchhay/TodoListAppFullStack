package com.androidapp.todolistapplication.feature.auth.presentation.resetpassword

import com.androidapp.todolistapplication.core.common.AppError
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.ConfirmPasswordResetUseCase
import com.androidapp.todolistapplication.feature.auth.fake.FakeAuthRepository
import com.androidapp.todolistapplication.testutil.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ResetPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAuthRepository
    private lateinit var viewModel: ResetPasswordViewModel

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
        viewModel = ResetPasswordViewModel(
            confirmPasswordResetUseCase = ConfirmPasswordResetUseCase(repository, mainDispatcherRule.testDispatcher),
            email = EMAIL,
            otp = OTP
        )
    }

    private fun enterPasswords(newPassword: String, confirm: String = newPassword) {
        viewModel.onNewPasswordChange(newPassword)
        viewModel.onConfirmPasswordChange(confirm)
    }

    @Test
    fun `sends email, otp and new password to the backend`() = runTest {
        enterPasswords("Abcdefg1")

        viewModel.onResetClick()
        advanceUntilIdle()

        assertEquals(
            listOf(FakeAuthRepository.ConfirmCall(EMAIL, OTP, "Abcdefg1")),
            repository.confirmPasswordResetCalls
        )
    }

    @Test
    fun `weak password is never sent`() = runTest {
        enterPasswords("short1")

        viewModel.onResetClick()
        advanceUntilIdle()

        assertTrue(repository.confirmPasswordResetCalls.isEmpty())
    }

    @Test
    fun `mismatched passwords are never sent`() = runTest {
        enterPasswords("Abcdefg1", confirm = "Abcdefg2")

        viewModel.onResetClick()
        advanceUntilIdle()

        assertTrue(repository.confirmPasswordResetCalls.isEmpty())
    }

    @Test
    fun `success marks the reset as done`() = runTest {
        enterPasswords("Abcdefg1")

        viewModel.onResetClick()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isResetSuccessful)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `invalid otp shows the backend message and stays on the screen`() = runTest {
        repository.confirmPasswordResetResult = AppResult.Error(AppError.BadRequest("Invalid or expired OTP"))
        enterPasswords("Abcdefg1")

        viewModel.onResetClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Invalid or expired OTP", state.errorMessage)
        assertFalse(state.isResetSuccessful)
        assertFalse(state.isLoading)
    }

    @Test
    fun `double tapping reset only sends one request`() = runTest {
        repository.delayMillis = 1_000
        enterPasswords("Abcdefg1")

        viewModel.onResetClick()
        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        viewModel.onResetClick()
        advanceUntilIdle()

        assertEquals(1, repository.confirmPasswordResetCalls.size)
    }

    @Test
    fun `toggle flips password visibility`() {
        viewModel.onTogglePasswordVisibility()
        assertTrue(viewModel.uiState.value.isPasswordVisible)

        viewModel.onTogglePasswordVisibility()
        assertFalse(viewModel.uiState.value.isPasswordVisible)
    }

    private companion object {
        const val EMAIL = "me@example.com"
        const val OTP = "123456"
    }
}

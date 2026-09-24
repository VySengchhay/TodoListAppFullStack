package com.androidapp.todolistapplication.feature.auth.presentation.otpverify

import com.androidapp.todolistapplication.core.common.AppError
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.RequestPasswordResetUseCase
import com.androidapp.todolistapplication.feature.auth.domain.usecase.VerifyPasswordResetOtpUseCase
import com.androidapp.todolistapplication.feature.auth.fake.FakeAuthRepository
import com.androidapp.todolistapplication.testutil.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OtpVerifyViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAuthRepository
    private lateinit var viewModel: OtpVerifyViewModel

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
        // Assisted injection is just a constructor argument in a test.
        viewModel = OtpVerifyViewModel(
            requestPasswordResetUseCase = RequestPasswordResetUseCase(repository, mainDispatcherRule.testDispatcher),
            verifyPasswordResetOtpUseCase = VerifyPasswordResetOtpUseCase(repository, mainDispatcherRule.testDispatcher),
            email = EMAIL
        )
    }

    @Test
    fun `starts with the email from the route`() {
        assertEquals(EMAIL, viewModel.uiState.value.email)
    }

    @Test
    fun `code input keeps digits only, max six`() {
        viewModel.onOtpChange("12ab345678")

        assertEquals("123456", viewModel.uiState.value.otp)
        assertTrue(viewModel.uiState.value.isOtpValid)
    }

    @Test
    fun `resend works before any code is typed`() = runTest {
        viewModel.onResendClick()
        advanceUntilIdle()

        assertEquals(listOf(EMAIL), repository.requestPasswordResetCalls)
    }

    @Test
    fun `resend shows Sending then re-enables with a confirmation message`() = runTest {
        repository.delayMillis = 1_000

        viewModel.onResendClick()
        runCurrent()
        assertTrue(viewModel.uiState.value.isResending)

        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isResending)
        assertEquals("Code sent to $EMAIL", state.resendMessage)
    }

    @Test
    fun `double tapping resend only sends one request`() = runTest {
        repository.delayMillis = 1_000

        viewModel.onResendClick()
        runCurrent()
        viewModel.onResendClick()
        advanceUntilIdle()

        assertEquals(1, repository.requestPasswordResetCalls.size)
    }

    @Test
    fun `resend failure shows the error and re-enables the button`() = runTest {
        repository.requestPasswordResetResult = AppResult.Error(AppError.Timeout)

        viewModel.onResendClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Request timed out. Please try again", state.errorMessage)
        assertFalse(state.isResending)
        assertNull(state.resendMessage)
    }

    @Test
    fun `continue checks the code with the backend`() = runTest {
        viewModel.onOtpChange("123456")

        viewModel.onContinueClick()
        advanceUntilIdle()

        assertEquals(listOf(FakeAuthRepository.VerifyCall(EMAIL, "123456")), repository.verifyPasswordResetOtpCalls)
        assertTrue(viewModel.uiState.value.isOtpVerified)
    }

    @Test
    fun `a wrong code shows the error and does not move on`() = runTest {
        repository.verifyPasswordResetOtpResult = AppResult.Error(AppError.BadRequest("Invalid or expired OTP"))
        viewModel.onOtpChange("111111")

        viewModel.onContinueClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Invalid or expired OTP", state.errorMessage)
        assertFalse(state.isOtpVerified)
        assertFalse(state.isVerifying)
        // The code stays so the user can fix one digit.
        assertEquals("111111", state.otp)
    }

    @Test
    fun `an incomplete code is never sent`() = runTest {
        viewModel.onOtpChange("123")

        viewModel.onContinueClick()
        advanceUntilIdle()

        assertTrue(repository.verifyPasswordResetOtpCalls.isEmpty())
    }

    @Test
    fun `double tapping continue only sends one request`() = runTest {
        repository.delayMillis = 1_000
        viewModel.onOtpChange("123456")

        viewModel.onContinueClick()
        runCurrent()
        assertTrue(viewModel.uiState.value.isVerifying)
        viewModel.onContinueClick()
        advanceUntilIdle()

        assertEquals(1, repository.verifyPasswordResetOtpCalls.size)
    }

    @Test
    fun `consumeOtpVerified resets the flag so coming back does not re-navigate`() = runTest {
        viewModel.onOtpChange("123456")
        viewModel.onContinueClick()
        advanceUntilIdle()

        viewModel.consumeOtpVerified()

        assertFalse(viewModel.uiState.value.isOtpVerified)
    }

    @Test
    fun `consuming messages clears them`() = runTest {
        viewModel.onResendClick()
        advanceUntilIdle()

        viewModel.consumeResendMessage()
        viewModel.consumeErrorMessage()

        assertNull(viewModel.uiState.value.resendMessage)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    private companion object {
        const val EMAIL = "me@example.com"
    }
}

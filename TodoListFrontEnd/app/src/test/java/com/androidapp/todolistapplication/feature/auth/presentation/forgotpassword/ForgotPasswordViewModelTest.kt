package com.androidapp.todolistapplication.feature.auth.presentation.forgotpassword

import com.androidapp.todolistapplication.core.common.AppError
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.RequestPasswordResetUseCase
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

class ForgotPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAuthRepository
    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
        viewModel = ForgotPasswordViewModel(
            RequestPasswordResetUseCase(repository, mainDispatcherRule.testDispatcher)
        )
    }

    @Test
    fun `send code does nothing while the email is invalid`() = runTest {
        viewModel.onEmailChange("not-an-email")

        viewModel.onSendCodeClick()
        advanceUntilIdle()

        assertTrue(repository.requestPasswordResetCalls.isEmpty())
        assertFalse(viewModel.uiState.value.isRequestSuccess)
    }

    @Test
    fun `send code trims the email before calling the backend`() = runTest {
        viewModel.onEmailChange("  me@example.com  ")

        viewModel.onSendCodeClick()
        advanceUntilIdle()

        assertEquals(listOf("me@example.com"), repository.requestPasswordResetCalls)
    }

    @Test
    fun `shows loading while the request is in flight`() = runTest {
        repository.delayMillis = 1_000
        viewModel.onEmailChange("me@example.com")

        viewModel.onSendCodeClick()
        runCurrent() // start the coroutine, but don't skip past the fake delay

        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle() // now let the delay finish

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `success marks the request as sent`() = runTest {
        viewModel.onEmailChange("me@example.com")

        viewModel.onSendCodeClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isRequestSuccess)
        assertNull(state.errorMessage)
    }

    @Test
    fun `failure shows the backend message and does not navigate`() = runTest {
        repository.requestPasswordResetResult = AppResult.Error(AppError.TooManyRequests("Slow down"))
        viewModel.onEmailChange("me@example.com")

        viewModel.onSendCodeClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Slow down", state.errorMessage)
        assertFalse(state.isRequestSuccess)
        assertFalse(state.isLoading)
    }

    @Test
    fun `consumeRequestSuccess resets the flag so coming back does not re-navigate`() = runTest {
        viewModel.onEmailChange("me@example.com")
        viewModel.onSendCodeClick()
        advanceUntilIdle()

        viewModel.consumeRequestSuccess()

        assertFalse(viewModel.uiState.value.isRequestSuccess)
    }

    @Test
    fun `typing clears the previous error`() = runTest {
        repository.requestPasswordResetResult = AppResult.Error(AppError.NoInternet)
        viewModel.onEmailChange("me@example.com")
        viewModel.onSendCodeClick()
        advanceUntilIdle()

        viewModel.onEmailChange("me@example.org")

        assertNull(viewModel.uiState.value.errorMessage)
    }
}

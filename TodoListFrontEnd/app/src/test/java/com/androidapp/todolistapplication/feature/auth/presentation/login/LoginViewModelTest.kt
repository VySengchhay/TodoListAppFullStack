package com.androidapp.todolistapplication.feature.auth.presentation.login

import com.androidapp.todolistapplication.core.common.AppError
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.LoginUseCase
import com.androidapp.todolistapplication.feature.auth.fake.FakeAuthRepository
import com.androidapp.todolistapplication.testutil.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
        viewModel = LoginViewModel(LoginUseCase(repository, mainDispatcherRule.testDispatcher))
    }

    @Test
    fun `blank form does not call the backend`() = runTest {
        viewModel.onLoginClick()
        advanceUntilIdle()

        assertTrue(repository.loginCalls.isEmpty())
    }

    @Test
    fun `trims the email but not the password`() = runTest {
        viewModel.onEmailChange("  me@example.com ")
        viewModel.onPasswordChange(" secret ")

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals(listOf(FakeAuthRepository.Credentials("me@example.com", " secret ")), repository.loginCalls)
    }

    @Test
    fun `success marks login as done`() = runTest {
        viewModel.onEmailChange("me@example.com")
        viewModel.onPasswordChange("Password123")

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoginSuccessful)
    }

    @Test
    fun `wrong password shows the backend message`() = runTest {
        repository.loginResult = AppResult.Error(AppError.Unauthorized("Invalid email or password"))
        viewModel.onEmailChange("me@example.com")
        viewModel.onPasswordChange("wrong")

        viewModel.onLoginClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Invalid email or password", state.errorMessage)
        assertFalse(state.isLoginSuccessful)
        assertFalse(state.isLoading)
    }
}

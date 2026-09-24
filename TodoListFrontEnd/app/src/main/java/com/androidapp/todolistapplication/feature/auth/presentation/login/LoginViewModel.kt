package com.androidapp.todolistapplication.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onLoginClick() {
        val state = _uiState.value
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            when (
                val result = loginUseCase(
                    LoginUseCase.Params(email = state.email.trim(), password = state.password)
                )
            ) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoginSuccessful = true)
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.error.userMessage()
                    )
                }
            }
        }
    }

    fun consumeErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
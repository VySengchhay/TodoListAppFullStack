package com.androidapp.todolistapplication.feature.auth.presentation.register

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = null)
    }

    fun onTogglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
    }

    fun onRegisterClick() {
        val state = _uiState.value
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            when (val result = registerUseCase(
                RegisterUseCase.Params(email = state.email.trim(), password = state.password)
            )) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegisterSuccessful = true
                    )
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
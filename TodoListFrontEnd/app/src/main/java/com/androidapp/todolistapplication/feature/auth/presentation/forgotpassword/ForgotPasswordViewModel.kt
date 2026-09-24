package com.androidapp.todolistapplication.feature.auth.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.RequestPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val passwordResetUseCase: RequestPasswordResetUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(email = value, errorMessage = null)
        }
    }

    fun onSendCodeClick() {
        val state = _uiState.value
        if (!state.isFormValid) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            when (val result = passwordResetUseCase(
                RequestPasswordResetUseCase.Params(email = state.email.trim()))) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isRequestSuccess = true) }
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.error.userMessage()) }
                }
            }
        }
    }

    fun consumeErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeRequestSuccess() {
        _uiState.update { it.copy(isRequestSuccess = false) }
    }
}
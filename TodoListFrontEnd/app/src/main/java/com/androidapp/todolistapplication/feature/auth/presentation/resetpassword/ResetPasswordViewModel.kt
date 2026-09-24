package com.androidapp.todolistapplication.feature.auth.presentation.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.ConfirmPasswordResetUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ResetPasswordViewModel.Factory::class)
class ResetPasswordViewModel @AssistedInject constructor(
    private val confirmPasswordResetUseCase: ConfirmPasswordResetUseCase,
    @Assisted("email") email: String,
    @Assisted("otp") otp: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("email") email: String, @Assisted("otp") otp: String): ResetPasswordViewModel
    }

    private val _uiState = MutableStateFlow(ResetPasswordUiState(email = email, otp = otp))
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                newPassword = value,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                confirmPassword = value,
                errorMessage = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun onResetClick() {
        val state = _uiState.value
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            when (
                val result = confirmPasswordResetUseCase(
                    ConfirmPasswordResetUseCase.Params(
                        email = state.email,
                        otp = state.otp,
                        newPassword = state.newPassword
                    )
                )
            ) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isResetSuccessful = true) }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.error.userMessage())
                    }
                }
            }
        }
    }

    fun consumeErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

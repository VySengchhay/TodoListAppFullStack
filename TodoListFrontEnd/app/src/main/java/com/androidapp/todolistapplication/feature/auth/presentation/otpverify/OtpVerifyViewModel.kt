package com.androidapp.todolistapplication.feature.auth.presentation.otpverify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.RequestPasswordResetUseCase
import com.androidapp.todolistapplication.feature.auth.domain.usecase.VerifyPasswordResetOtpUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = OtpVerifyViewModel.Factory::class)
class OtpVerifyViewModel @AssistedInject constructor(
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
    private val verifyPasswordResetOtpUseCase: VerifyPasswordResetOtpUseCase,
    @Assisted private val email: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(email: String): OtpVerifyViewModel
    }

    private val _uiState = MutableStateFlow(OtpVerifyUiState(email = email))
    val uiState: StateFlow<OtpVerifyUiState> = _uiState.asStateFlow()

    fun onOtpChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(OtpVerifyUiState.OTP_LENGTH)
        _uiState.update {
            it.copy(
                otp = digitsOnly,
                errorMessage = null
            )
        }
    }

    fun onContinueClick() {
        val state = _uiState.value
        if (!state.isOtpValid || state.isVerifying) return

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, errorMessage = null) }

            when (val result = verifyPasswordResetOtpUseCase(
                VerifyPasswordResetOtpUseCase.Params(email = email, otp = state.otp))) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isVerifying = false, isOtpVerified = true) }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(isVerifying = false, errorMessage = result.error.userMessage())
                    }
                }
            }
        }
    }

    fun onResendClick() {
        if (_uiState.value.isResending) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isResending = true, errorMessage = null)
            }

            when (val result = requestPasswordResetUseCase(
                RequestPasswordResetUseCase.Params(email = email))) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isResending = false, resendMessage = "Code sent to $email")
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isResending = false,
                            errorMessage = result.error.userMessage()
                        )
                    }
                }
            }
        }
    }

    fun consumeOtpVerified() {
        _uiState.update { it.copy(isOtpVerified = false) }
    }

    fun consumeErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeResendMessage() {
        _uiState.update { it.copy(resendMessage = null) }
    }
}

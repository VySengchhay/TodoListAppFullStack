package com.androidapp.todolistapplication.feature.auth.presentation.otpverify

data class OtpVerifyUiState (
    val email: String = "",
    val otp: String = "",
    val isResending: Boolean = false,
    val isVerifying: Boolean = false,
    val isOtpVerified: Boolean = false,
    val errorMessage: String? = null,
    val resendMessage: String? = null
) {
    val isOtpValid: Boolean
        get() = otp.length == OTP_LENGTH && otp.all { it.isDigit() }

    companion object {
        const val OTP_LENGTH = 6
    }
}

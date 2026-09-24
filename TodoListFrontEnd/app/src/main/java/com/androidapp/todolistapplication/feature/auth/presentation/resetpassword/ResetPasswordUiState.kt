package com.androidapp.todolistapplication.feature.auth.presentation.resetpassword

data class ResetPasswordUiState (
    val email: String = "",
    val otp: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isResetSuccessful: Boolean = false
) {
    val isPasswordStrong: Boolean
        get() = newPassword.length >= MIN_PASSWORD_LENGTH &&
            newPassword.any { it.isLetter() } &&
            newPassword.any { it.isDigit() }

    val passwordsMatch: Boolean
        get() = newPassword == confirmPassword

    val isFormValid: Boolean
        get() = email.isNotBlank() && otp.isNotBlank() && isPasswordStrong && passwordsMatch

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}

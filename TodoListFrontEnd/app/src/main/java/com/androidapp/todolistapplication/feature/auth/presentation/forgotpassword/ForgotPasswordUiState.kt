package com.androidapp.todolistapplication.feature.auth.presentation.forgotpassword

data class ForgotPasswordUiState (
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRequestSuccess: Boolean = false
) {
    val isEmailValid: Boolean
        get() = EMAIL_REGEX.matches(email.trim())

    val isFormValid: Boolean
        get() = isEmailValid

    private companion object {
        val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}

package com.androidapp.todolistapplication.feature.auth.presentation.register

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
) {
    val passwordsMismatch: Boolean
        get() = password.isNotBlank() && confirmPassword.isNotBlank() && password != confirmPassword

    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
}
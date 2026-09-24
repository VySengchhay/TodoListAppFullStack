package com.androidapp.todolistapplication.feature.auth.presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}
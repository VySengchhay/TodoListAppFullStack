package com.androidapp.todolistapplication.feature.auth.data.remote.dto.request

data class PasswordResetConfirmDto(
    val email: String,
    val otp: String,
    val newPassword: String
)

package com.androidapp.todolistapplication.feature.auth.data.remote.dto.request

data class PasswordResetVerifyRequestDto(
    val email: String,
    val otp: String
)

package com.androidapp.todolistapplication.feature.auth.data.remote.dto.response

data class AuthTokensResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresInSeconds: Long
)

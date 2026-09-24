package com.androidapp.todolistapplication.feature.auth.domain.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long
)
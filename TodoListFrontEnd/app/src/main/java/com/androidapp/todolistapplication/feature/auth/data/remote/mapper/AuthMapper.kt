package com.androidapp.todolistapplication.feature.auth.data.remote.mapper

import com.androidapp.todolistapplication.feature.auth.data.remote.dto.response.AuthTokensResponseDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.response.MeResponseDto
import com.androidapp.todolistapplication.feature.auth.domain.model.AuthTokens
import com.androidapp.todolistapplication.feature.auth.domain.model.User

fun AuthTokensResponseDto.toDomain() : AuthTokens {
    return AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresInSeconds = expiresInSeconds
    )
}

fun MeResponseDto.toDomain() : User {
    return User(
        id = id,
        email = email
    )
}
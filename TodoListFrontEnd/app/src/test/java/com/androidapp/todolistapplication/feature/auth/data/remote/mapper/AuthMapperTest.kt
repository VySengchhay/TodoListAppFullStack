package com.androidapp.todolistapplication.feature.auth.data.remote.mapper

import com.androidapp.todolistapplication.feature.auth.data.remote.dto.response.AuthTokensResponseDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.response.MeResponseDto
import com.androidapp.todolistapplication.feature.auth.domain.model.AuthTokens
import com.androidapp.todolistapplication.feature.auth.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthMapperTest {

    @Test
    fun `token response maps to domain tokens`() {
        val dto = AuthTokensResponseDto(
            accessToken = "access",
            refreshToken = "refresh",
            tokenType = "Bearer",
            expiresInSeconds = 900
        )

        assertEquals(AuthTokens(accessToken = "access", refreshToken = "refresh", expiresInSeconds = 900), dto.toDomain())
    }

    @Test
    fun `me response maps to domain user`() {
        assertEquals(User(id = "u1", email = "me@example.com"), MeResponseDto(id = "u1", email = "me@example.com").toDomain())
    }
}

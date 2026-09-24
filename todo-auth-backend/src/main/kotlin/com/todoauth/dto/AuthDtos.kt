package com.todoauth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
	@field:NotBlank
	@field:Email
	val email: String,

	// At least 8 chars, one letter and one number - reasonable minimum for a portfolio project.
	@field:NotBlank
	@field:Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
	@field:Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
		message = "Password must contain at least one letter and one number"
	)
	val password: String
)

data class RegisterResponse(
	val id: String,
	val email: String
)

data class LoginRequest(
	@field:NotBlank
	@field:Email
	val email: String,

	@field:NotBlank
	val password: String
)

data class AuthTokensResponse(
	val accessToken: String,
	val refreshToken: String,
	val tokenType: String = "Bearer",
	val expiresInSeconds: Long
)

data class RefreshRequest(
	@field:NotBlank
	val refreshToken: String
)

data class LogoutRequest(
	@field:NotBlank
	val refreshToken: String
)

data class PasswordResetRequestDto(
	@field:NotBlank
	@field:Email
	val email: String
)

data class PasswordResetVerifyDto(
	@field:NotBlank
	@field:Email
	val email: String,

	@field:NotBlank
	@field:Size(min = 6, max = 6)
	val otp: String
)

data class PasswordResetConfirmDto(
	@field:NotBlank
	@field:Email
	val email: String,

	@field:NotBlank
	@field:Size(min = 6, max = 6)
	val otp: String,

	@field:NotBlank
	@field:Size(min = 8, max = 72)
	@field:Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
		message = "Password must contain at least one letter and one number"
	)
	val newPassword: String
)

data class MeResponse(
	val id: String,
	val email: String
)

data class GenericMessageResponse(
	val message: String
)

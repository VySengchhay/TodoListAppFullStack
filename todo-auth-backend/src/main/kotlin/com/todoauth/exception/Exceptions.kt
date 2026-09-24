package com.todoauth.exception

class EmailAlreadyRegisteredException(email: String) :
	RuntimeException("An account with email '$email' already exists")

class InvalidCredentialsException :
	RuntimeException("Invalid email or password")

class InvalidRefreshTokenException :
	RuntimeException("Refresh token is invalid, expired, or revoked")

class InvalidOtpException(message: String = "Invalid or expired OTP") :
	RuntimeException(message)

class TooManyOtpRequestsException :
	RuntimeException("Too many password reset requests. Please try again later")

class UserNotFoundException :
	RuntimeException("User not found")

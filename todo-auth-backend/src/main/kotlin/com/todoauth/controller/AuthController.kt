package com.todoauth.controller

import com.todoauth.dto.*
import com.todoauth.exception.UserNotFoundException
import com.todoauth.repository.UserRepository
import com.todoauth.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthController(
	private val authService: AuthService,
	private val userRepository: UserRepository
) {

	@PostMapping("/register")
	fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<RegisterResponse> =
		ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request))

	@PostMapping("/login")
	fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthTokensResponse> =
		ResponseEntity.ok(authService.login(request))

	@PostMapping("/refresh")
	fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<AuthTokensResponse> =
		ResponseEntity.ok(authService.refresh(request))

	@PostMapping("/logout")
	fun logout(@Valid @RequestBody request: LogoutRequest): ResponseEntity<GenericMessageResponse> {
		authService.logout(request)
		return ResponseEntity.ok(GenericMessageResponse("Logged out"))
	}

	@PostMapping("/password-reset/request")
	fun requestPasswordReset(@Valid @RequestBody request: PasswordResetRequestDto): ResponseEntity<GenericMessageResponse> {
		authService.requestPasswordReset(request)
		// Generic response regardless of whether the email exists - prevents user enumeration.
		return ResponseEntity.ok(GenericMessageResponse("If that email is registered, a reset code has been sent"))
	}

	@PostMapping("/password-reset/verify")
	fun verifyPasswordResetOtp(@Valid @RequestBody request: PasswordResetVerifyDto): ResponseEntity<GenericMessageResponse> {
		authService.verifyPasswordResetOtp(request)
		return ResponseEntity.ok(GenericMessageResponse("Code verified"))
	}

	@PostMapping("/password-reset/confirm")
	fun confirmPasswordReset(@Valid @RequestBody request: PasswordResetConfirmDto): ResponseEntity<GenericMessageResponse> {
		authService.confirmPasswordReset(request)
		return ResponseEntity.ok(GenericMessageResponse("Password has been reset successfully"))
	}

	// Simple protected endpoint to prove the JWT filter + security config work end to end.
	@GetMapping("/me")
	fun me(httpRequest: HttpServletRequest): ResponseEntity<MeResponse> {
		val userId = httpRequest.getAttribute("userId") as? UUID
			?: throw UserNotFoundException()
		val user = userRepository.findById(userId).orElseThrow { UserNotFoundException() }
		return ResponseEntity.ok(MeResponse(id = user.id.toString(), email = user.email))
	}
}

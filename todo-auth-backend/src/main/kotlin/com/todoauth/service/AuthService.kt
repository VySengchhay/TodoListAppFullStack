package com.todoauth.service

import com.todoauth.dto.*
import com.todoauth.entity.PasswordResetOtp
import com.todoauth.entity.RefreshToken
import com.todoauth.entity.User
import com.todoauth.exception.*
import com.todoauth.repository.PasswordResetOtpRepository
import com.todoauth.repository.RefreshTokenRepository
import com.todoauth.repository.UserRepository
import com.todoauth.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.UUID

@Service
class AuthService(
	private val userRepository: UserRepository,
	private val refreshTokenRepository: RefreshTokenRepository,
	private val passwordResetOtpRepository: PasswordResetOtpRepository,
	private val passwordEncoder: PasswordEncoder,
	private val jwtService: JwtService,
	private val emailService: EmailService,
	@Value("\${app.jwt.refresh-token-expiry-days}") private val refreshTokenExpiryDays: Long,
	@Value("\${app.otp.expiry-minutes}") private val otpExpiryMinutes: Long,
	@Value("\${app.otp.max-verify-attempts}") private val otpMaxAttempts: Int,
	@Value("\${app.otp.max-requests-per-hour}") private val otpMaxRequestsPerHour: Int
) {
	private val log = LoggerFactory.getLogger(AuthService::class.java)
	private val secureRandom = SecureRandom()

	// ---------- Register ----------

	@Transactional
	fun register(request: RegisterRequest): RegisterResponse {
		val normalizedEmail = request.email.trim().lowercase()

		if (userRepository.existsByEmail(normalizedEmail)) {
			throw EmailAlreadyRegisteredException(normalizedEmail)
		}

		val user = User(
			email = normalizedEmail,
			passwordHash = passwordEncoder.encode(request.password)
		)
		val saved = userRepository.save(user)
		log.info("Registered new user {}", saved.id)

		return RegisterResponse(id = saved.id.toString(), email = saved.email)
	}

	// ---------- Login ----------

	@Transactional
	fun login(request: LoginRequest): AuthTokensResponse {
		val normalizedEmail = request.email.trim().lowercase()
		val user = userRepository.findByEmail(normalizedEmail)
			.orElseThrow { InvalidCredentialsException() }

		if (!user.enabled || !passwordEncoder.matches(request.password, user.passwordHash)) {
			throw InvalidCredentialsException()
		}

		return issueTokens(user)
	}

	// ---------- Refresh ----------

	@Transactional
	fun refresh(request: RefreshRequest): AuthTokensResponse {
		val tokenHash = hashToken(request.refreshToken)
		val stored = refreshTokenRepository.findByTokenHash(tokenHash)
			.orElseThrow { InvalidRefreshTokenException() }

		if (stored.revoked || stored.expiryDate.isBefore(Instant.now())) {
			throw InvalidRefreshTokenException()
		}

		// Rotate: revoke the old refresh token and issue a brand new pair.
		stored.revoked = true
		refreshTokenRepository.save(stored)

		return issueTokens(stored.user)
	}

	// ---------- Logout ----------

	@Transactional
	fun logout(request: LogoutRequest) {
		val tokenHash = hashToken(request.refreshToken)
		refreshTokenRepository.findByTokenHash(tokenHash).ifPresent {
			it.revoked = true
			refreshTokenRepository.save(it)
		}
		// Intentionally no error if the token wasn't found - logout is idempotent.
	}

	// ---------- Password reset: request ----------

	@Transactional
	fun requestPasswordReset(request: PasswordResetRequestDto) {
		val normalizedEmail = request.email.trim().lowercase()
		val userExists = userRepository.existsByEmail(normalizedEmail)

		// Always behave the same way whether or not the email exists,
		// to avoid leaking which emails are registered (user enumeration).
		if (!userExists) {
			log.info("Password reset requested for unknown email {}", normalizedEmail)
			return
		}

		val since = Instant.now().minus(1, ChronoUnit.HOURS)
		val recentRequests = passwordResetOtpRepository.countRecentRequests(normalizedEmail, since)
		if (recentRequests >= otpMaxRequestsPerHour) {
			throw TooManyOtpRequestsException()
		}

		val otp = generateOtp()
		val entity = PasswordResetOtp(
			email = normalizedEmail,
			otpHash = hashToken(otp),
			expiryDate = Instant.now().plus(otpExpiryMinutes, ChronoUnit.MINUTES)
		)
		passwordResetOtpRepository.save(entity)
		emailService.sendOtpEmail(normalizedEmail, otp, otpExpiryMinutes)
	}

	// ---------- Password reset: verify ----------

	/**
	 * Checks a code without consuming it, so the app can tell the user the code is wrong
	 * before asking for a new password. The code stays usable for /password-reset/confirm.
	 *
	 * noRollbackFor: a wrong guess increments attempts and then throws. Without this, Spring
	 * rolls the increment back on the RuntimeException and the attempt limit never applies.
	 */
	@Transactional(noRollbackFor = [InvalidOtpException::class])
	fun verifyPasswordResetOtp(request: PasswordResetVerifyDto) {
		val normalizedEmail = request.email.trim().lowercase()
		checkOtp(normalizedEmail, request.otp)
		log.info("Password reset code verified for {}", normalizedEmail)
	}

	// ---------- Password reset: confirm ----------

	@Transactional(noRollbackFor = [InvalidOtpException::class])
	fun confirmPasswordReset(request: PasswordResetConfirmDto) {
		val normalizedEmail = request.email.trim().lowercase()

		val otpEntity = checkOtp(normalizedEmail, request.otp)

		val user = userRepository.findByEmail(normalizedEmail)
			.orElseThrow { UserNotFoundException() }

		user.passwordHash = passwordEncoder.encode(request.newPassword)
		user.updatedAt = Instant.now()
		userRepository.save(user)

		otpEntity.used = true
		passwordResetOtpRepository.save(otpEntity)

		// Reset password should force re-login everywhere.
		refreshTokenRepository.revokeAllForUser(user)

		log.info("Password reset completed for user {}", user.id)
	}

	// ---------- Helpers ----------

	/**
	 * Returns the newest unused OTP for [email] when [otp] matches it, otherwise throws
	 * InvalidOtpException. A mismatch counts as a failed attempt. Never marks the OTP used -
	 * the caller decides that.
	 */
	private fun checkOtp(email: String, otp: String): PasswordResetOtp {
		val otpEntity = passwordResetOtpRepository
			.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
			.orElseThrow { InvalidOtpException() }

		if (otpEntity.used || otpEntity.expiryDate.isBefore(Instant.now())) {
			throw InvalidOtpException()
		}

		if (otpEntity.attempts >= otpMaxAttempts) {
			throw InvalidOtpException("Too many incorrect attempts. Please request a new code")
		}

		if (hashToken(otp) != otpEntity.otpHash) {
			otpEntity.attempts += 1
			passwordResetOtpRepository.save(otpEntity)
			throw InvalidOtpException()
		}

		return otpEntity
	}

	private fun issueTokens(user: User): AuthTokensResponse {
		val userId = user.id ?: throw IllegalStateException("User has no id")
		val accessToken = jwtService.generateAccessToken(userId, user.email)

		val rawRefreshToken = generateRefreshTokenValue()
		val refreshToken = RefreshToken(
			user = user,
			tokenHash = hashToken(rawRefreshToken),
			expiryDate = Instant.now().plus(refreshTokenExpiryDays, ChronoUnit.DAYS)
		)
		refreshTokenRepository.save(refreshToken)

		return AuthTokensResponse(
			accessToken = accessToken,
			refreshToken = rawRefreshToken,
			expiresInSeconds = jwtService.accessTokenExpirySeconds()
		)
	}

	private fun generateRefreshTokenValue(): String {
		val bytes = ByteArray(64)
		secureRandom.nextBytes(bytes)
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
	}

	private fun generateOtp(): String {
		// 6-digit numeric code, zero-padded.
		val code = secureRandom.nextInt(1_000_000)
		return code.toString().padStart(6, '0')
	}

	private fun hashToken(raw: String): String {
		val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
		return Base64.getEncoder().encodeToString(digest)
	}
}

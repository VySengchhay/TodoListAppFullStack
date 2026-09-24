package com.todoauth.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "password_reset_otps")
class PasswordResetOtp(
	@Id
	@GeneratedValue
	val id: UUID? = null,

	@Column(nullable = false)
	var email: String,

	// SHA-256 hash of the OTP code. Raw OTP is only ever emailed, never stored.
	@Column(name = "otp_hash", nullable = false)
	var otpHash: String,

	@Column(name = "expiry_date", nullable = false)
	var expiryDate: Instant,

	@Column(nullable = false)
	var attempts: Int = 0,

	@Column(nullable = false)
	var used: Boolean = false,

	@Column(name = "created_at", nullable = false, updatable = false)
	var createdAt: Instant = Instant.now()
)

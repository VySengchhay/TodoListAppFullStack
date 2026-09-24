package com.todoauth.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
	@Id
	@GeneratedValue
	val id: UUID? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	var user: User,

	// SHA-256 hash of the raw refresh token. The raw token is only ever
	// returned to the client and never persisted in plaintext.
	@Column(name = "token_hash", nullable = false, unique = true)
	var tokenHash: String,

	@Column(name = "expiry_date", nullable = false)
	var expiryDate: Instant,

	@Column(nullable = false)
	var revoked: Boolean = false,

	@Column(name = "created_at", nullable = false, updatable = false)
	var createdAt: Instant = Instant.now()
)

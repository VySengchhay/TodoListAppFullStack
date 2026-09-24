package com.todoauth.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtService(
	@Value("\${app.jwt.secret}") secret: String,
	@Value("\${app.jwt.access-token-expiry-minutes}") private val accessTokenExpiryMinutes: Long
) {
	private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

	fun generateAccessToken(userId: UUID, email: String): String {
		val now = Date()
		val expiry = Date(now.time + accessTokenExpiryMinutes * 60 * 1000)
		return Jwts.builder()
			.subject(userId.toString())
			.claim("email", email)
			.issuedAt(now)
			.expiration(expiry)
			.signWith(key)
			.compact()
	}

	fun accessTokenExpirySeconds(): Long = accessTokenExpiryMinutes * 60

	fun extractUserId(token: String): UUID? =
		runCatching { UUID.fromString(parseClaims(token).subject) }.getOrNull()

	fun extractEmail(token: String): String? =
		runCatching { parseClaims(token)["email"] as String }.getOrNull()

	fun isTokenValid(token: String): Boolean =
		runCatching { parseClaims(token); true }.getOrElse { false }

	private fun parseClaims(token: String): Claims =
		Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
}

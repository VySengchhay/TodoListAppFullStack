package com.todoauth.repository

import com.todoauth.entity.RefreshToken
import com.todoauth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.Optional
import java.util.UUID

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
	fun findByTokenHash(tokenHash: String): Optional<RefreshToken>

	@Modifying
	@Query("update RefreshToken r set r.revoked = true where r.user = :user and r.revoked = false")
	fun revokeAllForUser(user: User)

	@Modifying
	@Query("delete from RefreshToken r where r.expiryDate < :now")
	fun deleteAllExpired(now: Instant)
}

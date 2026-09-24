package com.todoauth.repository

import com.todoauth.entity.PasswordResetOtp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.Optional
import java.util.UUID

interface PasswordResetOtpRepository : JpaRepository<PasswordResetOtp, UUID> {

	fun findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email: String): Optional<PasswordResetOtp>

	@Query(
		"select count(o) from PasswordResetOtp o where o.email = :email and o.createdAt > :since"
	)
	fun countRecentRequests(email: String, since: Instant): Long
}

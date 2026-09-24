package com.todoauth.service

import com.todoauth.repository.RefreshTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class TokenCleanupService(
	private val refreshTokenRepository: RefreshTokenRepository
) {
	private val log = LoggerFactory.getLogger(TokenCleanupService::class.java)

	// Runs once a day; deletes expired refresh tokens so the table doesn't grow unbounded.
	@Scheduled(cron = "0 0 3 * * *")
	@Transactional
	fun purgeExpiredRefreshTokens() {
		refreshTokenRepository.deleteAllExpired(Instant.now())
		log.debug("Purged expired refresh tokens")
	}
}

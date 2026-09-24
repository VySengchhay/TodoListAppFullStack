package com.todoauth.security

import com.todoauth.repository.UserRepository
import org.springframework.security.core.userdetails.User as SpringUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
	private val userRepository: UserRepository
) : UserDetailsService {

	override fun loadUserByUsername(email: String): UserDetails {
		val user = userRepository.findByEmail(email)
			.orElseThrow { UsernameNotFoundException("No user with email $email") }

		return SpringUser.builder()
			.username(user.email)
			.password(user.passwordHash)
			.disabled(!user.enabled)
			.authorities(emptyList())
			.build()
	}
}

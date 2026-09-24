package com.todoauth.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.todoauth.dto.ErrorResponse
import com.todoauth.security.JwtAuthenticationFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
	private val jwtAuthenticationFilter: JwtAuthenticationFilter,
	private val objectMapper: ObjectMapper
) {

	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
		config.authenticationManager

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		val config = CorsConfiguration().apply {
			// Tighten this to your Android app's actual origin(s) / use a backend
			// gateway in production. Left open for local development.
			allowedOriginPatterns = listOf("*")
			allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
			allowedHeaders = listOf("*")
		}
		return UrlBasedCorsConfigurationSource().apply {
			registerCorsConfiguration("/**", config)
		}
	}

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.csrf { it.disable() }
			.cors { }
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.authorizeHttpRequests { auth ->
				auth
					// Only the truly public auth endpoints are permitted here.
					// Anything else under /api/auth/** (e.g. /me) falls through
					// to anyRequest().authenticated() below.
					.requestMatchers(
						"/api/auth/register",
						"/api/auth/login",
						"/api/auth/refresh",
						"/api/auth/logout",
						"/api/auth/password-reset/**"
					).permitAll()
					.requestMatchers("/actuator/health").permitAll()
					.anyRequest().authenticated()
			}
			.exceptionHandling { ex ->
				// 401 = we don't know who you are (no/expired/invalid token). The Android client's
				// OkHttp Authenticator only fires on 401, so this is what lets it refresh and retry.
				ex.authenticationEntryPoint { request, response, _ ->
					writeError(request, response, HttpStatus.UNAUTHORIZED, "Authentication required")
				}
				// 403 = we know who you are, but this isn't yours.
				ex.accessDeniedHandler { request, response, _ ->
					writeError(request, response, HttpStatus.FORBIDDEN, "Access denied")
				}
			}
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

		return http.build()
	}

	/** Same JSON shape as GlobalExceptionHandler, so clients parse every error the same way. */
	private fun writeError(
		request: HttpServletRequest,
		response: HttpServletResponse,
		status: HttpStatus,
		message: String
	) {
		response.status = status.value()
		response.contentType = MediaType.APPLICATION_JSON_VALUE
		objectMapper.writeValue(
			response.outputStream,
			ErrorResponse(
				status = status.value(),
				error = status.reasonPhrase,
				message = message,
				path = request.requestURI
			)
		)
	}
}

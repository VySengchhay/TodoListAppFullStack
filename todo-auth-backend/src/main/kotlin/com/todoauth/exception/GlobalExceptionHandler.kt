package com.todoauth.exception

import com.todoauth.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(EmailAlreadyRegisteredException::class)
	fun handleEmailAlreadyRegistered(ex: EmailAlreadyRegisteredException, req: HttpServletRequest) =
		build(HttpStatus.CONFLICT, ex.message, req)

	@ExceptionHandler(InvalidCredentialsException::class, BadCredentialsException::class)
	fun handleInvalidCredentials(ex: Exception, req: HttpServletRequest) =
		build(HttpStatus.UNAUTHORIZED, "Invalid email or password", req)

	@ExceptionHandler(InvalidRefreshTokenException::class)
	fun handleInvalidRefreshToken(ex: InvalidRefreshTokenException, req: HttpServletRequest) =
		build(HttpStatus.UNAUTHORIZED, ex.message, req)

	@ExceptionHandler(InvalidOtpException::class)
	fun handleInvalidOtp(ex: InvalidOtpException, req: HttpServletRequest) =
		build(HttpStatus.BAD_REQUEST, ex.message, req)

	@ExceptionHandler(TooManyOtpRequestsException::class)
	fun handleTooManyOtp(ex: TooManyOtpRequestsException, req: HttpServletRequest) =
		build(HttpStatus.TOO_MANY_REQUESTS, ex.message, req)

	@ExceptionHandler(UserNotFoundException::class)
	fun handleUserNotFound(ex: UserNotFoundException, req: HttpServletRequest) =
		build(HttpStatus.NOT_FOUND, ex.message, req)

	@ExceptionHandler(TodoNotFoundException::class)
	fun handleTodoNotFound(ex: TodoNotFoundException, req: HttpServletRequest) =
		build(HttpStatus.NOT_FOUND, ex.message, req)

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidation(ex: MethodArgumentNotValidException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
		val fieldErrors = ex.bindingResult.fieldErrors.associate {
			it.field to (it.defaultMessage ?: "Invalid value")
		}
		val body = ErrorResponse(
			status = HttpStatus.BAD_REQUEST.value(),
			error = HttpStatus.BAD_REQUEST.reasonPhrase,
			message = "Validation failed",
			path = req.requestURI,
			fieldErrors = fieldErrors
		)
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
	}

	@ExceptionHandler(Exception::class)
	fun handleGeneric(ex: Exception, req: HttpServletRequest) =
		build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", req)

	private fun build(status: HttpStatus, message: String?, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
		val body = ErrorResponse(
			status = status.value(),
			error = status.reasonPhrase,
			message = message ?: status.reasonPhrase,
			path = req.requestURI
		)
		return ResponseEntity.status(status).body(body)
	}
}

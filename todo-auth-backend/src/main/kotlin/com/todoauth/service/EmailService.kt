package com.todoauth.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
	private val mailSender: JavaMailSender,
	@Value("\${app.mail.from}") private val fromAddress: String,
	@Value("\${app.mail.console-mode}") private val consoleMode: Boolean
) {
	private val log = LoggerFactory.getLogger(EmailService::class.java)

	fun sendOtpEmail(toEmail: String, otp: String, expiryMinutes: Long) {
		if (consoleMode) {
			// Dev/Docker default: no SMTP credentials required to run the stack.
			// Switch app.mail.console-mode to false and set MAIL_HOST/USERNAME/PASSWORD
			// to send real emails.
			log.info("[DEV MODE] Password reset OTP for {}: {} (expires in {} min)", toEmail, otp, expiryMinutes)
			return
		}

		val message = SimpleMailMessage().apply {
			setFrom(fromAddress)
			setTo(toEmail)
			setSubject("Your password reset code")
			setText(
				"Your password reset code is: $otp\n\n" +
					"This code expires in $expiryMinutes minutes. " +
					"If you didn't request this, you can safely ignore this email."
			)
		}
		mailSender.send(message)
	}
}

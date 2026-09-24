package com.androidapp.todolistapplication.feature.auth.presentation.resetpassword

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResetPasswordUiStateTest {

    // A state that passes every rule; each test breaks exactly one thing.
    private val valid = ResetPasswordUiState(
        email = "me@example.com",
        otp = "123456",
        newPassword = "Abcdefg1",
        confirmPassword = "Abcdefg1"
    )

    @Test
    fun `valid state is valid`() {
        assertTrue(valid.isPasswordStrong)
        assertTrue(valid.passwordsMatch)
        assertTrue(valid.isFormValid)
    }

    @Test
    fun `password shorter than 8 characters is not strong`() {
        val state = valid.copy(newPassword = "Abcdef1", confirmPassword = "Abcdef1")
        assertFalse(state.isPasswordStrong)
        assertFalse(state.isFormValid)
    }

    @Test
    fun `password without a digit is not strong`() {
        assertFalse(valid.copy(newPassword = "Abcdefgh", confirmPassword = "Abcdefgh").isFormValid)
    }

    @Test
    fun `password without a letter is not strong`() {
        assertFalse(valid.copy(newPassword = "12345678", confirmPassword = "12345678").isFormValid)
    }

    @Test
    fun `mismatched confirmation is invalid`() {
        val state = valid.copy(confirmPassword = "Abcdefg2")
        assertFalse(state.passwordsMatch)
        assertFalse(state.isFormValid)
    }

    @Test
    fun `missing email or otp is invalid`() {
        assertFalse(valid.copy(email = "").isFormValid)
        assertFalse(valid.copy(otp = "").isFormValid)
    }
}

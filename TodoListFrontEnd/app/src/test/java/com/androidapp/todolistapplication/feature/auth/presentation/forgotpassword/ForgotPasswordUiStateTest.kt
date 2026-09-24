package com.androidapp.todolistapplication.feature.auth.presentation.forgotpassword

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ForgotPasswordUiStateTest {

    @Test
    fun `a normal email address is valid`() {
        assertTrue(ForgotPasswordUiState(email = "me@example.com").isFormValid)
    }

    @Test
    fun `surrounding spaces are ignored`() {
        assertTrue(ForgotPasswordUiState(email = "  me@example.com  ").isFormValid)
    }

    @Test
    fun `blank email is invalid`() {
        assertFalse(ForgotPasswordUiState(email = "").isFormValid)
        assertFalse(ForgotPasswordUiState(email = "   ").isFormValid)
    }

    @Test
    fun `malformed emails are invalid`() {
        listOf("not-an-email", "me@", "@example.com", "me@example", "me @example.com", "me@@example.com")
            .forEach { email ->
                assertFalse("expected '$email' to be invalid", ForgotPasswordUiState(email = email).isFormValid)
            }
    }
}

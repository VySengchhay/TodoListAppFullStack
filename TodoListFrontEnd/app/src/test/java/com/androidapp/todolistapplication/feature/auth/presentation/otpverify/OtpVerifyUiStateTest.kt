package com.androidapp.todolistapplication.feature.auth.presentation.otpverify

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OtpVerifyUiStateTest {

    @Test
    fun `exactly six digits is valid`() {
        assertTrue(OtpVerifyUiState(otp = "123456").isOtpValid)
    }

    @Test
    fun `too short, too long or non-digit codes are invalid`() {
        listOf("", "12345", "1234567", "12a456", "12 456").forEach { otp ->
            assertFalse("expected '$otp' to be invalid", OtpVerifyUiState(otp = otp).isOtpValid)
        }
    }
}

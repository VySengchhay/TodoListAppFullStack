package com.androidapp.todolistapplication.feature.auth.presentation.otpverify

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OtpVerifyContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val otpChanges = mutableListOf<String>()
    private var resendClicks = 0
    private var continueClicks = 0
    private var backClicks = 0

    private fun render(state: OtpVerifyUiState) {
        composeRule.setContent {
            OtpVerifyScreenContent(
                uiState = state,
                onOtpChange = { otpChanges += it },
                onResendClick = { resendClicks++ },
                onContinueClick = { continueClicks++ },
                onBackClick = { backClicks++ }
            )
        }
    }

    @Test
    fun showsWhereTheCodeWasSent() {
        render(OtpVerifyUiState(email = "me@example.com"))

        composeRule.onNodeWithText("We sent a 6-digit code to me@example.com").assertIsDisplayed()
    }

    @Test
    fun incompleteCode_continueDisabled() {
        render(OtpVerifyUiState(email = "me@example.com", otp = "123"))

        composeRule.onNodeWithText("Continue").assertIsNotEnabled()
    }

    @Test
    fun completeCode_continueEnabledAndClickable() {
        render(OtpVerifyUiState(email = "me@example.com", otp = "123456"))

        composeRule.onNodeWithText("Continue").assertIsEnabled().performClick()

        assertEquals(1, continueClicks)
    }

    @Test
    fun resendIsClickableWithNoCodeTyped() {
        render(OtpVerifyUiState(email = "me@example.com", otp = ""))

        composeRule.onNodeWithText("Resend code").assertIsEnabled().performClick()

        assertEquals(1, resendClicks)
    }

    @Test
    fun whileResending_buttonShowsSendingAndIsDisabled() {
        render(OtpVerifyUiState(email = "me@example.com", isResending = true))

        composeRule.onNodeWithText("Sending...").assertIsNotEnabled()
        composeRule.onNodeWithText("Resend code").assertDoesNotExist()
    }

    @Test
    fun whileVerifying_continueShowsVerifyingAndIsDisabled() {
        render(OtpVerifyUiState(email = "me@example.com", otp = "123456", isVerifying = true))

        composeRule.onNodeWithText("Verifying...").assertIsNotEnabled()
        composeRule.onNodeWithText("Continue").assertDoesNotExist()
    }

    @Test
    fun typingInTheCodeField_reportsTheChange() {
        render(OtpVerifyUiState(email = "me@example.com"))

        composeRule.onNode(hasSetTextAction()).performTextInput("42")

        assertEquals(listOf("42"), otpChanges)
    }

    @Test
    fun backArrow_callsCallback() {
        render(OtpVerifyUiState(email = "me@example.com"))

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertEquals(1, backClicks)
    }
}

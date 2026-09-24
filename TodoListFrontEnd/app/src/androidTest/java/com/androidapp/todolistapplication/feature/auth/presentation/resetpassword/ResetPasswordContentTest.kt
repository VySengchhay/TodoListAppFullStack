package com.androidapp.todolistapplication.feature.auth.presentation.resetpassword

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResetPasswordContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var resetClicks = 0
    private var toggleClicks = 0
    private var backClicks = 0

    private val base = ResetPasswordUiState(email = "me@example.com", otp = "123456")

    private fun render(state: ResetPasswordUiState) {
        composeRule.setContent {
            ResetPasswordScreenContent(
                uiState = state,
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onTogglePasswordVisibility = { toggleClicks++ },
                onResetClick = { resetClicks++ },
                onBackClick = { backClicks++ }
            )
        }
    }

    @Test
    fun emptyForm_noErrorsAndButtonDisabled() {
        render(base)

        composeRule.onNodeWithText("Passwords do not match").assertDoesNotExist()
        composeRule.onNodeWithText("Reset password").assertIsNotEnabled()
    }

    @Test
    fun weakPassword_showsRuleAsError() {
        render(base.copy(newPassword = "abc"))

        composeRule.onNodeWithText("At least 8 characters, with a letter and a number").assertIsDisplayed()
        composeRule.onNodeWithText("Reset password").assertIsNotEnabled()
    }

    @Test
    fun mismatch_showsErrorAndButtonDisabled() {
        render(base.copy(newPassword = "Abcdefg1", confirmPassword = "Abcdefg2"))

        composeRule.onNodeWithText("Passwords do not match").assertIsDisplayed()
        composeRule.onNodeWithText("Reset password").assertIsNotEnabled()
    }

    @Test
    fun validForm_clickReset_callsCallback() {
        render(base.copy(newPassword = "Abcdefg1", confirmPassword = "Abcdefg1"))

        composeRule.onNodeWithText("Reset password").assertIsEnabled().performClick()

        assertEquals(1, resetClicks)
    }

    @Test
    fun visibilityToggle_callsCallback() {
        render(base)

        // Both fields have a toggle; either one flips the shared visibility flag.
        composeRule.onAllNodesWithContentDescription("Show password")[0].performClick()

        assertEquals(1, toggleClicks)
    }

    @Test
    fun backArrow_callsCallback() {
        render(base)

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertEquals(1, backClicks)
    }
}

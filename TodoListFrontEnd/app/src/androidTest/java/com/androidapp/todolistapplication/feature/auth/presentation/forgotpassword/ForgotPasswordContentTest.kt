package com.androidapp.todolistapplication.feature.auth.presentation.forgotpassword

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
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

/**
 * UI tests render the stateless *Content composable with a hand-made UiState.
 * No ViewModel, no Hilt, no network: we only check what the user sees and
 * that taps/typing call the right callback.
 */
@RunWith(AndroidJUnit4::class)
class ForgotPasswordContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var emailChanges = mutableListOf<String>()
    private var sendClicks = 0
    private var backClicks = 0

    private fun render(state: ForgotPasswordUiState) {
        composeRule.setContent {
            ForgotPasswordScreenContent(
                uiState = state,
                onEmailChange = { emailChanges += it },
                onSendCodeClick = { sendClicks++ },
                onBackToLoginClick = { backClicks++ }
            )
        }
    }

    @Test
    fun emptyEmail_sendButtonDisabled_noError() {
        render(ForgotPasswordUiState(email = ""))

        composeRule.onNodeWithText("Send code").assertIsNotEnabled()
        composeRule.onNodeWithText("Enter a valid email address").assertDoesNotExist()
    }

    @Test
    fun invalidEmail_showsErrorAndDisablesButton() {
        render(ForgotPasswordUiState(email = "not-an-email"))

        composeRule.onNodeWithText("Enter a valid email address").assertIsDisplayed()
        composeRule.onNodeWithText("Send code").assertIsNotEnabled()
    }

    @Test
    fun validEmail_clickSend_callsCallback() {
        render(ForgotPasswordUiState(email = "me@example.com"))

        composeRule.onNodeWithText("Send code").assertIsEnabled().performClick()

        assertEquals(1, sendClicks)
    }

    @Test
    fun typing_reportsEachChange() {
        render(ForgotPasswordUiState(email = ""))

        // hasSetTextAction() picks the editable field, not the "Email" label text.
        composeRule.onNode(hasSetTextAction() and hasText("Email")).performTextInput("me")

        assertEquals(listOf("me"), emailChanges)
    }

    @Test
    fun loading_hidesButtonLabel() {
        render(ForgotPasswordUiState(email = "me@example.com", isLoading = true))

        // AuthPrimaryButton swaps its label for a spinner while loading.
        composeRule.onNodeWithText("Send code").assertDoesNotExist()
    }

    @Test
    fun backArrow_callsCallback() {
        render(ForgotPasswordUiState())

        composeRule.onNodeWithContentDescription("Back to login").performClick()

        assertEquals(1, backClicks)
    }
}

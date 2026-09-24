package com.androidapp.todolistapplication.navigation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.androidapp.todolistapplication.HiltTestActivity
import com.androidapp.todolistapplication.core.common.AppError
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.fake.FakeAuthRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Drives the real AppNavDisplay, real screens and real ViewModels.
 * Only the repositories are fake (see TestRepositoryModule), so this checks
 * that routes pass their arguments and that back behaves - without a backend.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class PasswordResetNavigationTest {

    // Hilt must set up the graph (order 0) before the activity starts (order 1).
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var authRepository: FakeAuthRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent { AppNavDisplay(startRoute = LoginRoute, isLoggedIn = false) }
    }

    // Use cases run on Dispatchers.IO here, which Compose's idling doesn't track,
    // so wait for the next screen to appear instead of asserting immediately.
    private fun waitFor(matcher: SemanticsMatcher) =
        composeRule.waitUntilAtLeastOneExists(matcher, timeoutMillis = 5_000)

    private fun field(label: String) = hasSetTextAction() and hasText(label)

    private fun goToOtpScreen() {
        composeRule.onNodeWithText("Forgot password?").performClick()
        waitFor(hasText("Forgot Password?"))
        composeRule.onNode(field("Email")).performTextInput(EMAIL)
        composeRule.onNodeWithText("Send code").performClick()
        waitFor(hasText("We sent a 6-digit code to $EMAIL"))
    }

    private fun goToResetScreen() {
        goToOtpScreen()
        composeRule.onNode(hasSetTextAction()).performTextInput(OTP)
        composeRule.onNodeWithText("Continue").performClick()
        waitFor(hasText("Set a New Password"))
    }

    @Test
    fun fullFlow_resetsPasswordAndReturnsToLogin() {
        goToResetScreen()

        composeRule.onNode(field("New password")).performTextInput(NEW_PASSWORD)
        composeRule.onNode(field("Confirm new password")).performTextInput(NEW_PASSWORD)
        composeRule.onNodeWithText("Reset password").performClick()

        waitFor(hasText("Welcome Back"))
        assertEquals(
            listOf(FakeAuthRepository.ConfirmCall(EMAIL, OTP, NEW_PASSWORD)),
            authRepository.confirmPasswordResetCalls
        )
    }

    @Test
    fun emailTypedOnForgotPassword_reachesOtpScreen() {
        goToOtpScreen()

        composeRule.onNodeWithText("We sent a 6-digit code to $EMAIL").assertExists()
        assertEquals(listOf(EMAIL), authRepository.requestPasswordResetCalls)
    }

    @Test
    fun wrongOtp_staysOnOtpScreenWithError() {
        authRepository.verifyPasswordResetOtpResult = AppResult.Error(AppError.BadRequest("Invalid or expired OTP"))
        goToOtpScreen()

        composeRule.onNode(hasSetTextAction()).performTextInput("111111")
        composeRule.onNodeWithText("Continue").performClick()

        waitFor(hasText("Invalid or expired OTP"))
        composeRule.onNodeWithText("Set a New Password").assertDoesNotExist()
        composeRule.onNodeWithText("Enter code").assertExists()
    }

    @Test
    fun backFromOtp_returnsToForgotPassword_withoutBouncingForward() {
        goToOtpScreen()

        composeRule.onNodeWithContentDescription("Back").performClick()
        waitFor(hasText("Forgot Password?"))
        composeRule.waitForIdle()

        // Before consumeRequestSuccess() existed, this screen re-navigated to OTP on its own.
        composeRule.onNodeWithText("Enter code").assertDoesNotExist()
    }

    @Test
    fun backFromReset_returnsToOtp_keepingTheTypedCode() {
        goToResetScreen()

        composeRule.onNodeWithContentDescription("Back").performClick()
        waitFor(hasText("Enter code"))

        composeRule.onNodeWithText(OTP).assertExists()
    }

    @Test
    fun invalidOtp_staysOnResetScreenWithError() {
        authRepository.confirmPasswordResetResult = AppResult.Error(AppError.BadRequest("Invalid or expired OTP"))
        goToResetScreen()

        composeRule.onNode(field("New password")).performTextInput(NEW_PASSWORD)
        composeRule.onNode(field("Confirm new password")).performTextInput(NEW_PASSWORD)
        composeRule.onNodeWithText("Reset password").performClick()

        waitFor(hasText("Invalid or expired OTP"))
        composeRule.onNodeWithText("Set a New Password").assertExists()
    }

    private companion object {
        const val EMAIL = "me@example.com"
        const val OTP = "123456"
        const val NEW_PASSWORD = "Abcdefg1"
    }
}

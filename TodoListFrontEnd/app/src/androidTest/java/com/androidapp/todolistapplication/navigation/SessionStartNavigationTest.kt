package com.androidapp.todolistapplication.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.androidapp.todolistapplication.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * MainActivity decides startRoute/isLoggedIn from SessionManager; here we pass them in
 * directly, so the test controls "is there a saved session?" without touching DataStore.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class SessionStartNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun savedSession_opensTodoList() {
        composeRule.setContent { AppNavDisplay(startRoute = TodoListRoute, isLoggedIn = true) }

        composeRule.waitUntilAtLeastOneExists(hasText("My todos"), timeoutMillis = 5_000)
        composeRule.onNodeWithText("Welcome Back").assertDoesNotExist()
    }

    @Test
    fun noSession_opensLogin() {
        composeRule.setContent { AppNavDisplay(startRoute = LoginRoute, isLoggedIn = false) }

        composeRule.waitUntilAtLeastOneExists(hasText("Welcome Back"), timeoutMillis = 5_000)
    }

    @Test
    fun sessionClearedWhileOnTodoList_returnsToLogin() {
        var isLoggedIn by mutableStateOf(true)
        composeRule.setContent { AppNavDisplay(startRoute = TodoListRoute, isLoggedIn = isLoggedIn) }
        composeRule.waitUntilAtLeastOneExists(hasText("My todos"), timeoutMillis = 5_000)

        // Simulates TokenAuthenticator failing to refresh and calling clearSession().
        isLoggedIn = false

        composeRule.waitUntilAtLeastOneExists(hasText("Welcome Back"), timeoutMillis = 5_000)
        composeRule.onNodeWithText("My todos").assertDoesNotExist()
    }
}

package com.androidapp.todolistapplication.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object RegisterRoute : NavKey

@Serializable
data object LoginRoute : NavKey

@Serializable
data object ForgotPasswordRoute : NavKey

@Serializable
data class OtpVerifyRoute(val email: String) : NavKey

@Serializable
data class ResetPasswordRoute(val email: String, val otp: String) : NavKey

@Serializable
data object TodoListRoute : NavKey

@Serializable
data object AddTodoRoute : NavKey

@Serializable
data class EditTodoRoute(val todoId: String) : NavKey

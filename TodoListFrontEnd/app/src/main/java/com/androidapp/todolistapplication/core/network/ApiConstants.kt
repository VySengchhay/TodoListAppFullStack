package com.androidapp.todolistapplication.core.network

import com.androidapp.todolistapplication.BuildConfig

object ApiConstants {
    const val BASE_URL = BuildConfig.BASE_URL
    const val REGISTER = "api/auth/register"
    const val LOGIN = "api/auth/login"
    const val REFRESH = "api/auth/refresh"
    const val LOGOUT = "api/auth/logout"
    const val PASSWORD_RESET_REQUEST = "api/auth/password-reset/request"
    const val PASSWORD_RESET_VERIFY = "api/auth/password-reset/verify"
    const val PASSWORD_RESET_CONFIRM = "api/auth/password-reset/confirm"
    const val ME = "api/auth/me"
    const val TODOS = "/api/todos"
}
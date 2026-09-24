package com.androidapp.todolistapplication.core.network

import com.androidapp.todolistapplication.core.common.AppError
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

data class ErrorResponseDto(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null,
    val path: String? = null,
    val fieldErrors: Map<String, String>? = null
)

object NetworkErrorMapper {

    private val gson = Gson()

    fun toAppError(throwable: Throwable): AppError = when (throwable) {
        is UnknownHostException -> AppError.NoInternet
        is SocketTimeoutException -> AppError.Timeout
        is HttpException -> mapHttpException(throwable)
        is IOException -> AppError.NoInternet
        else -> AppError.Unknown(throwable.message ?: "Unexpected error")
    }

    private fun mapHttpException(exception: HttpException): AppError {
        val body = exception.response()?.errorBody()?.string()
        val parsed = runCatching { gson.fromJson(body, ErrorResponseDto::class.java) }.getOrNull()
        val message = (parsed?.message ?: exception.message())
            .takeUnless { it.isNullOrBlank() }
            ?: "Request failed (${exception.code()})"

        return when (exception.code()) {
            400 -> if (!parsed?.fieldErrors.isNullOrEmpty()) {
                AppError.Validation(parsed!!.fieldErrors!!)
            } else {
                AppError.BadRequest(message)
            }
            401, 403 -> AppError.Unauthorized(message)
            404 -> AppError.NotFound(message)
            409 -> AppError.Conflict(message)
            429 -> AppError.TooManyRequests(message)
            else -> AppError.Unknown(message)
        }
    }
}
package com.androidapp.todolistapplication.core.network

import com.androidapp.todolistapplication.core.common.AppError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkErrorMapperTest {

    /** Builds the same HttpException Retrofit throws for a non-2xx response. */
    private fun httpError(code: Int, body: String): HttpException =
        HttpException(Response.error<Any>(code, body.toResponseBody("application/json".toMediaType())))

    private fun springError(status: Int, message: String, fieldErrors: String = "null") =
        """{"status":$status,"error":"x","message":"$message","path":"/api","fieldErrors":$fieldErrors}"""

    @Test
    fun `unknown host means no internet`() {
        assertEquals(AppError.NoInternet, NetworkErrorMapper.toAppError(UnknownHostException()))
    }

    @Test
    fun `socket timeout maps to Timeout, not NoInternet`() {
        // SocketTimeoutException is an IOException, so this also checks the when-branch order.
        assertEquals(AppError.Timeout, NetworkErrorMapper.toAppError(SocketTimeoutException()))
    }

    @Test
    fun `other IO failures mean no internet`() {
        assertEquals(AppError.NoInternet, NetworkErrorMapper.toAppError(IOException("reset")))
    }

    @Test
    fun `400 with field errors becomes Validation`() {
        val error = NetworkErrorMapper.toAppError(
            httpError(400, springError(400, "Validation failed", """{"email":"must be a well-formed email"}"""))
        )
        assertEquals(AppError.Validation(mapOf("email" to "must be a well-formed email")), error)
        assertEquals("must be a well-formed email", error.userMessage())
    }

    @Test
    fun `400 without field errors becomes BadRequest with the server message`() {
        val error = NetworkErrorMapper.toAppError(httpError(400, springError(400, "Invalid or expired OTP")))
        assertEquals(AppError.BadRequest("Invalid or expired OTP"), error)
    }

    @Test
    fun `status codes map to their AppError types`() {
        assertEquals(AppError.Unauthorized("nope"), NetworkErrorMapper.toAppError(httpError(401, springError(401, "nope"))))
        assertEquals(AppError.Unauthorized("nope"), NetworkErrorMapper.toAppError(httpError(403, springError(403, "nope"))))
        assertEquals(AppError.NotFound("gone"), NetworkErrorMapper.toAppError(httpError(404, springError(404, "gone"))))
        assertEquals(AppError.Conflict("taken"), NetworkErrorMapper.toAppError(httpError(409, springError(409, "taken"))))
        assertEquals(AppError.TooManyRequests("slow down"), NetworkErrorMapper.toAppError(httpError(429, springError(429, "slow down"))))
        assertEquals(AppError.Unknown("boom"), NetworkErrorMapper.toAppError(httpError(500, springError(500, "boom"))))
    }

    @Test
    fun `a non-JSON error body still maps by status code`() {
        assertTrue(NetworkErrorMapper.toAppError(httpError(404, "<html>Not Found</html>")) is AppError.NotFound)
    }

    @Test
    fun `non-network exceptions become Unknown with their message`() {
        assertEquals(AppError.Unknown("weird"), NetworkErrorMapper.toAppError(IllegalStateException("weird")))
    }
}

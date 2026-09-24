package com.androidapp.todolistapplication.feature.auth.data.remote

import com.androidapp.todolistapplication.core.network.ApiConstants
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.LoginRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.LogoutRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.PasswordResetRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.PasswordResetVerifyRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.RefreshRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.RegisterRequestDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.response.AuthTokensResponseDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.response.GenericMessageResponseDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.response.MeResponseDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.request.PasswordResetConfirmDto
import com.androidapp.todolistapplication.feature.auth.data.remote.dto.response.RegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST(ApiConstants.REGISTER)
    suspend fun register(@Body request: RegisterRequestDto): RegisterResponseDto

    @POST(ApiConstants.LOGIN)
    suspend fun login(@Body request: LoginRequestDto): AuthTokensResponseDto

    @POST(ApiConstants.REFRESH)
    suspend fun refresh(@Body request: RefreshRequestDto): AuthTokensResponseDto

    @POST(ApiConstants.LOGOUT)
    suspend fun logout(@Body request: LogoutRequestDto) : GenericMessageResponseDto

    @POST(ApiConstants.PASSWORD_RESET_REQUEST)
    suspend fun requestPasswordReset(@Body request: PasswordResetRequestDto): GenericMessageResponseDto

    @POST(ApiConstants.PASSWORD_RESET_VERIFY)
    suspend fun verifyPasswordResetOtp(@Body request: PasswordResetVerifyRequestDto): GenericMessageResponseDto

    @POST(ApiConstants.PASSWORD_RESET_CONFIRM)
    suspend fun confirmPasswordReset(@Body request: PasswordResetConfirmDto): GenericMessageResponseDto

    @GET(ApiConstants.ME)
    suspend fun me(): MeResponseDto
}
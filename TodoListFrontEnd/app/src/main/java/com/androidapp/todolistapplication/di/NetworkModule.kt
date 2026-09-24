package com.androidapp.todolistapplication.di

import com.androidapp.todolistapplication.BuildConfig
import com.androidapp.todolistapplication.core.network.ApiConstants
import com.androidapp.todolistapplication.core.network.AuthInterceptor
import com.androidapp.todolistapplication.core.network.TokenAuthenticator
import com.androidapp.todolistapplication.feature.auth.data.remote.AuthApiService
import com.androidapp.todolistapplication.feature.todo.data.remote.TodoApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT_SECONDS = 30L

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .serializeNulls()
        .create()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            // Bodies contain passwords and tokens, so only log them in debug builds.
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)       // adds Bearer token
        .addInterceptor(loggingInterceptor)    // after auth, so it logs the final request
        .authenticator(tokenAuthenticator)     // handles 401 -> refresh -> retry
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)
//
    @Provides
    @Singleton
    fun provideTodoApiService(retrofit: Retrofit): TodoApiService {
        return retrofit.create(TodoApiService::class.java)
    }
}
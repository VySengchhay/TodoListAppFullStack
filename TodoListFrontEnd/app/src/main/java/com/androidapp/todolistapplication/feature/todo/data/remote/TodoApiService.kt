package com.androidapp.todolistapplication.feature.todo.data.remote

import com.androidapp.todolistapplication.core.network.ApiConstants
import com.androidapp.todolistapplication.feature.todo.data.remote.dto.request.CreateTodoRequestDto
import com.androidapp.todolistapplication.feature.todo.data.remote.dto.request.UpdateTodoRequestDto
import com.androidapp.todolistapplication.feature.todo.data.remote.dto.response.PagedTodoResponseDto
import com.androidapp.todolistapplication.feature.todo.data.remote.dto.response.TodoResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoApiService {

    @POST(ApiConstants.TODOS)
    suspend fun createTodo(
        @Body request: CreateTodoRequestDto
    ): TodoResponseDto

    @GET(ApiConstants.TODOS)
    suspend fun getTodos(
        @Query("completed") completed: Boolean?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("direction") direction: String
    ): PagedTodoResponseDto

    @GET("${ApiConstants.TODOS}/{id}")
    suspend fun getTodo(
        @Path("id") id: String
    ): TodoResponseDto

    @PUT("${ApiConstants.TODOS}/{id}")
    suspend fun updateTodo(
        @Path("id") id: String,
        @Body request: UpdateTodoRequestDto
    ): TodoResponseDto

    @DELETE("${ApiConstants.TODOS}/{id}")
    suspend fun deleteTodo(
        @Path("id") id: String
    )

    @PATCH("${ApiConstants.TODOS}/{id}/toggle-complete")
    suspend fun toggleTodoComplete(
        @Path("id") id: String
    ): TodoResponseDto

}
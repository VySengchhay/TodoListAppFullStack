package com.androidapp.todolistapplication.feature.todo.data.remote.dto.response

data class PagedTodoResponseDto (
    val items: List<TodoResponseDto>,
    val page: Int,
    val size: Int,
    val totalItems: Long,
    val totalPages: Int
)
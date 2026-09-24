package com.androidapp.todolistapplication.feature.todo.data.remote.dto.response

data class TodoResponseDto(
    val id: String,
    val title: String,
    val description: String?,
    val dueDate: String?,
    val priority: String,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String
)

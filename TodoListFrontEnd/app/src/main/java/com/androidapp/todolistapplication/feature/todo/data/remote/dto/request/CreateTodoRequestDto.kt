package com.androidapp.todolistapplication.feature.todo.data.remote.dto.request

data class CreateTodoRequestDto(
    val title: String,
    val description: String?,
    val dueDate: String?,
    val priority: String,
)

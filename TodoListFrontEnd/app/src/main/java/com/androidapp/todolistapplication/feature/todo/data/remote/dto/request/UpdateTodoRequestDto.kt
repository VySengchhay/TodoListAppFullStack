package com.androidapp.todolistapplication.feature.todo.data.remote.dto.request

data class UpdateTodoRequestDto(
    val title: String,
    val description: String?,
    val dueDate: String?,
    val priority: String,
    val completed: Boolean
)

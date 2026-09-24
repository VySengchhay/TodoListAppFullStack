package com.androidapp.todolistapplication.feature.todo.domain.model

import java.time.Instant

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

data class Todo(
    val id: String,
    val title: String,
    val description: String?,
    val dueDate: Instant?,
    val priority: Priority,
    val completed: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

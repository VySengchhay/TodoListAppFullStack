package com.todoauth.dto

import com.todoauth.entity.Priority
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class CreateTodoRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    val description: String? = null,

    val dueDate: Instant? = null,

    val priority: Priority = Priority.MEDIUM
)

data class UpdateTodoRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    val description: String? = null,

    val dueDate: Instant? = null,

    val priority: Priority = Priority.MEDIUM,

    val completed: Boolean = false
)

data class TodoResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val dueDate: Instant?,
    val priority: Priority,
    val completed: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class PagedTodoResponse(
    val items: List<TodoResponse>,
    val page: Int,
    val size: Int,
    val totalItems: Long,
    val totalPages: Int
)

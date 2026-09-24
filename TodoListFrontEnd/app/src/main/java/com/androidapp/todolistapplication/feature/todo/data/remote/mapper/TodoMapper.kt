package com.androidapp.todolistapplication.feature.todo.data.remote.mapper

import com.androidapp.todolistapplication.feature.todo.data.remote.dto.response.PagedTodoResponseDto
import com.androidapp.todolistapplication.feature.todo.data.remote.dto.response.TodoResponseDto
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoPage
import java.time.Instant

fun TodoResponseDto.toDomain() : Todo {
    return Todo(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate?.let { runCatching { Instant.parse(it) }.getOrNull() },
        priority = runCatching { Priority.valueOf(priority) }.getOrDefault(Priority.MEDIUM),
        completed = completed,
        createdAt = runCatching { Instant.parse(createdAt) }.getOrDefault(Instant.EPOCH),
        updatedAt = runCatching { Instant.parse(createdAt) }.getOrDefault(Instant.EPOCH)
    )
}

fun PagedTodoResponseDto.toDomain() : TodoPage {
    return TodoPage(
        items = items.map { it.toDomain() },
        page = page,
        totalPages = totalPages
    )
}


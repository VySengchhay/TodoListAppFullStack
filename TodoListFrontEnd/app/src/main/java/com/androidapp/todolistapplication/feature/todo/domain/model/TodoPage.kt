package com.androidapp.todolistapplication.feature.todo.domain.model

data class TodoPage(
    val items: List<Todo>,
    val page: Int,
    val totalPages: Int,
) {
    val hasMore: Boolean
        get() = page < totalPages - 1
}

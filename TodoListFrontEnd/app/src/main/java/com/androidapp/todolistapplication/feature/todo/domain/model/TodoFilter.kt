package com.androidapp.todolistapplication.feature.todo.domain.model

enum class TodoFilter {
    ALL, ACTIVE, COMPLETED
}

enum class TodoSortBy(val apiValue: String) {
    CREATED_AT("createdAt"),
    DUE_DATE("dueDate"),
    PRIORITY("priority"),
    TITLE("title")
}

enum class SortDirection(val apiValue: String) {
    ASC("asc"),
    DESC("desc")
}

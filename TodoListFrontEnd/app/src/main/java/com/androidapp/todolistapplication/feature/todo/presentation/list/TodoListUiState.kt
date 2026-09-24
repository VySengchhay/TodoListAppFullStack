package com.androidapp.todolistapplication.feature.todo.presentation.list

import com.androidapp.todolistapplication.feature.todo.domain.model.SortDirection
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoSortBy

data class  TodoListUiState (
    val todos: List<Todo> = emptyList(),
    val userEmail: String? = null,
    val filter: TodoFilter = TodoFilter.ALL,
    val sortBy: TodoSortBy = TodoSortBy.CREATED_AT,
    val direction: SortDirection = SortDirection.DESC,
    val currentPage: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null,
    val pendingDelete: Todo? = null
)
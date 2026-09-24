package com.androidapp.todolistapplication.feature.todo.domain.repository

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.model.SortDirection
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoPage
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoSortBy
import java.time.Instant

interface TodoRepository {

    suspend fun getTodos(
        filter: TodoFilter,
        sortBy: TodoSortBy,
        direction: SortDirection,
        page: Int
    ): AppResult<TodoPage>

    suspend fun createTodo(
        title: String,
        description: String?,
        dueDate: Instant?,
        priority: Priority,
    ): AppResult<Todo>

    suspend fun getTodo(id: String): AppResult<Todo>

    suspend fun updateTodo(
        id: String,
        title: String,
        description: String?,
        dueDate: Instant?,
        priority: Priority,
        completed: Boolean
    ): AppResult<Todo>

    suspend fun deleteTodo(id: String): AppResult<Unit>

    suspend fun toggleTodoComplete(id: String): AppResult<Todo>
}
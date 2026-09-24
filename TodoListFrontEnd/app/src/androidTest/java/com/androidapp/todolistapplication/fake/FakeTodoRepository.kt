package com.androidapp.todolistapplication.fake

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.model.SortDirection
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoPage
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoSortBy
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import java.time.Instant
class FakeTodoRepository : TodoRepository {

    var todos: List<Todo> = emptyList()

    override suspend fun getTodos(
        filter: TodoFilter,
        sortBy: TodoSortBy,
        direction: SortDirection,
        page: Int
    ): AppResult<TodoPage> =
        AppResult.Success(TodoPage(items = todos, page = 0, totalPages = 1))

    override suspend fun getTodo(id: String): AppResult<Todo> =
        TODO("Not needed by the auth navigation tests")

    override suspend fun updateTodo(
        id: String,
        title: String,
        description: String?,
        dueDate: java.time.Instant?,
        priority: Priority,
        completed: Boolean
    ): AppResult<Todo> = TODO("Not needed by the auth navigation tests")

    override suspend fun deleteTodo(id: String): AppResult<Unit> =
        TODO("Not needed by the auth navigation tests")

    override suspend fun toggleTodoComplete(id: String): AppResult<Todo> =
        TODO("Not needed by the auth navigation tests")

    override suspend fun createTodo(
        title: String,
        description: String?,
        dueDate: Instant?,
        priority: Priority
    ): AppResult<Todo> = TODO("Not needed by the auth navigation tests")
}

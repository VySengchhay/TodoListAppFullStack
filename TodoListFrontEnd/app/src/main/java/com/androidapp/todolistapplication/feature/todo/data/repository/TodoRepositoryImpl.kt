package com.androidapp.todolistapplication.feature.todo.data.repository

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.network.NetworkErrorMapper
import com.androidapp.todolistapplication.feature.todo.data.remote.TodoApiService
import com.androidapp.todolistapplication.feature.todo.data.remote.dto.request.CreateTodoRequestDto
import com.androidapp.todolistapplication.feature.todo.data.remote.dto.request.UpdateTodoRequestDto
import com.androidapp.todolistapplication.feature.todo.data.remote.mapper.toDomain
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.model.SortDirection
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoPage
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoSortBy
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import jakarta.inject.Inject
import java.time.Instant

private const val PAGE_SIZE = 10
class TodoRepositoryImpl @Inject constructor(
    private val todoApiService: TodoApiService
) : TodoRepository {
    override suspend fun getTodos(
        filter: TodoFilter,
        sortBy: TodoSortBy,
        direction: SortDirection,
        page: Int
    ): AppResult<TodoPage> = safeCall {
        val completed = when (filter) {
            TodoFilter.ACTIVE -> false
            TodoFilter.COMPLETED -> true
            TodoFilter.ALL -> null
        }

        todoApiService.getTodos(
            completed = completed,
            page = page,
            size = PAGE_SIZE,
            sortBy = sortBy.apiValue,
            direction = direction.apiValue
        ).toDomain()

    }


    override suspend fun createTodo(
        title: String,
        description: String?,
        dueDate: Instant?,
        priority: Priority
    ): AppResult<Todo> = safeCall {
        todoApiService.createTodo(
            CreateTodoRequestDto(
                title = title,
                description = description,
                dueDate = dueDate?.toString(),
                priority = priority.name
            )
        ).toDomain()
    }

    override suspend fun getTodo(id: String): AppResult<Todo> = safeCall {
        todoApiService.getTodo(id).toDomain()
    }

    override suspend fun updateTodo(
        id: String,
        title: String,
        description: String?,
        dueDate: Instant?,
        priority: Priority,
        completed: Boolean
    ): AppResult<Todo> = safeCall {
        todoApiService.updateTodo(
            id = id,
            request = UpdateTodoRequestDto(
                title = title,
                description = description,
                dueDate = dueDate?.toString(),
                priority = priority.name,
                // PUT replaces the whole todo and `completed` defaults to false server-side,
                // so the current value must be sent or editing would un-complete the todo.
                completed = completed
            )
        ).toDomain()
    }

    override suspend fun deleteTodo(id: String): AppResult<Unit> = safeCall {
        todoApiService.deleteTodo(id)
    }

    override suspend fun toggleTodoComplete(id: String): AppResult<Todo> = safeCall {
        todoApiService.toggleTodoComplete(id).toDomain()
    }

    private suspend inline fun <T> safeCall(block: () -> T): AppResult<T> =
        try {
            AppResult.Success(block())
        } catch (e: Exception) {
            AppResult.Error(NetworkErrorMapper.toAppError(e))
        }
}
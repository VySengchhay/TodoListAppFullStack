package com.androidapp.todolistapplication.feature.todo.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.feature.todo.domain.model.SortDirection
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoPage
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoSortBy
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import javax.inject.Inject

class GetTodosUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) : BaseUseCase<GetTodosUseCase.Params, TodoPage>() {
    data class Params(
        val filter: TodoFilter,
        val sortBy: TodoSortBy,
        val direction: SortDirection,
        val page: Int
    )

    override suspend fun execute(params: Params): AppResult<TodoPage> =
        todoRepository.getTodos(
            params.filter,
            params.sortBy,
            params.direction,
            params.page
        )
}
package com.androidapp.todolistapplication.feature.todo.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import java.time.Instant
import javax.inject.Inject

class CreateTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) : BaseUseCase<CreateTodoUseCase.Params, Todo>() {
    data class Params (
        val title: String,
        val description: String?,
        val dueDate: Instant?,
        val priority: Priority
    )

    override suspend fun execute(params: Params): AppResult<Todo> =
        todoRepository.createTodo(
            title = params.title,
            description = params.description,
            dueDate = params.dueDate,
            priority = params.priority
        )
}
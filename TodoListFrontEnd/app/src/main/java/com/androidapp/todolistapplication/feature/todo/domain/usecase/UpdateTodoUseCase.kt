package com.androidapp.todolistapplication.feature.todo.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import java.time.Instant
import javax.inject.Inject

class UpdateTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) : BaseUseCase<UpdateTodoUseCase.Params, Todo>() {
    data class Params(
        val id: String,
        val title: String,
        val description: String?,
        val dueDate: Instant?,
        val priority: Priority,
        val completed: Boolean
    )

    override suspend fun execute(params: Params): AppResult<Todo> =
        todoRepository.updateTodo(
            id = params.id,
            title = params.title,
            description = params.description,
            dueDate = params.dueDate,
            priority = params.priority,
            completed = params.completed
        )
}

package com.androidapp.todolistapplication.feature.todo.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import javax.inject.Inject

class ToggleTodoCompleteUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) : BaseUseCase<String, Todo>() {

    override suspend fun execute(params: String): AppResult<Todo> {
        return todoRepository.toggleTodoComplete(params)
    }
}
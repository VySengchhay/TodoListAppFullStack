package com.androidapp.todolistapplication.feature.todo.domain.usecase

import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.core.common.BaseUseCase
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import javax.inject.Inject

class DeleteTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) : BaseUseCase<String, Unit>() {

    override suspend fun execute(params: String): AppResult<Unit> = todoRepository.deleteTodo(params)
}

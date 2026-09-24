package com.androidapp.todolistapplication.feature.todo.presentation.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.androidapp.todolistapplication.feature.auth.domain.usecase.LogoutUseCase
import com.androidapp.todolistapplication.feature.todo.domain.model.SortDirection
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoSortBy
import com.androidapp.todolistapplication.feature.todo.domain.usecase.DeleteTodoUseCase
import com.androidapp.todolistapplication.feature.todo.domain.usecase.GetTodosUseCase
import com.androidapp.todolistapplication.feature.todo.domain.usecase.ToggleTodoCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val getTodosUseCase: GetTodosUseCase,
    private val toggleTodoCompleteUseCase: ToggleTodoCompleteUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TodoListUiState(
            todos = emptyList(),
            filter = TodoFilter.ALL,
            sortBy = TodoSortBy.CREATED_AT,
            direction = SortDirection.DESC
        )
    )
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    init {
        loadTodos(resetPage = true)
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(userEmail = result.data.email)
                    }
                }

                is AppResult.Error -> {}
            }
        }
    }

    fun onFilterChange(filter: TodoFilter) {
        _uiState.update {
            it.copy(filter = filter)
        }
        loadTodos(resetPage = true)
    }

    fun onSortChange(
        sortBy: TodoSortBy,
        direction: SortDirection
    ) {
        _uiState.update {
            it.copy(
                sortBy = sortBy,
                direction = direction
            )
        }
        loadTodos(resetPage = true)
    }

    fun onRefresh() {
        loadTodos(resetPage = true)
    }

    fun onLoadMore() {
        val state = _uiState.value
        if (!state.hasMore || state.isLoadingMore || state.isLoading) return
        loadTodos(resetPage = false)
    }

    private fun loadTodos(resetPage: Boolean) {
        val state = _uiState.value
        val targetPage = if (resetPage) 0 else state.currentPage + 1

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = resetPage,
                    isLoadingMore = !resetPage,
                    errorMessage = null
                )
            }
            when (
                val result = getTodosUseCase(
                    GetTodosUseCase.Params(
                        filter = state.filter,
                        sortBy = state.sortBy,
                        direction = state.direction,
                        page = targetPage
                    )
                )
            ) {
                is AppResult.Success -> {
                    val todo = result.data
                    _uiState.update {
                        it.copy(
                            todos = if (resetPage) todo.items else it.todos + todo.items,
                            currentPage = todo.page,
                            hasMore = todo.hasMore,
                            isLoading = false,
                            isLoadingMore = false
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = result.error.userMessage()
                        )
                    }
                }
            }
        }
    }

    fun onToggleComplete(todoId: String) {
        _uiState.update {
            it.copy(todos = it.todos.flipCompleted(todoId))
        }

        viewModelScope.launch {
            when (val result = toggleTodoCompleteUseCase(todoId)) {
                is AppResult.Success -> Unit
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            todos = it.todos.flipCompleted(todoId),
                            errorMessage = result.error.userMessage()
                        )
                    }
                }
            }
        }
    }

    private fun List<Todo>.flipCompleted(todoId: String): List<Todo> =
        map { todo -> if (todo.id == todoId) todo.copy(completed = !todo.completed) else todo }

    fun onDeleteRequested(todo: Todo) {
        commitPendingDelete()
        _uiState.update {
            it.copy(
                todos = it.todos.filterNot { existing -> existing.id == todo.id },
                pendingDelete = todo
            )
        }
    }

    fun onUndoDelete() {
        val todo = _uiState.value.pendingDelete ?: return
        _uiState.update {
            val restored = (it.todos + todo).sortedByDescending { item -> item.createdAt }
            it.copy(
                todos = restored,
                pendingDelete = null
            )
        }
    }

    fun commitPendingDelete() {
        val todo = _uiState.value.pendingDelete ?: return
        _uiState.update { it.copy(pendingDelete = null) }

        viewModelScope.launch {
            when (val result = deleteTodoUseCase(todo.id)) {
                is AppResult.Success -> Unit
                is AppResult.Error -> {
                    _uiState.update {
                        val restored = (it.todos + todo).sortedByDescending { item -> item.createdAt }
                        it.copy(
                            todos = restored,
                            errorMessage = result.error.userMessage()
                        )
                    }
                }
            }
        }
    }

    fun onLogoutClick() {
        val state = _uiState.value
        if (state.isLoggingOut) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoggingOut = true,
                    errorMessage = null
                )
            }

            when (val result = logoutUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            isLoggedOut = true
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            errorMessage = result.error.userMessage()
                        )
                    }
                }
            }
        }
    }

    fun consumeErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
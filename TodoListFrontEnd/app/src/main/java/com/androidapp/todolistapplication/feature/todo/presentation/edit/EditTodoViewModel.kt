package com.androidapp.todolistapplication.feature.todo.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.usecase.DeleteTodoUseCase
import com.androidapp.todolistapplication.feature.todo.domain.usecase.GetTodoUseCase
import com.androidapp.todolistapplication.feature.todo.domain.usecase.UpdateTodoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

@HiltViewModel(assistedFactory = EditTodoViewModel.Factory::class)
class EditTodoViewModel @AssistedInject constructor(
    private val getTodoUseCase: GetTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    @Assisted private val todoId: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(todoId: String): EditTodoViewModel
    }

    private val _uiState = MutableStateFlow(EditTodoUiState())
    val uiState: StateFlow<EditTodoUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadFailed = false, errorMessage = null) }

            when (val result = getTodoUseCase(todoId)) {
                is AppResult.Success -> {
                    val todo = result.data
                    _uiState.update {
                        it.copy(
                            title = todo.title,
                            description = todo.description.orEmpty(),
                            priority = todo.priority,
                            dueDate = todo.dueDate,
                            completed = todo.completed,
                            isLoading = false
                        )
                    }
                }

                is AppResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadFailed = true,
                        errorMessage = result.error.userMessage()
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update {
            it.copy(title = value.take(EditTodoUiState.MAX_TITLE_LENGTH), errorMessage = null)
        }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update {
            it.copy(description = value.take(EditTodoUiState.MAX_DESCRIPTION_LENGTH), errorMessage = null)
        }
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onDueDateChange(dueDate: Instant?) {
        _uiState.update { it.copy(dueDate = dueDate, showDatePicker = false) }
    }

    fun onDatePickerVisibilityChange(visible: Boolean) {
        _uiState.update { it.copy(showDatePicker = visible) }
    }

    fun onDeleteConfirmVisibilityChange(visible: Boolean) {
        _uiState.update { it.copy(showDeleteConfirm = visible) }
    }

    fun onSaveClick() {
        val state = _uiState.value
        if (!state.isFormValid || state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val result = updateTodoUseCase(
                UpdateTodoUseCase.Params(
                    id = todoId,
                    title = state.title.trim(),
                    description = state.description.trim().ifBlank { null },
                    dueDate = state.dueDate,
                    priority = state.priority,
                    completed = state.completed
                )
            )

            when (result) {
                is AppResult.Success -> _uiState.update { it.copy(isSaving = false, isSaved = true) }
                is AppResult.Error -> _uiState.update {
                    it.copy(isSaving = false, errorMessage = result.error.userMessage())
                }
            }
        }
    }

    fun onDeleteConfirmed() {
        if (_uiState.value.isDeleting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, showDeleteConfirm = false, errorMessage = null) }

            when (val result = deleteTodoUseCase(todoId)) {
                is AppResult.Success -> _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
                is AppResult.Error -> _uiState.update {
                    it.copy(isDeleting = false, errorMessage = result.error.userMessage())
                }
            }
        }
    }

    fun consumeErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

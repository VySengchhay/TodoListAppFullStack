package com.androidapp.todolistapplication.feature.todo.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.common.AppResult
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.usecase.CreateTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor(
    private val createTodoUseCase: CreateTodoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTodoUiState())
    val uiState: StateFlow<AddTodoUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.update {
            it.copy(
                title = value.take(AddTodoUiState.MAX_TITLE_LENGTH),
                errorMessage = null
            )
        }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update {
            it.copy(
                description = value.take(AddTodoUiState.MAX_DESCRIPTION_LENGTH),
                errorMessage = null
            )
        }
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onDueDateChange(dueDate: Instant?) {
        _uiState.update {
            it.copy(
                dueDate = dueDate,
                showDatePicker = false
            )
        }
    }

    fun onDatePickerVisibilityChange(visible: Boolean) {
        _uiState.update { it.copy(showDatePicker = visible) }
    }

    fun onSaveClick() {
        val state = _uiState.value
        if (!state.isFormValid || state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val result = createTodoUseCase(
                CreateTodoUseCase.Params(
                    title = state.title.trim(),
                    description = state.description.trim().ifBlank { null },
                    dueDate = state.dueDate,
                    priority = state.priority
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

    fun consumeErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

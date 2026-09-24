package com.androidapp.todolistapplication.feature.todo.presentation.edit

import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import java.time.Instant

data class EditTodoUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Instant? = null,
    val completed: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val showDatePicker: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val loadFailed: Boolean = false,
    val errorMessage: String? = null
) {
    val isTitleValid: Boolean
        get() = title.isNotBlank() && title.trim().length <= MAX_TITLE_LENGTH

    val isFormValid: Boolean
        get() = isTitleValid && !isLoading

    companion object {
        const val MAX_TITLE_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 500
    }
}

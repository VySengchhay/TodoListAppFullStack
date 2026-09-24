package com.androidapp.todolistapplication.feature.todo.presentation.add

import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import java.time.Instant

data class AddTodoUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Instant? = null,
    val showDatePicker: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
) {
    val isTitleValid: Boolean
        get() = title.isNotBlank() && title.trim().length <= MAX_TITLE_LENGTH

    val isFormValid: Boolean
        get() = isTitleValid

    companion object {
        const val MAX_TITLE_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 500
    }
}

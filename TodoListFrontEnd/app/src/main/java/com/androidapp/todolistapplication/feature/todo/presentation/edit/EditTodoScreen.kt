package com.androidapp.todolistapplication.feature.todo.presentation.edit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditTodoScreen(
    todoId: String,
    onTodoSaved: () -> Unit,
    onTodoDeleted: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditTodoViewModel = hiltViewModel<EditTodoViewModel, EditTodoViewModel.Factory>(
        creationCallback = { factory -> factory.create(todoId) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onTodoSaved()
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onTodoDeleted()
    }

    // While the form is on screen an error belongs in a snackbar; a failed *load*
    // owns the whole screen instead, so it isn't duplicated there.
    LaunchedEffect(uiState.errorMessage, uiState.loadFailed) {
        val message = uiState.errorMessage
        if (message != null && !uiState.loadFailed) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeErrorMessage()
        }
    }

    EditTodoContent(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriorityChange = viewModel::onPriorityChange,
        onDueDateChange = viewModel::onDueDateChange,
        onDatePickerVisibilityChange = viewModel::onDatePickerVisibilityChange,
        onDeleteConfirmVisibilityChange = viewModel::onDeleteConfirmVisibilityChange,
        onDeleteConfirmed = viewModel::onDeleteConfirmed,
        onSaveClick = viewModel::onSaveClick,
        onRetryLoad = viewModel::load,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

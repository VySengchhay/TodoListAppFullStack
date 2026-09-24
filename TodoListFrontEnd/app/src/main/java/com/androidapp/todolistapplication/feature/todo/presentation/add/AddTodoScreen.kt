package com.androidapp.todolistapplication.feature.todo.presentation.add

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddTodoScreen(
    onTodoCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onTodoCreated()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeErrorMessage()
        }
    }

    AddTodoContent(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriorityChange = viewModel::onPriorityChange,
        onDueDateChange = viewModel::onDueDateChange,
        onDatePickerVisibilityChange = viewModel::onDatePickerVisibilityChange,
        onSaveClick = viewModel::onSaveClick,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

package com.androidapp.todolistapplication.feature.todo.presentation.list

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.presentation.list.component.AccountBottomSheet

@Composable
fun TodoListScreen(
    onAddTodo: () -> Unit,
    onEditTodo: (Todo) -> Unit,
    onLogoutComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAccountSheet by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        viewModel.onRefresh()
        onPauseOrDispose {
            viewModel.commitPendingDelete()
        }
    }

    val pendingDelete = uiState.pendingDelete
    LaunchedEffect(pendingDelete?.id) {
        if (pendingDelete != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Deleted \"${pendingDelete.title}\"",
                actionLabel = "Undo",
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
            when (result) {
                SnackbarResult.ActionPerformed -> viewModel.onUndoDelete()
                SnackbarResult.Dismissed -> viewModel.commitPendingDelete()
            }
        }
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutComplete()
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.todos.isEmpty()) {
        val message = uiState.errorMessage
        if (message != null && uiState.todos.isNotEmpty()) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeErrorMessage()
        }
    }

    TodoListContent(
        uiState = uiState,
        onRetry = viewModel::onRefresh,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onFilterChange = viewModel::onFilterChange,
        onSortChange = viewModel::onSortChange,
        onAddTodo = onAddTodo,
        onTodoClick = onEditTodo,
        onToggleComplete = viewModel::onToggleComplete,
        onDeleteTodo = viewModel::onDeleteRequested,
        onAccountClick = { showAccountSheet = true },
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )

    if (showAccountSheet) {
        AccountBottomSheet(
            email = uiState.userEmail,
            isLoggingOut = uiState.isLoggingOut,
            onDismiss = { showAccountSheet = false },
            onLogoutClick = { viewModel.onLogoutClick() }
        )
    }
}

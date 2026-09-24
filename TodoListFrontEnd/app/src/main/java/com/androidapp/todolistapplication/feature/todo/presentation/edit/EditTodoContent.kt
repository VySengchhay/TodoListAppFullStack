package com.androidapp.todolistapplication.feature.todo.presentation.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.presentation.component.TodoDatePickerDialog
import com.androidapp.todolistapplication.feature.todo.presentation.component.TodoFormFields
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoContent(
    uiState: EditTodoUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onDueDateChange: (Instant?) -> Unit,
    onDatePickerVisibilityChange: (Boolean) -> Unit,
    onDeleteConfirmVisibilityChange: (Boolean) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onSaveClick: () -> Unit,
    onRetryLoad: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit todo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onDeleteConfirmVisibilityChange(true) },
                        enabled = !uiState.isLoading && !uiState.isDeleting && !uiState.loadFailed
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete todo"
                            )
                        }
                    }
                    TextButton(
                        onClick = onSaveClick,
                        enabled = uiState.isFormValid && !uiState.isSaving && !uiState.isDeleting
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                uiState.loadFailed -> Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(AppSpacing.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Couldn't load this todo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = onRetryLoad, modifier = Modifier.padding(top = AppSpacing.medium)) {
                        Text("Retry")
                    }
                }

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(horizontal = AppSpacing.screenHorizontal, vertical = AppSpacing.medium)
                ) {
                    TodoFormFields(
                        title = uiState.title,
                        onTitleChange = onTitleChange,
                        description = uiState.description,
                        onDescriptionChange = onDescriptionChange,
                        priority = uiState.priority,
                        onPriorityChange = onPriorityChange,
                        dueDate = uiState.dueDate,
                        onPickDate = { onDatePickerVisibilityChange(true) },
                        onClearDate = { onDueDateChange(null) },
                        titleMaxLength = EditTodoUiState.MAX_TITLE_LENGTH,
                        descriptionMaxLength = EditTodoUiState.MAX_DESCRIPTION_LENGTH
                    )
                }
            }
        }
    }

    if (uiState.showDatePicker) {
        TodoDatePickerDialog(
            initialDate = uiState.dueDate,
            onConfirm = onDueDateChange,
            onDismiss = { onDatePickerVisibilityChange(false) }
        )
    }

    if (uiState.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { onDeleteConfirmVisibilityChange(false) },
            icon = { Icon(Icons.Outlined.DeleteOutline, contentDescription = null) },
            title = { Text("Delete this todo?") },
            text = { Text("\"${uiState.title}\" will be removed. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = onDeleteConfirmed) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDeleteConfirmVisibilityChange(false) }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun previewContent(uiState: EditTodoUiState) {
    TodoListApplicationTheme {
        EditTodoContent(
            uiState = uiState,
            onTitleChange = {},
            onDescriptionChange = {},
            onPriorityChange = {},
            onDueDateChange = {},
            onDatePickerVisibilityChange = {},
            onDeleteConfirmVisibilityChange = {},
            onDeleteConfirmed = {},
            onSaveClick = {},
            onRetryLoad = {},
            onNavigateBack = {}
        )
    }
}

private fun loadedState() = EditTodoUiState(
    title = "Submit MIS assignment",
    description = "Chapter 4: database normalization exercises",
    priority = Priority.HIGH,
    dueDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
    isLoading = false
)

@Preview(name = "Loaded", showBackground = true)
@Composable
private fun EditTodoLoadedPreview() = previewContent(loadedState())

@Preview(name = "Loading", showBackground = true)
@Composable
private fun EditTodoLoadingPreview() = previewContent(EditTodoUiState())

@Preview(name = "Delete confirm", showBackground = true)
@Composable
private fun EditTodoDeletePreview() = previewContent(loadedState().copy(showDeleteConfirm = true))

@Preview(name = "Loaded - dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EditTodoDarkPreview() = previewContent(loadedState())

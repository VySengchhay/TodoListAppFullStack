package com.androidapp.todolistapplication.feature.todo.presentation.add

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun AddTodoContent(
    uiState: AddTodoUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onDueDateChange: (Instant?) -> Unit,
    onDatePickerVisibilityChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("New todo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSaveClick,
                        enabled = uiState.isFormValid && !uiState.isSaving
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                titleMaxLength = AddTodoUiState.MAX_TITLE_LENGTH,
                descriptionMaxLength = AddTodoUiState.MAX_DESCRIPTION_LENGTH
            )
        }
    }

    if (uiState.showDatePicker) {
        TodoDatePickerDialog(
            initialDate = uiState.dueDate,
            onConfirm = onDueDateChange,
            onDismiss = { onDatePickerVisibilityChange(false) }
        )
    }
}

@Composable
private fun previewContent(uiState: AddTodoUiState) {
    TodoListApplicationTheme {
        AddTodoContent(
            uiState = uiState,
            onTitleChange = {},
            onDescriptionChange = {},
            onPriorityChange = {},
            onDueDateChange = {},
            onDatePickerVisibilityChange = {},
            onSaveClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Empty form", showBackground = true)
@Composable
private fun AddTodoEmptyPreview() {
    previewContent(AddTodoUiState())
}

@Preview(name = "Filled form", showBackground = true)
@Composable
private fun AddTodoFilledPreview() {
    previewContent(
        AddTodoUiState(
            title = "Submit MIS assignment",
            description = "Chapter 4: database normalization exercises",
            priority = Priority.HIGH,
            dueDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
    )
}

@Preview(name = "Saving", showBackground = true)
@Composable
private fun AddTodoSavingPreview() {
    previewContent(AddTodoUiState(title = "Buy milk", isSaving = true))
}

@Preview(name = "Filled - dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddTodoDarkPreview() {
    previewContent(
        AddTodoUiState(
            title = "Submit MIS assignment",
            description = "Chapter 4: database normalization exercises",
            priority = Priority.HIGH,
            dueDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
    )
}

package com.androidapp.todolistapplication.feature.todo.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import java.time.Instant
import java.time.ZoneId

@Composable
fun TodoFormFields(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    priority: Priority,
    onPriorityChange: (Priority) -> Unit,
    dueDate: Instant?,
    onPickDate: () -> Unit,
    onClearDate: () -> Unit,
    titleMaxLength: Int,
    descriptionMaxLength: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.large)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                placeholder = { Text("What needs doing?") },
                singleLine = true,
                shape = RoundedCornerShape(AppSpacing.small),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                // The counter only appears once there is something to count.
                supportingText = title.takeIf { it.isNotEmpty() }?.let { { Text("${it.length} / $titleMaxLength") } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
                placeholder = { Text("Add any details (optional)") },
                shape = RoundedCornerShape(AppSpacing.small),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                ),
                supportingText = description.takeIf { it.isNotEmpty() }
                    ?.let { { Text("${it.length} / $descriptionMaxLength") } },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp)
            )
        }

        LabelledSection(label = "Priority") {
            PrioritySelector(selected = priority, onSelect = onPriorityChange)
        }

        LabelledSection(label = "Due date") {
            DueDateField(
                dueDate = dueDate,
                onPickDate = onPickDate,
                onClearDate = onClearDate
            )
        }
    }
}

@Composable
private fun LabelledSection(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDatePickerDialog(
    initialDate: Instant?,
    onConfirm: (Instant?) -> Unit,
    onDismiss: () -> Unit
) {
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate?.toEpochMilli())

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val picked = pickerState.selectedDateMillis?.let { millis ->
                        Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                    }
                    onConfirm(picked)
                },
                enabled = pickerState.selectedDateMillis != null
            ) {
                Text("OK")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = pickerState)
    }
}

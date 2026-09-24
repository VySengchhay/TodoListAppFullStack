package com.androidapp.todolistapplication.feature.todo.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority

@Composable
fun PrioritySelector(
    selected: Priority,
    onSelect: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH)

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, priority ->
            SegmentedButton(
                selected = priority == selected,
                onClick = { onSelect(priority) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                icon = {
                    // Only the selected option gets a tick.
                    if (priority == selected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                        )
                    }
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = priority.containerColor(),
                    activeContentColor = priority.contentColor()
                ),
                label = { Text(priority.label()) }
            )
        }
    }
}

@Composable
private fun Priority.containerColor() = when (this) {
    Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
    Priority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
    Priority.LOW -> MaterialTheme.colorScheme.secondaryContainer
}

@Composable
private fun Priority.contentColor() = when (this) {
    Priority.HIGH -> MaterialTheme.colorScheme.onErrorContainer
    Priority.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
    Priority.LOW -> MaterialTheme.colorScheme.onSecondaryContainer
}

private fun Priority.label(): String = when (this) {
    Priority.HIGH -> "High"
    Priority.MEDIUM -> "Medium"
    Priority.LOW -> "Low"
}

@Preview(showBackground = true)
@Composable
private fun PrioritySelectorPreview() {
    TodoListApplicationTheme {
        Row(modifier = Modifier.fillMaxWidth()) {
            PrioritySelector(selected = Priority.MEDIUM, onSelect = {})
        }
    }
}

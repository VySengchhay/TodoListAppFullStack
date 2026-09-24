package com.androidapp.todolistapplication.feature.todo.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme
import com.androidapp.todolistapplication.feature.todo.presentation.list.DueDateFormatter
import java.time.Instant
import java.time.temporal.ChronoUnit

/** Tap to open the date picker; the trailing × clears a chosen date. */
@Composable
fun DueDateField(
    dueDate: Instant?,
    onPickDate: () -> Unit,
    onClearDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = dueDate?.let { DueDateFormatter.formatWithDate(it) } ?: "No due date"

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPickDate)
            .semantics { contentDescription = "Due date: $label" },
        shape = RoundedCornerShape(AppSpacing.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AppSpacing.medium, top = AppSpacing.small, bottom = AppSpacing.small, end = AppSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (dueDate != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.weight(1f)
            )

            if (dueDate != null) {
                IconButton(onClick = onClearDate) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear due date",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DueDateFieldPreview() {
    TodoListApplicationTheme {
        DueDateField(
            dueDate = Instant.now().plus(1, ChronoUnit.DAYS),
            onPickDate = {},
            onClearDate = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun DueDateFieldEmptyPreview() {
    TodoListApplicationTheme {
        DueDateField(dueDate = null, onPickDate = {}, onClearDate = {})
    }
}

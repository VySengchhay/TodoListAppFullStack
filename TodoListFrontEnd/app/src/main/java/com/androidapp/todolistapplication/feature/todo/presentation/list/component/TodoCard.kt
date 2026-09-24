package com.androidapp.todolistapplication.feature.todo.presentation.list.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme
import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.presentation.list.DueDateFormatter
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoCard(
    todo: Todo,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onToggleComplete: (() -> Unit)? = null,
    clock: Clock = Clock.systemDefaultZone()
) {
    val overdue = todo.dueDate?.let { DueDateFormatter.isOverdue(it, todo.completed, clock) } == true

    val cardModifier = if (onToggleComplete == null) {
        modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
    } else {
        modifier.fillMaxWidth()
    }
    val shape = RoundedCornerShape(AppSpacing.medium)
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    val elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation
        ) {
            TodoCardBody(
                todo = todo,
                overdue = overdue,
                onToggleComplete = onToggleComplete,
                clock = clock
            )
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation
        ) {
            TodoCardBody(
                todo = todo,
                overdue = overdue,
                onToggleComplete = onToggleComplete,
                clock = clock
            )
        }
    }
}

@Composable
private fun TodoCardBody(
    todo: Todo,
    overdue: Boolean,
    onToggleComplete: (() -> Unit)?,
    clock: Clock
) {
    Column(
        modifier = Modifier
            .padding(AppSpacing.medium)
            .alpha(if (todo.completed) 0.6f else 1f),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onToggleComplete != null) {
                Checkbox(
                    checked = todo.completed,
                    onCheckedChange = { onToggleComplete() },
                    modifier = Modifier.semantics {
                        contentDescription = if (todo.completed) {
                            "Mark ${todo.title} not complete"
                        } else {
                            "Mark ${todo.title} complete"
                        }
                    }
                )
            } else if (todo.completed) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    // Not colour alone: "done" is stated for screen readers too.
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = todo.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration = if (todo.completed) TextDecoration.LineThrough else null,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        if (!todo.description.isNullOrBlank()) {
            Text(
                text = todo.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PriorityChip(priority = todo.priority)

            todo.dueDate?.let { dueDate ->
                DueDateLabel(
                    dueDate = dueDate,
                    isOverdue = overdue,
                    clock = clock
                )
            }
        }
    }
}

@Composable
private fun PriorityChip(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (container: Color, content: Color, label: String) = when (priority) {
        Priority.HIGH -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "High"
        )
        Priority.MEDIUM -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Medium"
        )
        Priority.LOW -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Low"
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AppSpacing.small),
        color = container
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = content,
            modifier = Modifier
                .padding(
                    horizontal = AppSpacing.small,
                    vertical = AppSpacing.extraSmall
                )
        )
    }
}

@Composable
private fun DueDateLabel(
    dueDate: Instant,
    isOverdue: Boolean,
    modifier: Modifier = Modifier,
    clock: Clock = Clock.systemDefaultZone()
) {
    val text = DueDateFormatter.format(dueDate, clock)
    val color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = if (isOverdue) "Overdue · $text" else text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

private fun previewTodo(
    title: String,
    description: String? = "Pick up the parcel before the shop closes",
    priority: Priority = Priority.MEDIUM,
    completed: Boolean = false,
    dueDaysFromNow: Long? = 0
) = Todo(
    id = title,
    title = title,
    description = description,
    dueDate = dueDaysFromNow?.let { Instant.now().plus(it, ChronoUnit.DAYS) },
    priority = priority,
    completed = completed,
    createdAt = Instant.now(),
    updatedAt = Instant.now()
)

@Preview(name = "Card - light", showBackground = true)
@Composable
private fun TodoCardPreview() {
    TodoListApplicationTheme {
        Column(
            modifier = Modifier.padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            TodoCard(todo = previewTodo("Buy milk", priority = Priority.HIGH))
            TodoCard(todo = previewTodo("Renew passport", description = null, priority = Priority.LOW, dueDaysFromNow = 3))
            TodoCard(todo = previewTodo("Pay rent", priority = Priority.HIGH, dueDaysFromNow = -2))
            TodoCard(todo = previewTodo("Archive old notes", completed = true, dueDaysFromNow = -5))
        }
    }
}

@Preview(name = "Card - with checkbox", showBackground = true)
@Composable
private fun TodoCardCheckboxPreview() {
    TodoListApplicationTheme {
        Column(
            modifier = Modifier.padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            TodoCard(todo = previewTodo("Buy milk", priority = Priority.HIGH), onToggleComplete = {})
            TodoCard(
                todo = previewTodo("Archive old notes", completed = true, dueDaysFromNow = -5),
                onToggleComplete = {}
            )
        }
    }
}

@Preview(name = "Card - dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoCardDarkPreview() {
    TodoCardPreview()
}

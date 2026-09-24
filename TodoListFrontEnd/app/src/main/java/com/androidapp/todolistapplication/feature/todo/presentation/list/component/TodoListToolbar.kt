package com.androidapp.todolistapplication.feature.todo.presentation.list.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme
import com.androidapp.todolistapplication.feature.todo.domain.model.SortDirection
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoSortBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListToolbar(
    filter: TodoFilter,
    sortBy: TodoSortBy,
    direction: SortDirection,
    onFilterChange: (TodoFilter) -> Unit,
    onSortChange: (TodoSortBy, SortDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppSpacing.screenHorizontal,
                vertical = AppSpacing.small
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            TodoFilter.entries.forEach { entry ->
                val selected = entry == filter
                FilterChip(
                    selected = selected,
                    onClick = {
                        onFilterChange(entry)
                    },
                    label = {
                        Text(entry.label())
                    },
                    leadingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }

        SortMenu(
            sortBy = sortBy,
            direction = direction,
            onSortChange = onSortChange
        )
    }
}

@Composable
private fun SortMenu(
    sortBy: TodoSortBy,
    direction: SortDirection,
    onSortChange: (TodoSortBy, SortDirection) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                expanded = true
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = "Sort by ${sortBy.label()}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            TodoSortBy.entries.forEach { entry ->
                val selected = entry == sortBy
                DropdownMenuItem(
                    text = {
                        Text(entry.label())
                   },
                    onClick = {
                        val newDirection = if (selected) direction.flip() else direction
                        onSortChange(entry, newDirection)
                        expanded = false
                    },
                    trailingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = if (direction == SortDirection.ASC) {
                                    Icons.Filled.ArrowUpward
                                } else {
                                    Icons.Filled.ArrowDownward
                                },
                                contentDescription = if (direction == SortDirection.ASC) "Ascending" else "Descending",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

private fun SortDirection.flip(): SortDirection =
    if (this == SortDirection.ASC) SortDirection.DESC else SortDirection.ASC

private fun TodoFilter.label(): String = when (this) {
    TodoFilter.ALL -> "All"
    TodoFilter.ACTIVE -> "Active"
    TodoFilter.COMPLETED -> "Completed"
}

private fun TodoSortBy.label(): String = when (this) {
    TodoSortBy.CREATED_AT -> "Date created"
    TodoSortBy.DUE_DATE -> "Due date"
    TodoSortBy.PRIORITY -> "Priority"
    TodoSortBy.TITLE -> "Title"
}

@Preview(showBackground = true)
@Composable
private fun TodoListToolbarPreview() {
    TodoListApplicationTheme {
        TodoListToolbar(
            filter = TodoFilter.ACTIVE,
            sortBy = TodoSortBy.DUE_DATE,
            direction = SortDirection.ASC,
            onFilterChange = {},
            onSortChange = { _, _ -> }
        )
    }
}

package com.androidapp.todolistapplication.feature.todo.presentation.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme

@Composable
fun AccountAvatar(
    email: String?,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    val initial = email?.trim()?.firstOrNull()?.uppercaseChar()
    val isLarge = size >= 56.dp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (initial != null) {
            Text(
                text = initial.toString(),
                style = if (isLarge) {
                    MaterialTheme.typography.headlineMedium
                } else {
                    MaterialTheme.typography.titleMedium
                },
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountAvatarPreview() {
    TodoListApplicationTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            AccountAvatar(email = "vy@example.com")
            AccountAvatar(email = null)
            AccountAvatar(email = "vy@example.com", size = 72.dp)
        }
    }
}

package com.androidapp.todolistapplication.feature.todo.presentation.list.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter

@Composable
fun TodoListEmptyState(
    modifier: Modifier = Modifier,
    filter: TodoFilter = TodoFilter.ALL
) {
    val (title, message) = when (filter) {
        TodoFilter.ALL -> "Nothing to do yet" to "Todos you create will show up here."
        TodoFilter.ACTIVE -> "All caught up" to "Nothing active right now. Nice work."
        TodoFilter.COMPLETED -> "Nothing completed yet" to "Todos you finish will show up here."
    }

    MessageState(
        icon = Icons.Outlined.TaskAlt,
        title = title,
        message = message,
        modifier = modifier
    )
}

@Composable
fun TodoListErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    MessageState(
        icon = Icons.Outlined.CloudOff,
        title = "Couldn't load your todos",
        message = message,
        modifier = modifier
    ) {
        Button(
            onClick = onRetry
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun TodoListLoadingState(
    modifier: Modifier = Modifier,
    placeholders: Int = 4
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "skeletonAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = AppSpacing.screenHorizontal, vertical = AppSpacing.small),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        repeat(placeholders) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppSpacing.medium))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(AppSpacing.medium)
                    .alpha(alpha),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
            ) {
                SkeletonBar(widthFraction = 0.6f, height = 20.dp)
                SkeletonBar(widthFraction = 0.9f, height = 14.dp)
                SkeletonBar(widthFraction = 0.3f, height = 14.dp)
            }
        }
    }
}

@Composable
private fun SkeletonBar(
    widthFraction: Float,
    height: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(
                RoundedCornerShape(AppSpacing.extraSmall)
            )
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    )
}

@Composable
private fun MessageState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = AppSpacing.large)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = AppSpacing.small)
        )

        if (action != null) {
            Box(
                modifier = Modifier
                    .padding(top = AppSpacing.large)
            ) {
                action()
            }
        }
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun EmptyPreview() {
    TodoListApplicationTheme { TodoListEmptyState() }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun ErrorPreview() {
    TodoListApplicationTheme {
        TodoListErrorState(message = "No internet connection. Please check your network", onRetry = {})
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun LoadingPreview() {
    TodoListApplicationTheme { TodoListLoadingState() }
}

@Preview(name = "Empty - dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyDarkPreview() {
    TodoListApplicationTheme { TodoListEmptyState() }
}

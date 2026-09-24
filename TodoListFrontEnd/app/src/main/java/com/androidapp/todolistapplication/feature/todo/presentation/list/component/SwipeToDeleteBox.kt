package com.androidapp.todolistapplication.feature.todo.presentation.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteBox(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance -> distance * 0.55f },
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onDelete()
            }
            false
        }
    )

    SwipeToDismissBox(
        state = state,
        modifier = modifier,
        backgroundContent = {
            val alignment = when (state.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                else -> Alignment.CenterEnd
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(AppSpacing.medium))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = AppSpacing.large),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        content = { content() }
    )
}

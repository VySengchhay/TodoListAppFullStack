package com.androidapp.todolistapplication.feature.todo.presentation.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountBottomSheet(
    email: String?,
    isLoggingOut: Boolean,
    onDismiss: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLogoutConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        AccountSheetContent(
            email = email,
            isLoggingOut = isLoggingOut,
            onLogoutClick = { showLogoutConfirm = true }
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = {
                showLogoutConfirm = false
                               },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null
                )
            },
            title = {
                Text("Log out?")
            },
            text = {
                Text("You'll need to sign in again to see your todos.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Log out")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutConfirm = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AccountSheetContent(
    email: String?,
    isLoggingOut: Boolean,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.screenHorizontal)
            .padding(bottom = AppSpacing.large)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        AccountAvatar(email = email, size = 72.dp)

        Spacer(modifier = Modifier.height(AppSpacing.extraSmall))

        Text(
            text = email ?: "Loading…",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Signed in",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.medium))

        OutlinedButton(
            onClick = onLogoutClick,
            enabled = !isLoggingOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppSpacing.componentHeight),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            if (isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.size(AppSpacing.small))
            Text(if (isLoggingOut) "Logging out…" else "Log out")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountSheetContentPreview() {
    TodoListApplicationTheme {
        AccountSheetContent(
            email = "vy@example.com",
            isLoggingOut = false,
            onLogoutClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountSheetContentLoggingOutPreview() {
    TodoListApplicationTheme {
        AccountSheetContent(
            email = "vy@example.com",
            isLoggingOut = true,
            onLogoutClick = {}
        )
    }
}

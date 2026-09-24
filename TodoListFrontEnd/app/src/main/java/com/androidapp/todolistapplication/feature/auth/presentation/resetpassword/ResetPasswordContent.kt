package com.androidapp.todolistapplication.feature.auth.presentation.resetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthHeader
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthPrimaryButton
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreenContent(
    uiState: ResetPasswordUiState,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onResetClick: () -> Unit,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(
                    horizontal = AppSpacing.screenHorizontal,
                    vertical = AppSpacing.screenVertical
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            AuthHeader(
                title = "Set a New Password",
                subtitle = "Use at least ${ResetPasswordUiState.MIN_PASSWORD_LENGTH} characters, " +
                    "with a letter and a number",
                icon = Icons.Filled.LockReset,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.extraLarge))

            AuthTextField(
                value = uiState.newPassword,
                onValueChange = onNewPasswordChange,
                label = "New password",
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                isError = uiState.newPassword.isNotEmpty() && !uiState.isPasswordStrong,
                errorText = "At least ${ResetPasswordUiState.MIN_PASSWORD_LENGTH} characters, " +
                    "with a letter and a number"
            )

            Spacer(modifier = Modifier.height(AppSpacing.medium))

            AuthTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm new password",
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                isError = uiState.confirmPassword.isNotEmpty() && !uiState.passwordsMatch,
                errorText = "Passwords do not match"
            )

            Spacer(modifier = Modifier.height(AppSpacing.large))

            AuthPrimaryButton(
                text = "Reset password",
                onClick = onResetClick,
                isLoading = uiState.isLoading,
                enabled = uiState.isFormValid
            )
        }
    }
}

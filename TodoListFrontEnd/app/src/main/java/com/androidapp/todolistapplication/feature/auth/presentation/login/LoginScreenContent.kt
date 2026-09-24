package com.androidapp.todolistapplication.feature.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthFooterRow
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthHeader
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthPrimaryButton
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthTextField

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = AppSpacing.screenHorizontal,
                    vertical = AppSpacing.screenVertical
                ),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.extraExtraLarge))

            AuthHeader(
                title = "Welcome Back",
                subtitle = "Sign in to continue to your account",
                icon = Icons.Filled.Lock,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.extraLarge))

            AuthTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.height(AppSpacing.medium))

            AuthTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Password",
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.height(AppSpacing.small))

            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(onClick = onForgotPasswordClick)
            )

            Spacer(modifier = Modifier.height(AppSpacing.large))

            AuthPrimaryButton(
                text = "Login",
                onClick = onLoginClick,
                isLoading = uiState.isLoading,
                enabled = uiState.isFormValid
            )

            Spacer(modifier = Modifier.height(AppSpacing.extraLarge))

            AuthFooterRow(
                normalText = "Don't have an account?",
                actionText = "Register",
                onActionClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.medium))
        }
    }
}
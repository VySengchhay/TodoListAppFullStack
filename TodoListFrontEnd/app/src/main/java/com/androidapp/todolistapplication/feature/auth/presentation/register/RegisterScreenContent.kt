package com.androidapp.todolistapplication.feature.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthFooterRow
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthHeader
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthPrimaryButton
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthTextField

@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
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
            Spacer(modifier = Modifier.height(AppSpacing.extraLarge))

            AuthHeader(
                title = "Create Account",
                subtitle = "Sign up to get started",
                icon = Icons.Filled.PersonAddAlt,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.medium))

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

            Spacer(modifier = Modifier.height(AppSpacing.medium))

            AuthTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm Password",
                isPassword = true,
                isPasswordVisible = uiState.isConfirmPasswordVisible,
                onTogglePasswordVisibility = onToggleConfirmPasswordVisibility,
                isError = uiState.passwordsMismatch,
                errorText = if (uiState.passwordsMismatch) "Passwords do not match" else null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.height(AppSpacing.large))

            AuthPrimaryButton(
                text = "Register",
                onClick = onRegisterClick,
                isLoading = uiState.isLoading,
                enabled = uiState.isFormValid
            )

            Spacer(modifier = Modifier.height(AppSpacing.extraLarge))

            AuthFooterRow(
                normalText = "Already have an account?",
                actionText = "Login",
                onActionClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.medium))
        }
    }
}
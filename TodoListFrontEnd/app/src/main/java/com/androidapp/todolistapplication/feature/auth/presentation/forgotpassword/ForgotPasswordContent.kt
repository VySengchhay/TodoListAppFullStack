package com.androidapp.todolistapplication.feature.auth.presentation.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthHeader
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthPrimaryButton
import com.androidapp.todolistapplication.feature.auth.presentation.component.AuthTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreenContent(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSendCodeClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackToLoginClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to login",
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
                ),
            verticalArrangement = Arrangement.Center
        ) {
            AuthHeader(
                title = "Forgot Password?",
                subtitle = "Enter your registered email and we'll send you a 6-digit code to reset your password",
                icon = Icons.Filled.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.extraLarge))

            AuthTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email,
                isError = uiState.email.isNotBlank() && !uiState.isEmailValid,
                errorText = "Enter a valid email address",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.height(AppSpacing.large))

            AuthPrimaryButton(
                text = "Send code",
                onClick = onSendCodeClick,
                isLoading = uiState.isLoading,
                enabled = uiState.isFormValid
            )
        }
    }
}

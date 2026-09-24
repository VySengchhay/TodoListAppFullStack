package com.androidapp.todolistapplication.feature.auth.presentation.otpverify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerifyScreenContent(
    uiState: OtpVerifyUiState,
    onOtpChange: (String) -> Unit,
    onResendClick: () -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Enter code") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = AppSpacing.screenHorizontal,
                    vertical = AppSpacing.screenVertical
                )
        ) {
            Text(
                text = "We sent a 6-digit code to ${uiState.email}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(AppSpacing.extraLarge))

            OutlinedTextField(
                value = uiState.otp,
                onValueChange = onOtpChange,
                label = { Text("6-digit code") },
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.medium))

            TextButton(
                onClick = onResendClick,
                enabled = !uiState.isResending
            ) {
                Text(if (uiState.isResending) "Sending..." else "Resend code")
            }

            Spacer(modifier = Modifier.height(AppSpacing.large))

            Button(
                onClick = onContinueClick,
                enabled = uiState.isOtpValid && !uiState.isVerifying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppSpacing.componentHeight)
            ) {
                Text(if (uiState.isVerifying) "Verifying..." else "Continue")
            }
        }
    }
}

package com.androidapp.todolistapplication.feature.auth.presentation.forgotpassword

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ForgotPasswordScreen(
    onCodeSent: (email: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isRequestSuccess) {
        if (uiState.isRequestSuccess) {
            onCodeSent(uiState.email.trim())
            viewModel.consumeRequestSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeErrorMessage()
        }
    }

    ForgotPasswordScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onSendCodeClick = viewModel::onSendCodeClick,
        onBackToLoginClick = onNavigateBack,
        snackbarHostState = snackbarHostState
    )
}

package com.androidapp.todolistapplication.feature.auth.presentation.register

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isRegisterSuccessful) {
        if (uiState.isRegisterSuccessful) onRegisterSuccess()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeErrorMessage()
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
        onRegisterClick = viewModel::onRegisterClick,
        onLoginClick = onNavigateToLogin,
        snackbarHostState = snackbarHostState
    )
}
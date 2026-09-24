package com.androidapp.todolistapplication.feature.auth.presentation.resetpassword

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ResetPasswordScreen(
    email: String,
    otp: String,
    onResetSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel<ResetPasswordViewModel, ResetPasswordViewModel.Factory>(
        creationCallback = { factory -> factory.create(email, otp) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isResetSuccessful) {
        if (uiState.isResetSuccessful) onResetSuccess()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeErrorMessage()
        }
    }

    ResetPasswordScreenContent(
        uiState = uiState,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onResetClick = viewModel::onResetClick,
        onBackClick = onNavigateBack,
        snackbarHostState = snackbarHostState
    )
}

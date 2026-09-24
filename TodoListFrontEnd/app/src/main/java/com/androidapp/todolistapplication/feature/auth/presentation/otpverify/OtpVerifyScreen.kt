package com.androidapp.todolistapplication.feature.auth.presentation.otpverify

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OtpVerifyScreen(
    email: String,
    onOtpEntered: (otp: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OtpVerifyViewModel = hiltViewModel<OtpVerifyViewModel, OtpVerifyViewModel.Factory>(
        creationCallback = { factory -> factory.create(email) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isOtpVerified) {
        if (uiState.isOtpVerified) {
            onOtpEntered(uiState.otp)
            viewModel.consumeOtpVerified()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeErrorMessage()
        }
    }

    LaunchedEffect(uiState.resendMessage) {
        uiState.resendMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeResendMessage()
        }
    }

    OtpVerifyScreenContent(
        uiState = uiState,
        onOtpChange = viewModel::onOtpChange,
        onResendClick = viewModel::onResendClick,
        onContinueClick = viewModel::onContinueClick,
        onBackClick = onNavigateBack,
        snackbarHostState = snackbarHostState
    )
}

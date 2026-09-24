package com.androidapp.todolistapplication.feature.auth.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {},
    isError: Boolean = false,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(label)
            },
            singleLine = true,
            isError = isError,
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (isPassword) {
                    IconButton(
                        onClick = onTogglePasswordVisibility
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            },
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType
            ),
            shape = RoundedCornerShape(AppSpacing.small),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && !errorText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(AppSpacing.extraExtraSmall))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = AppSpacing.extraSmall)
            )
        }
    }
}
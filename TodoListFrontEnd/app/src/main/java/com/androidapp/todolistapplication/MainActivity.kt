package com.androidapp.todolistapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme
import com.androidapp.todolistapplication.navigation.AppNavDisplay
import com.androidapp.todolistapplication.navigation.LoginRoute
import com.androidapp.todolistapplication.navigation.TodoListRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val localNetworkPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestLocalNetworkPermissionIfNeeded()
        setContent {
            TodoListApplicationTheme {
                val isLoggedIn by mainViewModel.isLoggedIn.collectAsStateWithLifecycle()
                val loggedIn = isLoggedIn

                if (loggedIn == null) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                } else {
                    val startRoute = remember { if (loggedIn) TodoListRoute else LoginRoute }
                    AppNavDisplay(
                        startRoute = startRoute,
                        isLoggedIn = loggedIn
                    )
                }
            }
        }
    }

    private fun requestLocalNetworkPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < 37) return
        val permission = Manifest.permission.ACCESS_LOCAL_NETWORK
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            localNetworkPermissionLauncher.launch(permission)
        }
    }
}

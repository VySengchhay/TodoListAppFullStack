package com.androidapp.todolistapplication

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Empty Hilt-enabled activity for instrumented tests, so they can call setContent { AppNavDisplay() }
 * without MainActivity's permission prompt. Lives in src/debug so it never ships in release builds.
 */
@AndroidEntryPoint
class HiltTestActivity : ComponentActivity()

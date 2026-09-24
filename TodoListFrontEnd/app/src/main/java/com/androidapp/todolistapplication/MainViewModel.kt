package com.androidapp.todolistapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidapp.todolistapplication.core.datastore.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    sessionManager: SessionManager
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean?> = sessionManager.isLoggedInFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)
}

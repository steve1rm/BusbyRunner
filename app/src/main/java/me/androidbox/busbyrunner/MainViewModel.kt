package me.androidbox.busbyrunner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.androidbox.core.domain.SessionStorage

class MainViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    var mainState by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            mainState = mainState.copy(isAuthenticating = true)
            mainState = mainState.copy(isLoggedIn = sessionStorage.get() != null)
            mainState = mainState.copy(isAuthenticating = false)
        }
    }
}
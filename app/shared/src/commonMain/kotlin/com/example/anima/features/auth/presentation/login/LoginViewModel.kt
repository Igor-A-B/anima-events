package com.example.anima.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import com.example.anima.features.auth.data.MockAccounts
import com.example.anima.features.auth.data.MockSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    // walks the mock list, so the button works with any number of accounts
    fun onSwitchAccount() {
        _uiState.update { state ->
            val accounts = MockAccounts.all
            val next = (accounts.indexOf(state.account) + 1) % accounts.size

            state.copy(account = accounts[next])
        }
    }

    fun onSubmit(onSuccess: () -> Unit) {
        // when backend ready, call usecase here
        MockSession.signIn(_uiState.value.account)
        onSuccess()
    }
}

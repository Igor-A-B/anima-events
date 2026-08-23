package com.example.anima.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onAccountTypeSelected(type: AccountType) {
        _uiState.update { it.copy(accountType = type) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }


    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onNextStep() {
        _uiState.update { state ->
            if (state.canAdvance && !state.isLastStep) {
                state.copy(step = state.step + 1)
            } else {
                state
            }
        }
    }

    fun onPreviousStep() {
        _uiState.update { state ->
            if (state.step > 1) {
                state.copy(step = state.step - 1)
            } else {
                state
            }
        }
    }

    fun onSubmit(){
        // when backend ready, call usecase here
    }
}
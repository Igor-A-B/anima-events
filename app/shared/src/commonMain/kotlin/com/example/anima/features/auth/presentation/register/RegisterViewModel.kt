package com.example.anima.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anima.features.auth.dtos.RegisterRequestDto
import com.anima.features.user.models.AccountType
import com.example.anima.core.error.AppExceptionBus
import com.example.anima.core.error.toAppError
import com.example.anima.features.auth.data.SessionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val session: SessionRepository,
    private val bus: AppExceptionBus,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onAccountTypeSelected(type: AccountType) {
        _uiState.update { it.copy(accountType = type) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
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

    // registering also signs the user in, onSuccess runs once the account exists
    fun onSubmit(onSuccess: () -> Unit) {
        val state = _uiState.value
        val accountType = state.accountType ?: return
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                session.register(RegisterRequestDto(state.name, state.email, state.password, accountType))
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.toAppError()) }
                bus.report(e)
            }
        }
    }
}
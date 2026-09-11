package com.example.anima.features.auth.presentation.login

import com.example.anima.features.auth.data.MockAccount
import com.example.anima.features.auth.data.MockAccounts

data class LoginUiState(
    // the account the card is showing, swapped by the change button
    val account: MockAccount = MockAccounts.all.first(),
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val canSubmit: Boolean = password.isNotBlank()
}

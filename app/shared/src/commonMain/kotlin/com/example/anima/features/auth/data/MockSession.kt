package com.example.anima.features.auth.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// who is signed in right now, so the screens after the login agree on it
// TODO: replace with a SessionRepository handed to the view models by DI
object MockSession {

    private val _account = MutableStateFlow(MockAccounts.all.first())

    val account: StateFlow<MockAccount> = _account.asStateFlow()

    fun signIn(account: MockAccount) {
        _account.value = account
    }
}

package com.example.anima.features.auth.data

import com.example.anima.features.auth.presentation.register.AccountType

// one of the accounts the login can sign in as while there is no backend
// TODO: drop once the API returns the real user
data class MockAccount(
    val id: String,
    val name: String,
    // the login card never shows the full address
    val maskedEmail: String,
    val accountType: AccountType,
)

object MockAccounts {

    val JohnDoe = MockAccount(
        id = "u2",
        name = "John Doe",
        maskedEmail = "jo*******@gmail.com",
        accountType = AccountType.VISITOR,
    )

    val Galpao9 = MockAccount(
        id = "u1",
        name = "Galpao 9",
        maskedEmail = "co*****@galpao9.com.br",
        accountType = AccountType.EXHIBITOR,
    )

    // order defines what the switch button cycles through
    val all: List<MockAccount> = listOf(JohnDoe, Galpao9)
}

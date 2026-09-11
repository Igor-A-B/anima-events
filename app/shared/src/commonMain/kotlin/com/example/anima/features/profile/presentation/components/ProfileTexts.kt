package com.example.anima.features.profile.presentation.components

import androidx.compose.runtime.Composable
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.profile_account_exhibitor
import anima.app.shared.generated.resources.profile_account_visitor
import anima.app.shared.generated.resources.profile_field_cnpj
import anima.app.shared.generated.resources.profile_field_cpf
import com.example.anima.features.auth.presentation.register.AccountType
import org.jetbrains.compose.resources.stringResource

// same idea as FeedTexts: the enum stays language free
@Composable
fun AccountType.label(): String = stringResource(
    when (this) {
        AccountType.VISITOR -> Res.string.profile_account_visitor
        AccountType.EXHIBITOR -> Res.string.profile_account_exhibitor
    }
)

// a visitor carries a CPF, an exhibitor a CNPJ
@Composable
fun AccountType.documentLabel(): String = stringResource(
    when (this) {
        AccountType.VISITOR -> Res.string.profile_field_cpf
        AccountType.EXHIBITOR -> Res.string.profile_field_cnpj
    }
)

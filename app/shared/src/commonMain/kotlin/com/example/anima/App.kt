package com.example.anima

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.auth.presentation.login.LoginScreen

@Composable
@Preview
fun App() {
    AnimaTheme {
        LoginScreen()
    }
}
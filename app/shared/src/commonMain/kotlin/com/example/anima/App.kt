package com.example.anima

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import com.example.anima.theme.AnimaTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.anima.feature.search.presentation.SearchEventsScreen
import com.example.anima.feature.home.presentation.HomeScreen
import com.example.anima.navigation.AppDestination
import com.example.anima.navigation.FloatingBottomNavigation
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun App() {
    var currentDestination by remember {
        mutableStateOf(AppDestination.HOME)
    }

    AnimaTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 32.dp,
                            vertical = 16.dp,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    FloatingBottomNavigation(
                        selectedDestination = currentDestination,
                        onDestinationSelected = { destination ->
                            currentDestination = destination
                        },
                    )
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                when (currentDestination) {
                    AppDestination.HOME -> {
                        HomeScreen(
                        )
                    }

                    AppDestination.SEARCH -> {
                        SearchEventsScreen()
                    }
                }
            }
        }
    }
}
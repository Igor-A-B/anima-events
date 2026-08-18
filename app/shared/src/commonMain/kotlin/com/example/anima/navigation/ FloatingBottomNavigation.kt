package com.example.anima.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun FloatingBottomNavigation(
    selectedDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.height(72.dp),
        ) {
            NavigationBarItem(
                selected = selectedDestination == AppDestination.HOME,
                onClick = {
                    onDestinationSelected(AppDestination.HOME)
                },
                icon = {
                    Text("⌂")
                },
                label = {
                    Text("Início")
                },
            )

            NavigationBarItem(
                selected = selectedDestination == AppDestination.SEARCH,
                onClick = {
                    onDestinationSelected(AppDestination.SEARCH)
                },
                icon = {
                    Text("⌕")
                },
                label = {
                    Text("Pesquisar")
                },
            )
        }
    }
}
package com.example.anima.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Home
import compose.icons.feathericons.Search
import compose.icons.feathericons.Plus
import compose.icons.feathericons.User

@Composable
fun FloatingBottomNavigation(
    selectedDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {

    val backgroundColor = Color(0xFF1E1E1E)
    val accentColor = Color(0xFFB82055)
    val darkIconColor = Color(0xFF111111)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = CircleShape,
        color = backgroundColor,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            NavigationItem(
                isSelected = selectedDestination == AppDestination.HOME,
                onClick = { onDestinationSelected(AppDestination.HOME) },
                icon = FeatherIcons.Home,
                accentColor = accentColor,
                selectedIconColor = darkIconColor
            )

            // Search
            NavigationItem(
                isSelected = selectedDestination == AppDestination.SEARCH,
                onClick = { onDestinationSelected(AppDestination.SEARCH) },
                icon = FeatherIcons.Search,
                accentColor = accentColor,
                selectedIconColor = darkIconColor
            )

            // Plus
            NavigationItem(
                isSelected = false,
                onClick = { /* sem rota ainda */ },
                icon = FeatherIcons.Plus,
                accentColor = accentColor,
                selectedIconColor = darkIconColor
            )

            // Profile
            NavigationItem(
                isSelected = false,
                onClick = { /* sem rota ainda */ },
                icon = FeatherIcons.User,
                accentColor = accentColor,
                selectedIconColor = darkIconColor
            )
        }
    }
}

@Composable
private fun NavigationItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    accentColor: Color,
    selectedIconColor: Color
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            // Se selecionado
            .background(if (isSelected) accentColor else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) selectedIconColor else accentColor,
            modifier = Modifier.size(28.dp)
        )
    }
}
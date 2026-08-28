package com.example.anima.core.components.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.anima_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun AnimaBrand(
    modifier: Modifier = Modifier,
    width: Dp = AnimaBrandDefaults.Width,
) {
    Image(
        painter = painterResource(Res.drawable.anima_logo),
        contentDescription = "Anima",
        modifier = modifier.width(width),
        contentScale = ContentScale.Fit,
    )
}

object AnimaBrandDefaults {
    val Width: Dp = 180.dp
}
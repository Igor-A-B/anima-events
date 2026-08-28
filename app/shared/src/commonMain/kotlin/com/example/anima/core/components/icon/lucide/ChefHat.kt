package com.example.anima.core.components.icon.lucide

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LucideChefHat: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideChefHat",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M17 21a1 1 0 0 0 1-1v-5.35c0-.457.316-.844.727-1.041a4 4 0 0 0-2.134-7.589 5 5 0 0 0-9.186 0 4 4 0 0 0-2.134 7.588c.411.198.727.585.727 1.041V20a1 1 0 0 0 1 1Z"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(17f, 21f)
                arcToRelative(1f, 1f, 0f, false, false, 1f, -1f)
                verticalLineToRelative(-5.35f)
                curveToRelative(0f, -0.457f, 0.316f, -0.844f, 0.727f, -1.041f)
                arcToRelative(4f, 4f, 0f, false, false, -2.134f, -7.589f)
                arcToRelative(5f, 5f, 0f, false, false, -9.186f, 0f)
                arcToRelative(4f, 4f, 0f, false, false, -2.134f, 7.588f)
                curveToRelative(0.411f, 0.198f, 0.727f, 0.585f, 0.727f, 1.041f)
                verticalLineTo(20f)
                arcToRelative(1f, 1f, 0f, false, false, 1f, 1f)
                close()
            }

            // path d="M6 17h12"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(6f, 17f)
                horizontalLineToRelative(12f)
            }
        }.build()
    }

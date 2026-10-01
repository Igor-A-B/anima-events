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

val LucideGlobe: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideGlobe",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // circle cx="12" cy="12" r="10"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(22f, 12f)
                arcTo(10f, 10f, 0f, false, true, 2f, 12f)
                arcTo(10f, 10f, 0f, false, true, 22f, 12f)
                close()
            }

            // meridian lens
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 2f)
                curveTo(6f, 6f, 6f, 18f, 12f, 22f)
                curveTo(18f, 18f, 18f, 6f, 12f, 2f)
                close()
            }

            // equator
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(2f, 12f)
                lineTo(22f, 12f)
            }
        }.build()
    }

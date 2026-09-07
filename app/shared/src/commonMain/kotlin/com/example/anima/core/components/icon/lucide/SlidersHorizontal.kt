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

val LucideSlidersHorizontal: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideSlidersHorizontal",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // the three rails
            listOf(
                Triple(21f, 14f, 4f),
                Triple(10f, 3f, 4f),
                Triple(21f, 12f, 12f),
                Triple(8f, 3f, 12f),
                Triple(21f, 16f, 20f),
                Triple(12f, 3f, 20f),
            ).forEach { (startX, endX, y) ->
                path(
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(startX, y)
                    lineTo(endX, y)
                }
            }

            // the three handles
            listOf(
                Triple(14f, 2f, 6f),
                Triple(8f, 10f, 14f),
                Triple(16f, 18f, 22f),
            ).forEach { (x, startY, endY) ->
                path(
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(x, startY)
                    lineTo(x, endY)
                }
            }
        }.build()
    }

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

val LucideShare: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideShare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // circle cx="18" cy="5" r="3"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(21f, 5f)
                arcTo(3f, 3f, 0f, false, true, 15f, 5f)
                arcTo(3f, 3f, 0f, false, true, 21f, 5f)
                close()
            }

            // circle cx="6" cy="12" r="3"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(9f, 12f)
                arcTo(3f, 3f, 0f, false, true, 3f, 12f)
                arcTo(3f, 3f, 0f, false, true, 9f, 12f)
                close()
            }

            // circle cx="18" cy="19" r="3"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(21f, 19f)
                arcTo(3f, 3f, 0f, false, true, 15f, 19f)
                arcTo(3f, 3f, 0f, false, true, 21f, 19f)
                close()
            }

            // line x1="8.59" x2="15.42" y1="13.51" y2="17.49"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(8.59f, 13.51f)
                lineTo(15.42f, 17.49f)
            }

            // line x1="15.41" x2="8.59" y1="6.51" y2="10.49"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(15.41f, 6.51f)
                lineTo(8.59f, 10.49f)
            }
        }.build()
    }
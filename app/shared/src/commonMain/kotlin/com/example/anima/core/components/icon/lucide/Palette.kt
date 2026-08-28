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

val LucidePalette: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucidePalette",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M12 22a1 1 0 0 1 0-20 10 9 0 0 1 10 9 5 5 0 0 1-5 5h-2.25a1.75 1.75 0 0 0-1.4 2.8l.3.4a1.75 1.75 0 0 1-1.4 2.8z"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 22f)
                arcToRelative(1f, 1f, 0f, false, true, 0f, -20f)
                arcToRelative(10f, 9f, 0f, false, true, 10f, 9f)
                arcToRelative(5f, 5f, 0f, false, true, -5f, 5f)
                horizontalLineToRelative(-2.25f)
                arcToRelative(1.75f, 1.75f, 0f, false, false, -1.4f, 2.8f)
                lineToRelative(0.3f, 0.4f)
                arcToRelative(1.75f, 1.75f, 0f, false, true, -1.4f, 2.8f)
                close()
            }

            // circle cx="13.5" cy="6.5" r=".5" fill="currentColor"
            path(
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(14.0f, 6.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 13.0f, 6.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 14.0f, 6.5f)
                close()
            }

            // circle cx="17.5" cy="10.5" r=".5" fill="currentColor"
            path(
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(18.0f, 10.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 17.0f, 10.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 18.0f, 10.5f)
                close()
            }

            // circle cx="6.5" cy="12.5" r=".5" fill="currentColor"
            path(
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(7.0f, 12.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 6.0f, 12.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 7.0f, 12.5f)
                close()
            }

            // circle cx="8.5" cy="7.5" r=".5" fill="currentColor"
            path(
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(9.0f, 7.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 8.0f, 7.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 9.0f, 7.5f)
                close()
            }
        }.build()
    }

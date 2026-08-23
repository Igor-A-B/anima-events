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

val LucideCircleUser: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideCircleUser",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M17.925 20.056a6 6 0 0 0-11.851.001"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(17.925f, 20.056f)
                arcToRelative(6f, 6f, 0f, false, false, -11.851f, 0.001f)
            }

            // circle cx="12" cy="11" r="4"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(16f, 11f)
                arcTo(4f, 4f, 0f, false, true, 8f, 11f)
                arcTo(4f, 4f, 0f, false, true, 16f, 11f)
                close()
            }

            // circle cx="12" cy="12" r="10"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(22f, 12f)
                arcTo(10f, 10f, 0f, false, true, 1.5f, 12f)
                arcTo(10f, 10f, 0f, false, true, 22f, 12f)
                close()
            }
        }.build()
    }
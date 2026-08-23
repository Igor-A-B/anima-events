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

val LucideFingerprint: ImageVector
    @Composable get() = remember {
        ImageVector.Builder(
            name = "LucideFingerprint",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M12 10a2 2 0 0 0-2 2c0 1.02-.1 2.51-.26 4"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 10f)
                arcToRelative(2f, 2f, 0f, false, false, -2f, 2f)
                curveToRelative(0f, 1.02f, -0.1f, 2.51f, -0.26f, 4f)
            }

            // path d="M14 13.12c0 2.38 0 6.38-1 8.88"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(14f, 13.12f)
                curveToRelative(0f, 2.38f, 0f, 6.38f, -1f, 8.88f)
            }

            // path d="M17.29 21.02c.12-.6.43-2.3.5-3.02"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(17.29f, 21.02f)
                curveToRelative(0.12f, -0.6f, 0.43f, -2.3f, 0.5f, -3.02f)
            }

            // path d="M2 12a10 10 0 0 1 18-6"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(2f, 12f)
                arcToRelative(10f, 10f, 0f, false, true, 18f, -6f)
            }

            // path d="M2 16h.01"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(2f, 16f)
                horizontalLineToRelative(0.01f)
            }

            // path d="M21.8 16c.2-2 .131-5.354 0-6"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(21.8f, 16f)
                curveToRelative(0.2f, -2f, 0.131f, -5.354f, 0f, -6f)
            }

            // path d="M5 19.5C5.5 18 6 15 6 12a6 6 0 0 1 .34-2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(5f, 19.5f)
                curveTo(5.5f, 18f, 6f, 15f, 6f, 12f)
                arcToRelative(6f, 6f, 0f, false, true, 0.34f, -2f)
            }

            // path d="M8.65 22c.21-.66.45-1.32.57-2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(8.65f, 22f)
                curveToRelative(0.21f, -0.66f, 0.45f, -1.32f, 0.57f, -2f)
            }

            // path d="M9 6.8a6 6 0 0 1 9 5.2v2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(9f, 6.8f)
                arcToRelative(6f, 6f, 0f, false, true, 9f, 5.2f)
                verticalLineToRelative(2f)
            }
        }.build()
    }
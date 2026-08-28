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

val LucideCpu: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideCpu",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M12 20v2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 20f)
                verticalLineToRelative(2f)
            }

            // path d="M12 2v2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 2f)
                verticalLineToRelative(2f)
            }

            // path d="M17 20v2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(17f, 20f)
                verticalLineToRelative(2f)
            }

            // path d="M17 2v2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(17f, 2f)
                verticalLineToRelative(2f)
            }

            // path d="M2 12h2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(2f, 12f)
                horizontalLineToRelative(2f)
            }

            // path d="M2 17h2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(2f, 17f)
                horizontalLineToRelative(2f)
            }

            // path d="M2 7h2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(2f, 7f)
                horizontalLineToRelative(2f)
            }

            // path d="M20 12h2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(20f, 12f)
                horizontalLineToRelative(2f)
            }

            // path d="M20 17h2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(20f, 17f)
                horizontalLineToRelative(2f)
            }

            // path d="M20 7h2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(20f, 7f)
                horizontalLineToRelative(2f)
            }

            // path d="M7 20v2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(7f, 20f)
                verticalLineToRelative(2f)
            }

            // path d="M7 2v2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(7f, 2f)
                verticalLineToRelative(2f)
            }

            // rect x="4" y="4" width="16" height="16" rx="2"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(6f, 4f)
                horizontalLineTo(18f)
                arcTo(2f, 2f, 0f, false, true, 20f, 6f)
                verticalLineTo(18f)
                arcTo(2f, 2f, 0f, false, true, 18f, 20f)
                horizontalLineTo(6f)
                arcTo(2f, 2f, 0f, false, true, 4f, 18f)
                verticalLineTo(6f)
                arcTo(2f, 2f, 0f, false, true, 6f, 4f)
                close()
            }

            // rect x="8" y="8" width="8" height="8" rx="1"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(9f, 8f)
                horizontalLineTo(15f)
                arcTo(1f, 1f, 0f, false, true, 16f, 9f)
                verticalLineTo(15f)
                arcTo(1f, 1f, 0f, false, true, 15f, 16f)
                horizontalLineTo(9f)
                arcTo(1f, 1f, 0f, false, true, 8f, 15f)
                verticalLineTo(9f)
                arcTo(1f, 1f, 0f, false, true, 9f, 8f)
                close()
            }
        }.build()
    }

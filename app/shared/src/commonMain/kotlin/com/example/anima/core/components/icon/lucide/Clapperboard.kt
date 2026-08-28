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

val LucideClapperboard: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideClapperboard",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="m12.296 3.464 3.02 3.956"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12.296f, 3.464f)
                lineToRelative(3.02f, 3.956f)
            }

            // path d="M20.2 6 3 11l-.9-2.4c-.3-1.1.3-2.2 1.3-2.5l13.5-4c1.1-.3 2.2.3 2.5 1.3z"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(20.2f, 6f)
                lineTo(3f, 11f)
                lineToRelative(-0.9f, -2.4f)
                curveToRelative(-0.3f, -1.1f, 0.3f, -2.2f, 1.3f, -2.5f)
                lineToRelative(13.5f, -4f)
                curveToRelative(1.1f, -0.3f, 2.2f, 0.3f, 2.5f, 1.3f)
                close()
            }

            // path d="M3 11h18v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(3f, 11f)
                horizontalLineToRelative(18f)
                verticalLineToRelative(8f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                horizontalLineTo(5f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
                close()
            }

            // path d="m6.18 5.276 3.1 3.899"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(6.18f, 5.276f)
                lineToRelative(3.1f, 3.899f)
            }
        }.build()
    }

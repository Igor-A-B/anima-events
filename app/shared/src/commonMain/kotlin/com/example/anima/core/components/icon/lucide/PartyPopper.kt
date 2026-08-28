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

val LucidePartyPopper: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucidePartyPopper",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M11 13c1.93 1.93 2.83 4.17 2 5-.83.83-3.07-.07-5-2-1.93-1.93-2.83-4.17-2-5 .83-.83 3.07.07 5 2Z"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(11f, 13f)
                curveToRelative(1.93f, 1.93f, 2.83f, 4.17f, 2f, 5f)
                curveToRelative(-0.83f, 0.83f, -3.07f, -0.07f, -5f, -2f)
                curveToRelative(-1.93f, -1.93f, -2.83f, -4.17f, -2f, -5f)
                curveToRelative(0.83f, -0.83f, 3.07f, 0.07f, 5f, 2f)
                close()
            }

            // path d="m11 2 .33.82c.34.86-.2 1.82-1.11 1.98C9.52 4.9 9 5.52 9 6.23V7"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(11f, 2f)
                lineToRelative(0.33f, 0.82f)
                curveToRelative(0.34f, 0.86f, -0.2f, 1.82f, -1.11f, 1.98f)
                curveTo(9.52f, 4.9f, 9f, 5.52f, 9f, 6.23f)
                verticalLineTo(7f)
            }

            // path d="M15 2h.01"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(15f, 2f)
                horizontalLineToRelative(0.01f)
            }

            // path d="m22 13-.82-.33c-.86-.34-1.82.2-1.98 1.11c-.11.7-.72 1.22-1.43 1.22H17"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(22f, 13f)
                lineToRelative(-0.82f, -0.33f)
                curveToRelative(-0.86f, -0.34f, -1.82f, 0.2f, -1.98f, 1.11f)
                curveToRelative(-0.11f, 0.7f, -0.72f, 1.22f, -1.43f, 1.22f)
                horizontalLineTo(17f)
            }

            // path d="m22 2-2.24.75a2.9 2.9 0 0 0-1.96 3.12c.1.86-.57 1.63-1.45 1.63h-.38c-.86 0-1.6.6-1.76 1.44L14 10"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(22f, 2f)
                lineToRelative(-2.24f, 0.75f)
                arcToRelative(2.9f, 2.9f, 0f, false, false, -1.96f, 3.12f)
                curveToRelative(0.1f, 0.86f, -0.57f, 1.63f, -1.45f, 1.63f)
                horizontalLineToRelative(-0.38f)
                curveToRelative(-0.86f, 0f, -1.6f, 0.6f, -1.76f, 1.44f)
                lineTo(14f, 10f)
            }

            // path d="M22 20h.01"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(22f, 20f)
                horizontalLineToRelative(0.01f)
            }

            // path d="M22 8h.01"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(22f, 8f)
                horizontalLineToRelative(0.01f)
            }

            // path d="M4 3h.01"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(4f, 3f)
                horizontalLineToRelative(0.01f)
            }

            // path d="M5.8 11.3 2 22l10.7-3.79"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(5.8f, 11.3f)
                lineTo(2f, 22f)
                lineToRelative(10.7f, -3.79f)
            }
        }.build()
    }

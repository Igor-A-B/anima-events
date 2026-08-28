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

val LucideSport: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideSport",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="m15 10.42 4.8-5.07"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(15f, 10.42f)
                lineToRelative(4.8f, -5.07f)
            }

            // path d="M19 18h3"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(19f, 18f)
                horizontalLineToRelative(3f)
            }

            // path d="M9.5 22 21.414 9.415A2 2 0 0 0 21.2 6.4l-5.61-4.208A1 1 0 0 0 14 3v2a2 2 0 0 1-1.394 1.906L8.677 8.053A1 1 0 0 0 8 9c-.155 6.393-2.082 9-4 9a2 2 0 0 0 0 4h14"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(9.5f, 22f)
                lineTo(21.414f, 9.415f)
                arcTo(2f, 2f, 0f, false, false, 21.2f, 6.4f)
                lineToRelative(-5.61f, -4.208f)
                arcTo(1f, 1f, 0f, false, false, 14f, 3f)
                verticalLineToRelative(2f)
                arcToRelative(2f, 2f, 0f, false, true, -1.394f, 1.906f)
                lineTo(8.677f, 8.053f)
                arcTo(1f, 1f, 0f, false, false, 8f, 9f)
                curveToRelative(-0.155f, 6.393f, -2.082f, 9f, -4f, 9f)
                arcToRelative(2f, 2f, 0f, false, false, 0f, 4f)
                horizontalLineToRelative(14f)
            }
        }.build()
    }

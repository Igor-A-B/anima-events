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

val LucideMapPin: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideMapPin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(20f, 10f)
                curveToRelative(0f, 4.993f, -5.539f, 10.193f, -7.399f, 11.799f)
                arcToRelative(1f, 1f, 0f, false, true, -1.202f, 0f)
                curveTo(9.539f, 20.193f, 4f, 14.993f, 4f, 10f)
                arcToRelative(8f, 8f, 0f, false, true, 16f, 0f)
            }

            // circle cx="12" cy="10" r="3"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(15f, 10f)
                arcTo(3f, 3f, 0f, false, true, 9f, 10f)
                arcTo(3f, 3f, 0f, false, true, 15f, 10f)
                close()
            }
        }.build()
    }
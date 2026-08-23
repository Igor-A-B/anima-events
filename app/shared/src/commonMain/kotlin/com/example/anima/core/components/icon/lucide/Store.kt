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

val LucideStore: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideStore",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M15 21v-5a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v5"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(15f, 21f)
                verticalLineToRelative(-5f)
                arcToRelative(1f, 1f, 0f, false, false, -1f, -1f)
                horizontalLineToRelative(-4f)
                arcToRelative(1f, 1f, 0f, false, false, -1f, 1f)
                verticalLineTo(21f)
            }

            // path d="M17.774 10.31a1.12 1.12 0 0 0-1.549 0 2.5 2.5 0 0 1-3.451 0 ..."
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(17.774f, 10.31f)
                arcToRelative(1.12f, 1.12f, 0f, false, false, -1.549f, 0f)
                arcToRelative(2.5f, 2.5f, 0f, false, true, -3.451f, 0f)
                arcToRelative(1.12f, 1.12f, 0f, false, false, -1.548f, 0f)
                arcToRelative(2.5f, 2.5f, 0f, false, true, -3.452f, 0f)
                arcToRelative(1.12f, 1.12f, 0f, false, false, -1.549f, 0f)
                arcToRelative(2.5f, 2.5f, 0f, false, true, -3.77f, -3.248f)
                lineToRelative(2.889f, -4.184f)
                arcTo(2f, 2f, 0f, false, true, 7f, 2f)
                horizontalLineTo(17f)
                arcToRelative(2f, 2f, 0f, false, true, 1.653f, 0.873f)
                lineToRelative(2.895f, 4.192f)
                arcToRelative(2.5f, 2.5f, 0f, false, true, -3.774f, 3.244f)
            }
            
            // path d="M4 10.95V19a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8.05"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(4f, 10.95f)
                verticalLineTo(19f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
                horizontalLineTo(18f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
                verticalLineToRelative(-8.05f)
            }
        }.build()
    }
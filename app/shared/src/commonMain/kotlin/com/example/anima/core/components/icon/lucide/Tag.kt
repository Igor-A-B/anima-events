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

val LucideTag: ImageVector
    @Composable
    get() = remember {
        ImageVector.Builder(
            name = "LucideTag",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // path d="M12.586 2.586A2 2 0 0 0 11.172 2H4a2 2 0 0 0-2 2v7.172a2 2 0 0 0 .586 1.414l8.704 8.704a2.426 2.426 0 0 0 3.42 0l6.58-6.58a2.426 2.426 0 0 0 0-3.42z"
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12.586f, 2.586f)
                arcToRelative(2f, 2f, 0f, false, false, -1.414f, -0.586f)
                horizontalLineTo(4f)
                arcToRelative(2f, 2f, 0f, false, false, -2f, 2f)
                verticalLineToRelative(7.172f)
                arcToRelative(2f, 2f, 0f, false, false, 0.586f, 1.414f)
                lineToRelative(8.704f, 8.704f)
                arcToRelative(2.426f, 2.426f, 0f, false, false, 3.42f, 0f)
                lineToRelative(6.58f, -6.58f)
                arcToRelative(2.426f, 2.426f, 0f, false, false, 0f, -3.42f)
                close()
            }

            // circle cx="7.5" cy="7.5" r=".5" fill="currentColor"
            path(
                fill = SolidColor(Color.Black),
                strokeLineWidth = 0f,
            ) {
                moveTo(8f, 7.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 7f, 7.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 8f, 7.5f)
                close()
            }
        }.build()
    }
package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalJournalTheme

enum class PaperPattern {
    PLAIN,
    RULED_LINES,
    DOT_GRID,
    SQUARE_GRID,
    VINTAGE_NEWSPAPER
}

/**
 * Journal Page Wrapper that renders realistic paper texture,
 * lined/dotted patterns, page margins, binder holes, and page numbers.
 */
@Composable
fun JournalPageSurface(
    modifier: Modifier = Modifier,
    pageNumber: Int? = null,
    pattern: PaperPattern = PaperPattern.RULED_LINES,
    hasLeftSpineMargin: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val theme = LocalJournalTheme.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.paperColor)
    ) {
        // Subtle drawn paper background (lines or dots or grid)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Spine shadow on the left edge
            if (hasLeftSpineMargin) {
                drawRect(
                    color = Color(0x0E000000),
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(16.dp.toPx(), h)
                )
                // Red vertical margin line
                drawLine(
                    color = Color(0x22D9534F),
                    start = Offset(24.dp.toPx(), 0f),
                    end = Offset(24.dp.toPx(), h),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Patterns
            when (pattern) {
                PaperPattern.RULED_LINES -> {
                    val lineSpacing = 28.dp.toPx()
                    var y = 50.dp.toPx()
                    val lineColor = Color(0x18000000)
                    while (y < h) {
                        drawLine(
                            color = lineColor,
                            start = Offset(if (hasLeftSpineMargin) 26.dp.toPx() else 12.dp.toPx(), y),
                            end = Offset(w - 12.dp.toPx(), y),
                            strokeWidth = 0.8.dp.toPx()
                        )
                        y += lineSpacing
                    }
                }
                PaperPattern.DOT_GRID -> {
                    val dotSpacing = 20.dp.toPx()
                    val dotColor = Color(0x1A000000)
                    var x = 24.dp.toPx()
                    while (x < w - 12.dp.toPx()) {
                        var y = 24.dp.toPx()
                        while (y < h - 12.dp.toPx()) {
                            drawCircle(color = dotColor, radius = 1.2.dp.toPx(), center = Offset(x, y))
                            y += dotSpacing
                        }
                        x += dotSpacing
                    }
                }
                PaperPattern.SQUARE_GRID -> {
                    val gridSpacing = 20.dp.toPx()
                    val gridColor = Color(0x10000000)
                    var x = 0f
                    while (x < w) {
                        drawLine(gridColor, Offset(x, 0f), Offset(x, h), 0.6.dp.toPx())
                        x += gridSpacing
                    }
                    var y = 0f
                    while (y < h) {
                        drawLine(gridColor, Offset(0f, y), Offset(w, y), 0.6.dp.toPx())
                        y += gridSpacing
                    }
                }
                PaperPattern.VINTAGE_NEWSPAPER -> {
                    // Two column divider line in middle
                    drawLine(
                        color = Color(0x22000000),
                        start = Offset(w / 2f, 80.dp.toPx()),
                        end = Offset(w / 2f, h - 40.dp.toPx()),
                        strokeWidth = 0.8.dp.toPx()
                    )
                }
                PaperPattern.PLAIN -> {}
            }
        }

        // Child Content
        content()

        // Page number at bottom right (handwritten)
        if (pageNumber != null) {
            Text(
                text = "- $pageNumber -",
                fontFamily = FontFamily.Cursive,
                fontSize = 13.sp,
                color = theme.secondaryTextColor,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 8.dp)
            )
        }
    }
}

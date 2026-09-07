package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalJournalTheme
import com.example.ui.theme.TapeWarmTerracotta

/**
 * Washi Tape strip with jagged/torn ends and soft translucent colors
 */
@Composable
fun WashiTape(
    modifier: Modifier = Modifier,
    color: Color = TapeWarmTerracotta.copy(alpha = 0.85f),
    width: Dp = 100.dp,
    height: Dp = 24.dp,
    rotation: Float = -2f,
    pattern: String = "stripes" // stripes, dots, plain
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .width(width)
            .height(height)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                // Left torn edge
                moveTo(0f, 0f)
                lineTo(4f, h * 0.25f)
                lineTo(0f, h * 0.5f)
                lineTo(5f, h * 0.75f)
                lineTo(0f, h)
                // Bottom edge
                lineTo(w, h)
                // Right torn edge
                lineTo(w - 4f, h * 0.75f)
                lineTo(w, h * 0.5f)
                lineTo(w - 5f, h * 0.25f)
                lineTo(w, 0f)
                close()
            }
            drawPath(path = path, color = color)

            // Subtle pattern
            if (pattern == "stripes") {
                val stripeColor = Color.White.copy(alpha = 0.25f)
                var x = -h
                while (x < w + h) {
                    drawLine(
                        color = stripeColor,
                        start = Offset(x, 0f),
                        end = Offset(x + h, h),
                        strokeWidth = 2.dp.toPx()
                    )
                    x += 12.dp.toPx()
                }
            } else if (pattern == "dots") {
                val dotColor = Color.White.copy(alpha = 0.3f)
                var x = 8.dp.toPx()
                while (x < w) {
                    drawCircle(color = dotColor, radius = 1.8.dp.toPx(), center = Offset(x, h / 2f))
                    x += 12.dp.toPx()
                }
            }
        }
    }
}

/**
 * Paperclip visual accessory for clipping notes/Polaroids
 */
@Composable
fun PaperClip(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFC5A059), // Antique gold brass
    rotation: Float = 15f
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .size(width = 16.dp, height = 36.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val stroke = Stroke(width = 2.2.dp.toPx())
            val path = Path().apply {
                moveTo(w * 0.3f, h * 0.75f)
                lineTo(w * 0.3f, h * 0.3f)
                cubicTo(w * 0.3f, h * 0.05f, w * 0.7f, h * 0.05f, w * 0.7f, h * 0.3f)
                lineTo(w * 0.7f, h * 0.85f)
                cubicTo(w * 0.7f, h * 0.98f, w * 0.1f, h * 0.98f, w * 0.1f, h * 0.7f)
                lineTo(w * 0.1f, h * 0.4f)
            }
            drawPath(path = path, color = color, style = stroke)
        }
    }
}

/**
 * Realistic Polaroid Photo Frame with shadow, slight tilt, and handwritten caption
 */
@Composable
fun PolaroidCard(
    modifier: Modifier = Modifier,
    rotation: Float = 0f,
    caption: String = "",
    dateText: String = "",
    hasPaperClip: Boolean = false,
    hasWashiTape: Boolean = false,
    content: @Composable () -> Unit
) {
    val theme = LocalJournalTheme.current
    Box(
        modifier = modifier
            .rotate(rotation)
            .padding(top = if (hasPaperClip || hasWashiTape) 12.dp else 0.dp)
    ) {
        // Physical shadow and paper container
        Box(
            modifier = Modifier
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(2.dp))
                .background(Color(0xFFFCFAF5), shape = RoundedCornerShape(2.dp))
                .border(width = 0.5.dp, color = Color(0x22000000), shape = RoundedCornerShape(2.dp))
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Photo area
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEDE8DE))
                        .border(0.5.dp, Color(0x15000000))
                ) {
                    content()
                }

                if (caption.isNotEmpty() || dateText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (caption.isNotEmpty()) {
                        Text(
                            text = caption,
                            fontFamily = FontFamily.Cursive,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = theme.textColor,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    if (dateText.isNotEmpty()) {
                        Text(
                            text = dateText,
                            fontFamily = FontFamily.Default,
                            fontSize = 11.sp,
                            color = theme.secondaryTextColor
                        )
                    }
                }
            }
        }

        // Optional Top Washi Tape
        if (hasWashiTape) {
            WashiTape(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-8).dp),
                width = 80.dp,
                height = 20.dp,
                rotation = -4f
            )
        }

        // Optional Paper Clip on top left
        if (hasPaperClip) {
            PaperClip(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 10.dp, y = (-12).dp),
                rotation = 5f
            )
        }
    }
}

/**
 * Journal Spine and ribbon bookmark visual accents
 */
@Composable
fun JournalBookmarkRibbon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFB23A2B),
    length: Dp = 60.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(22.dp)
            .height(length)
            .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h)
                lineTo(w / 2f, h - 8.dp.toPx()) // V-notch cut
                lineTo(0f, h)
                close()
            }
            // Drop shadow
            drawPath(path = path, color = color)
            // Subtle ribbon texture line
            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = Offset(w / 2f, 0f),
                end = Offset(w / 2f, h - 8.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}

/**
 * Stamp / Sticker visual element
 */
@Composable
fun JournalSticker(
    emojiOrText: String,
    modifier: Modifier = Modifier,
    rotation: Float = 6f,
    bgColor: Color = Color(0xFFFFF1C5),
    borderColor: Color = Color(0xFFD4B483)
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .shadow(2.dp, shape = CircleShape)
            .background(bgColor, shape = CircleShape)
            .border(1.dp, borderColor, shape = CircleShape)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emojiOrText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = Color(0xFF4A3E38)
        )
    }
}

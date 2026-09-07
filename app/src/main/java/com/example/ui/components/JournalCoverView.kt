package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JournalThemeConfig

@Composable
fun JournalCoverView(
    theme: JournalThemeConfig,
    userName: String,
    quote: String,
    stickers: String = "🌸,✨,📜",
    isOpen: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 3D cover opening animation
    val openRotationY by animateFloatAsState(
        targetValue = if (isOpen) -95f else 0f,
        animationSpec = tween(durationMillis = 750),
        label = "coverRotation"
    )

    Box(
        modifier = modifier
            .width(310.dp)
            .height(430.dp)
            .graphicsLayer {
                rotationY = openRotationY
                cameraDistance = 14f * density
            }
            .clickable { onClick() }
            .testTag("journal_cover_touch_target")
    ) {
        // Physical Journal Cover with realistic linen/leather texture
        val isMidnight = theme.name.contains("Midnight", ignoreCase = true)
        val isBotanical = theme.name.contains("Botanical", ignoreCase = true)
        val isY2K = theme.name.contains("Y2K", ignoreCase = true)
        val isVintage = theme.name.contains("Vintage", ignoreCase = true)

        val coverBaseColor = when {
            isMidnight -> Color(0xFF1B202B)
            isBotanical -> Color(0xFF384A3B)
            isY2K -> Color(0xFFE86F88)
            isVintage -> Color(0xFF422F24)
            else -> Color(0xFFDCD2C3) // Warm Linen Cream as in the reference!
        }

        val coverTextColor = when {
            isMidnight -> Color(0xFFE2E6EF)
            isBotanical -> Color(0xFFEAF0E8)
            isY2K -> Color(0xFF2C2226)
            isVintage -> Color(0xFFE8DCCB)
            else -> Color(0xFF2E241F) // Deep Charcoal / Warm Ink
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(
                        topStart = 6.dp,
                        bottomStart = 6.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            coverBaseColor.copy(alpha = 0.92f),
                            coverBaseColor,
                            coverBaseColor,
                            coverBaseColor.copy(alpha = 0.95f)
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 6.dp,
                        bottomStart = 6.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0x33000000),
                    shape = RoundedCornerShape(
                        topStart = 6.dp,
                        bottomStart = 6.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
        ) {
            // Subtle linen cloth fabric texture lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val grainColor = Color(0x0C000000)
                var y = 0f
                while (y < h) {
                    drawLine(grainColor, Offset(0f, y), Offset(w, y), 1.dp.toPx())
                    y += 4.dp.toPx()
                }
            }

            // Left Spine Crease
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0x38000000),
                                Color(0x10000000),
                                Color(0x40000000),
                                Color(0x00000000)
                            )
                        )
                    )
            )

            // Vertical Elastic Closure Strap running down the right-center
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-38).dp)
                    .width(12.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF635142),
                                Color(0xFF7A6553),
                                Color(0xFF564436)
                            )
                        )
                    )
                    .shadow(elevation = 2.dp)
            )

            // Antique Brass Heart Lock Charm on the elastic strap
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-31).dp)
                    .size(28.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .background(Color(0xFF8B5A3C), shape = CircleShape)
                    .border(1.5.dp, Color(0xFFC59F60), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(14.dp)) {
                    val w = size.width
                    val h = size.height
                    val heart = Path().apply {
                        moveTo(w * 0.5f, h * 0.85f)
                        cubicTo(w * 0.1f, h * 0.55f, 0f, h * 0.25f, w * 0.3f, h * 0.15f)
                        cubicTo(w * 0.45f, h * 0.1f, w * 0.5f, h * 0.3f, w * 0.5f, h * 0.35f)
                        cubicTo(w * 0.5f, h * 0.3f, w * 0.55f, h * 0.1f, w * 0.7f, h * 0.15f)
                        cubicTo(w * 1.0f, h * 0.25f, w * 0.9f, h * 0.55f, w * 0.5f, h * 0.85f)
                    }
                    drawPath(heart, color = Color(0xFFC59F60), style = Stroke(width = 1.6.dp.toPx()))
                }
            }

            // Hanging bookmark ribbon out of the bottom
            JournalBookmarkRibbon(
                color = theme.ribbonColor,
                length = 56.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = (-20).dp, y = 24.dp)
            )

            // Butterfly sticker on the upper left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 42.dp, y = 30.dp)
                    .rotate(-12f)
            ) {
                Text(
                    text = "🦋",
                    fontSize = 24.sp
                )
            }

            // Pressed Wildflowers tucked near the top right
            BotanicalDoodle(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 18.dp)
                    .rotate(14f),
                tint = Color(0xFF5A4838),
                flowerTint = Color(0xFF9E6553)
            )

            // Main Cover Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 48.dp, end = 48.dp, top = 60.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main Handwritten Title: "my corner ♡"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "my",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        color = coverTextColor
                    )
                    Text(
                        text = "corner",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = coverTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HeartDoodle(
                        color = coverTextColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Belongs to note
                Text(
                    text = userName.ifBlank { "fiza" },
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = coverTextColor.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                if (quote.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "“$quote”",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 13.sp,
                        color = coverTextColor.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }

                if (stickers.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        stickers.split(",").filter { it.isNotBlank() }.take(6).forEach { sticker ->
                            Text(
                                text = sticker.trim(),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

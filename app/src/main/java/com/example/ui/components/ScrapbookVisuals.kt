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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Botanical Flower and Leaf Doodle
 * Vector-drawn realistic pressed wildflowers, stems, lavender, and leaves.
 */
@Composable
fun BotanicalDoodle(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF6B5848),
    flowerTint: Color = Color(0xFF9E6553),
    scale: Float = 1f
) {
    Canvas(
        modifier = modifier
            .size(44.dp * scale, 60.dp * scale)
    ) {
        val w = size.width
        val h = size.height

        val stemPath = Path().apply {
            moveTo(w * 0.45f, h * 0.95f)
            cubicTo(
                w * 0.48f, h * 0.7f,
                w * 0.35f, h * 0.45f,
                w * 0.5f, h * 0.15f
            )
        }
        drawPath(
            path = stemPath,
            color = tint,
            style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
        )

        // Delicate Leaves
        val leaf1 = Path().apply {
            moveTo(w * 0.43f, h * 0.65f)
            cubicTo(w * 0.2f, h * 0.6f, w * 0.15f, h * 0.5f, w * 0.22f, h * 0.42f)
            cubicTo(w * 0.35f, h * 0.48f, w * 0.4f, h * 0.58f, w * 0.43f, h * 0.65f)
        }
        drawPath(path = leaf1, color = tint.copy(alpha = 0.85f), style = Fill)

        val leaf2 = Path().apply {
            moveTo(w * 0.44f, h * 0.45f)
            cubicTo(w * 0.65f, h * 0.42f, w * 0.75f, h * 0.32f, w * 0.7f, h * 0.24f)
            cubicTo(w * 0.55f, h * 0.3f, w * 0.47f, h * 0.38f, w * 0.44f, h * 0.45f)
        }
        drawPath(path = leaf2, color = tint.copy(alpha = 0.85f), style = Fill)

        // Blossoms at top
        drawCircle(
            color = flowerTint,
            radius = 3.5.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.15f)
        )
        drawCircle(
            color = flowerTint.copy(alpha = 0.9f),
            radius = 2.8.dp.toPx(),
            center = Offset(w * 0.38f, h * 0.22f)
        )
        drawCircle(
            color = flowerTint.copy(alpha = 0.9f),
            radius = 2.8.dp.toPx(),
            center = Offset(w * 0.62f, h * 0.20f)
        )
        drawCircle(
            color = Color(0xFFD4AF37), // golden center
            radius = 1.5.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.15f)
        )
    }
}

/**
 * Hand-drawn Heart doodle
 */
@Composable
fun HeartDoodle(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF3E2D23),
    strokeWidth: Dp = 1.6.dp
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.8f)
            cubicTo(w * 0.1f, h * 0.5f, 0f, h * 0.2f, w * 0.3f, h * 0.1f)
            cubicTo(w * 0.45f, h * 0.05f, w * 0.5f, h * 0.25f, w * 0.5f, h * 0.3f)
            cubicTo(w * 0.5f, h * 0.25f, w * 0.55f, h * 0.05f, w * 0.7f, h * 0.1f)
            cubicTo(w * 1.0f, h * 0.2f, w * 0.9f, h * 0.5f, w * 0.5f, h * 0.8f)
        }
        drawPath(path = path, color = color, style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round))
    }
}

/**
 * Hand-drawn Antique Padlock Doodle for Passcode and Lock pages
 */
@Composable
fun AntiqueLockDoodle(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF5A4434),
    shackleColor: Color = Color(0xFF8B6B4F),
    accentColor: Color = Color(0xFFD4AF37)
) {
    Canvas(modifier = modifier.size(36.dp, 44.dp)) {
        val w = size.width
        val h = size.height

        // Shackle arch at top
        val shacklePath = Path().apply {
            moveTo(w * 0.3f, h * 0.42f)
            lineTo(w * 0.3f, h * 0.25f)
            cubicTo(w * 0.3f, h * 0.05f, w * 0.7f, h * 0.05f, w * 0.7f, h * 0.25f)
            lineTo(w * 0.7f, h * 0.42f)
        }
        drawPath(
            path = shacklePath,
            color = shackleColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Lock Body (Warm rounded brass padlock)
        val bodyPath = Path().apply {
            moveTo(w * 0.18f, h * 0.42f)
            lineTo(w * 0.82f, h * 0.42f)
            cubicTo(w * 0.9f, h * 0.42f, w * 0.92f, h * 0.48f, w * 0.92f, h * 0.55f)
            lineTo(w * 0.88f, h * 0.88f)
            cubicTo(w * 0.86f, h * 0.96f, w * 0.78f, h * 0.98f, w * 0.5f, h * 0.98f)
            cubicTo(w * 0.22f, h * 0.98f, w * 0.14f, h * 0.96f, w * 0.12f, h * 0.88f)
            lineTo(w * 0.08f, h * 0.55f)
            cubicTo(w * 0.08f, h * 0.48f, w * 0.1f, h * 0.42f, w * 0.18f, h * 0.42f)
            close()
        }
        drawPath(path = bodyPath, color = color, style = Fill)
        drawPath(path = bodyPath, color = Color(0x33000000), style = Stroke(width = 1.2.dp.toPx()))

        // Antique Brass Keyhole
        drawCircle(
            color = accentColor,
            radius = 2.6.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.64f)
        )
        val keyholeSlot = Path().apply {
            moveTo(w * 0.46f, h * 0.65f)
            lineTo(w * 0.43f, h * 0.78f)
            lineTo(w * 0.57f, h * 0.78f)
            lineTo(w * 0.54f, h * 0.65f)
            close()
        }
        drawPath(path = keyholeSlot, color = accentColor, style = Fill)
    }
}

/**
 * Hand-drawn Curly Arrow Doodle
 */
@Composable
fun HandwrittenArrowDoodle(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF5A4434),
    strokeWidth: Dp = 1.6.dp
) {
    Canvas(modifier = modifier.size(36.dp, 28.dp)) {
        val w = size.width
        val h = size.height

        val arrowTail = Path().apply {
            moveTo(w * 0.1f, h * 0.85f)
            cubicTo(w * 0.25f, h * 0.95f, w * 0.45f, h * 0.7f, w * 0.65f, h * 0.4f)
            cubicTo(w * 0.75f, h * 0.25f, w * 0.85f, h * 0.25f, w * 0.9f, h * 0.3f)
        }
        drawPath(
            path = arrowTail,
            color = color,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        // Arrow head
        val head1 = Path().apply {
            moveTo(w * 0.7f, h * 0.15f)
            lineTo(w * 0.92f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.52f)
        }
        drawPath(
            path = head1,
            color = color,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

/**
 * Vintage Pencil / Fountain Pen Doodle
 */
@Composable
fun PencilDoodle(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6B5544)
) {
    Canvas(modifier = modifier.size(40.dp, 16.dp)) {
        val w = size.width
        val h = size.height

        // Pencil Body
        val pencilBody = Path().apply {
            moveTo(w * 0.35f, h * 0.25f)
            lineTo(w * 0.92f, h * 0.25f)
            lineTo(w * 0.92f, h * 0.75f)
            lineTo(w * 0.35f, h * 0.75f)
            close()
        }
        drawPath(pencilBody, color = Color(0xFFD4A373), style = Fill)
        drawPath(pencilBody, color = color, style = Stroke(width = 1.2.dp.toPx()))

        // Pencil Tip
        val tip = Path().apply {
            moveTo(w * 0.35f, h * 0.25f)
            lineTo(w * 0.1f, h * 0.5f)
            lineTo(w * 0.35f, h * 0.75f)
            close()
        }
        drawPath(tip, color = Color(0xFFEFE6D5), style = Fill)
        drawPath(tip, color = color, style = Stroke(width = 1.2.dp.toPx()))

        // Graphite Lead
        val lead = Path().apply {
            moveTo(w * 0.2f, h * 0.4f)
            lineTo(w * 0.1f, h * 0.5f)
            lineTo(w * 0.2f, h * 0.6f)
            close()
        }
        drawPath(lead, color = Color(0xFF2C2523), style = Fill)

        // Eraser at end
        val eraser = Path().apply {
            moveTo(w * 0.92f, h * 0.25f)
            lineTo(w * 0.98f, h * 0.25f)
            lineTo(w * 0.98f, h * 0.75f)
            lineTo(w * 0.92f, h * 0.75f)
            close()
        }
        drawPath(eraser, color = Color(0xFFE29578), style = Fill)
        drawPath(eraser, color = color, style = Stroke(width = 1.dp.toPx()))
    }
}

/**
 * Masked entry dot using hand-drawn little heart
 */
@Composable
fun HeartMaskedDot(
    isFilled: Boolean,
    modifier: Modifier = Modifier,
    filledColor: Color = Color(0xFF2C2523),
    unfilledColor: Color = Color(0xFF8A7E75)
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height

        val heartPath = Path().apply {
            moveTo(w * 0.5f, h * 0.85f)
            cubicTo(w * 0.1f, h * 0.55f, 0f, h * 0.25f, w * 0.3f, h * 0.15f)
            cubicTo(w * 0.45f, h * 0.1f, w * 0.5f, h * 0.3f, w * 0.5f, h * 0.35f)
            cubicTo(w * 0.5f, h * 0.3f, w * 0.55f, h * 0.1f, w * 0.7f, h * 0.15f)
            cubicTo(w * 1.0f, h * 0.25f, w * 0.9f, h * 0.55f, w * 0.5f, h * 0.85f)
            close()
        }

        if (isFilled) {
            drawPath(path = heartPath, color = filledColor, style = Fill)
        } else {
            drawPath(
                path = heartPath,
                color = unfilledColor,
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Realistic Washi Tape Strip with jagged torn ends and authentic textures
 */
@Composable
fun ScrapbookWashiTape(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE2A68C),
    width: Dp = 90.dp,
    height: Dp = 22.dp,
    rotation: Float = 0f,
    pattern: String = "stripes" // stripes, gingham, dots, plain
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .width(width)
            .height(height)
            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(1.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // Jagged realistic tear path
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(3f, h * 0.2f)
                lineTo(0f, h * 0.4f)
                lineTo(4f, h * 0.6f)
                lineTo(1f, h * 0.8f)
                lineTo(0f, h)

                lineTo(w, h)

                lineTo(w - 3f, h * 0.8f)
                lineTo(w, h * 0.6f)
                lineTo(w - 4f, h * 0.4f)
                lineTo(w - 1f, h * 0.2f)
                lineTo(w, 0f)
                close()
            }

            drawPath(path = path, color = color.copy(alpha = 0.88f))

            // Texture overlays
            when (pattern) {
                "stripes" -> {
                    val stripeColor = Color.White.copy(alpha = 0.35f)
                    var x = -h
                    while (x < w + h) {
                        drawLine(
                            color = stripeColor,
                            start = Offset(x, 0f),
                            end = Offset(x + h * 0.8f, h),
                            strokeWidth = 2.dp.toPx()
                        )
                        x += 10.dp.toPx()
                    }
                }
                "gingham" -> {
                    val gridColor = Color.White.copy(alpha = 0.28f)
                    var x = 0f
                    while (x < w) {
                        drawLine(gridColor, Offset(x, 0f), Offset(x, h), 3.dp.toPx())
                        x += 8.dp.toPx()
                    }
                    var y = 0f
                    while (y < h) {
                        drawLine(gridColor, Offset(0f, y), Offset(w, y), 3.dp.toPx())
                        y += 8.dp.toPx()
                    }
                }
                "dots" -> {
                    val dotColor = Color.White.copy(alpha = 0.38f)
                    var x = 6.dp.toPx()
                    while (x < w - 6.dp.toPx()) {
                        drawCircle(color = dotColor, radius = 1.8.dp.toPx(), center = Offset(x, h * 0.5f))
                        x += 9.dp.toPx()
                    }
                }
            }
        }
    }
}

/**
 * Taped Kraft Paper Note with handwritten phrase and washi tape
 */
@Composable
fun TapedPaperScrap(
    text: String,
    modifier: Modifier = Modifier,
    tapeColor: Color = Color(0xFFDDA15E),
    rotation: Float = 0f,
    fontFamily: FontFamily = FontFamily.Cursive,
    fontSize: Float = 14f,
    textColor: Color = Color(0xFF2C2523),
    bgColor: Color = Color(0xFFEFE8DA),
    tapeWidth: Dp = 55.dp
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .padding(top = 8.dp)
    ) {
        // Paper Card
        Box(
            modifier = Modifier
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(2.dp))
                .background(bgColor, shape = RoundedCornerShape(2.dp))
                .border(0.5.dp, Color(0x22000000), shape = RoundedCornerShape(2.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                fontFamily = fontFamily,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }

        // Taped top
        ScrapbookWashiTape(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp),
            color = tapeColor,
            width = tapeWidth,
            height = 16.dp,
            rotation = -2f,
            pattern = "stripes"
        )
    }
}

/**
 * Notebook Binder Rings / Stitched Spine
 */
@Composable
fun NotebookSpineMargin(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Shaded spine gradient
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0x25000000),
                    Color(0x10000000),
                    Color(0x00000000)
                )
            ),
            size = Size(18.dp.toPx(), h)
        )

        // Spiral binder holes
        val holeSpacing = 36.dp.toPx()
        val holeRadius = 4.dp.toPx()
        var y = 28.dp.toPx()
        while (y < h - 20.dp.toPx()) {
            // Hole shadow
            drawCircle(
                color = Color(0x35000000),
                radius = holeRadius + 0.8.dp.toPx(),
                center = Offset(11.dp.toPx(), y + 1.dp.toPx())
            )
            // Hole cutout
            drawCircle(
                color = Color(0xFF382920), // desk surface underneath
                radius = holeRadius,
                center = Offset(11.dp.toPx(), y)
            )
            // Silver wire loop
            drawArc(
                color = Color(0xFF888888),
                startAngle = 120f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(2.dp.toPx(), y - holeRadius),
                size = Size(14.dp.toPx(), holeRadius * 2),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
            y += holeSpacing
        }

        // Red / Soft pink margin line
        drawLine(
            color = Color(0x35D9534F),
            start = Offset(26.dp.toPx(), 0f),
            end = Offset(26.dp.toPx(), h),
            strokeWidth = 1.dp.toPx()
        )
    }
}

/**
 * Handmade Interactive Checkbox for Bullet Journaling
 */
@Composable
fun HandmadeHeartCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF2C2523)
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(20.dp)) {
            val w = size.width
            val h = size.height

            if (checked) {
                // Checkmark
                val checkPath = Path().apply {
                    moveTo(w * 0.15f, h * 0.5f)
                    lineTo(w * 0.45f, h * 0.85f)
                    lineTo(w * 0.9f, h * 0.2f)
                }
                drawPath(
                    path = checkPath,
                    color = Color(0xFF4A7C59), // Forest sage check
                    style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            } else {
                // Hand-drawn heart outline
                val heartPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.8f)
                    cubicTo(w * 0.1f, h * 0.5f, 0f, h * 0.2f, w * 0.3f, h * 0.1f)
                    cubicTo(w * 0.45f, h * 0.05f, w * 0.5f, h * 0.25f, w * 0.5f, h * 0.3f)
                    cubicTo(w * 0.5f, h * 0.25f, w * 0.55f, h * 0.05f, w * 0.7f, h * 0.1f)
                    cubicTo(w * 1.0f, h * 0.2f, w * 0.9f, h * 0.5f, w * 0.5f, h * 0.8f)
                }
                drawPath(
                    path = heartPath,
                    color = color.copy(alpha = 0.7f),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * Newspaper Headline Clipping for Collages and Vision Board
 */
@Composable
fun NewspaperSnippet(
    text: String,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    rotation: Float = 0f
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .shadow(2.dp, RoundedCornerShape(1.dp))
            .background(if (isDark) Color(0xFF24201E) else Color(0xFFF2EDE2), RoundedCornerShape(1.dp))
            .border(1.dp, if (isDark) Color(0xFF1E1B19) else Color(0xFFD4C8B8), RoundedCornerShape(1.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            fontSize = 13.sp,
            color = if (isDark) Color(0xFFFAF7F0) else Color(0xFF1E1D1B)
        )
    }
}

/**
 * Scrapbook Bottom Navigation Bar matching the Reference Image
 */
@Composable
fun ScrapbookBottomNavBar(
    selectedTabName: String,
    onTabSelect: (String) -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp)
            .background(Color(0xFFFBF8F2))
            .border(width = 1.dp, color = Color(0x18000000))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Today
            BottomNavItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Today",
                        tint = if (selectedTabName == "today") Color(0xFF2C2523) else Color(0xFF8A7E75),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = "Today",
                isSelected = selectedTabName == "today",
                onClick = { onTabSelect("today") }
            )

            // 2. Pages
            BottomNavItem(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Pages",
                        tint = if (selectedTabName == "pages") Color(0xFF2C2523) else Color(0xFF8A7E75),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = "Pages",
                isSelected = selectedTabName == "pages",
                onClick = { onTabSelect("pages") }
            )

            // 3. Center Action Button (+)
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .background(Color(0xFF2C2523), CircleShape)
                    .clickable { onActionClick() }
                    .testTag("scrapbook_action_plus"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    tint = Color(0xFFFBF8F2),
                    modifier = Modifier.size(26.dp)
                )
            }

            // 4. Collections / Vault (Grid)
            BottomNavItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Collections",
                        tint = if (selectedTabName == "collections" || selectedTabName == "memories") Color(0xFF2C2523) else Color(0xFF8A7E75),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = "Collections",
                isSelected = selectedTabName == "collections" || selectedTabName == "memories",
                onClick = { onTabSelect("collections") }
            )

            // 5. Me (Person)
            BottomNavItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Me",
                        tint = if (selectedTabName == "me") Color(0xFF2C2523) else Color(0xFF8A7E75),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = "Me",
                isSelected = selectedTabName == "me",
                onClick = { onTabSelect("me") }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: @Composable () -> Unit,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .testTag("nav_item_${label.lowercase()}")
    ) {
        icon()
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = FontFamily.Serif,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF2C2523) else Color(0xFF7A6E65)
        )
    }
}

package com.example.ui.screens.desk

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.UserSettingsEntity
import com.example.ui.components.AntiqueLockDoodle
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.HeartDoodle
import com.example.ui.components.HeartMaskedDot
import com.example.ui.components.JournalBookmarkRibbon
import com.example.ui.components.JournalCoverView
import com.example.ui.components.NewspaperSnippet
import com.example.ui.components.PaperClip
import com.example.ui.components.ScrapbookWashiTape
import com.example.ui.components.TapedPaperScrap
import com.example.ui.theme.getThemeByName
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun JournalDeskScreen(
    userSettings: UserSettingsEntity,
    isLocked: Boolean,
    onUnlockAttempt: (String) -> Boolean,
    onBiometricUnlock: () -> Unit,
    onEnterJournal: () -> Unit
) {
    val themeConfig = getThemeByName(userSettings.themeName)
    var showPasscodeDialog by remember { mutableStateOf(false) }
    var isCoverOpened by remember { mutableStateOf(false) }
    var showIntroPage by remember { mutableStateOf(false) }
    var isTurningToIntroToday by remember { mutableStateOf(false) }
    val introPageTurn = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isLocked) {
        if (isLocked) {
            isCoverOpened = false
            showIntroPage = false
            showPasscodeDialog = false
            isTurningToIntroToday = false
            introPageTurn.snapTo(0f)
        }
    }

    BackHandler(enabled = showIntroPage) {
        showIntroPage = false
        isCoverOpened = false
    }

    // Cozy overhead desk scene (warm vintage wood with warm light)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4A382C),
                        Color(0xFF33241B),
                        Color(0xFF221711)
                    ),
                    center = Offset(500f, 600f),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Wood grain lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val grainColor = Color(0x0AFFFFFF)
            var y = 0f
            while (y < h) {
                drawLine(grainColor, Offset(0f, y), Offset(w, y), 2.dp.toPx())
                y += 18.dp.toPx()
            }
        }

        if (!showIntroPage) {
            // Desk Ephemera layered around and underneath the journal

            // Taped note at top-left: "a safe space to be you ♡"
            TapedPaperScrap(
                text = "a safe space to be you ♡",
                rotation = -7f,
                tapeColor = Color(0xFFD49A76),
                bgColor = Color(0xFFF3EDE2),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 18.dp, y = 36.dp)
            )

            // Dried Botanical Lavender sprig at top right
            BotanicalDoodle(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 28.dp)
                    .rotate(24f),
                tint = Color(0xFF9E8A78),
                flowerTint = Color(0xFF8E7196),
                scale = 1.2f
            )

            // Vintage Postcard / Paris newspaper illustration under bottom left of journal
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 12.dp, y = 110.dp)
                    .rotate(-14f)
                    .shadow(8.dp, RoundedCornerShape(2.dp))
                    .background(Color(0xFFEFE6D5), RoundedCornerShape(2.dp))
                    .border(0.5.dp, Color(0x33000000), RoundedCornerShape(2.dp))
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "POST CARD",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize = 9.sp,
                        color = Color(0xFF6B5344)
                    )
                    Text(
                        text = "PARIS • 1926",
                        fontFamily = FontFamily.Serif,
                        fontSize = 7.sp,
                        color = Color(0xFF8A7160)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "🗼 ☕ 🕊️", fontSize = 16.sp)
                }
            }

            // Tilted Polaroid Card ("sunsets & soft thoughts ♡") tucked under bottom left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 24.dp, y = (-75).dp)
                    .rotate(-9f)
                    .shadow(10.dp, RoundedCornerShape(2.dp))
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(2.dp))
                    .border(0.5.dp, Color(0x22000000), RoundedCornerShape(2.dp))
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 65.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFFE89874), Color(0xFF8E6088), Color(0xFF4A4E69))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌅", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "sunsets & soft thoughts ♡",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 9.sp,
                        color = Color(0xFF2C2523)
                    )
                }
            }

            // Taped note at bottom-right: "good things take time ♡"
            TapedPaperScrap(
                text = "good things take time ♡",
                rotation = 8f,
                tapeColor = Color(0xFF9EABA2),
                bgColor = Color(0xFFF3EDE2),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-16).dp, y = (-85).dp)
            )

            // Vintage Brass Paperclip on bottom right
            PaperClip(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-40).dp, y = (-30).dp),
                rotation = -35f
            )

            // Center Column: Journal + Unlock Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                JournalCoverView(
                    theme = themeConfig,
                    userName = userSettings.userName,
                    quote = userSettings.coverQuote,
                    stickers = userSettings.coverStickers,
                    isOpen = isCoverOpened,
                    onClick = {
                        if (isLocked) {
                            showPasscodeDialog = true
                        } else {
                            isCoverOpened = true
                            showIntroPage = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Warm rounded capsule pill button: "tap to unlock my corner ♡ >"
                Box(
                    modifier = Modifier
                        .shadow(8.dp, shape = RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFFEFE8DA), Color(0xFFE8DFCD))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(1.dp, Color(0x33000000), shape = RoundedCornerShape(24.dp))
                        .clickable {
                            if (isLocked) {
                                showPasscodeDialog = true
                            } else {
                                isCoverOpened = true
                                showIntroPage = true
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .testTag("desk_unlock_action_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isLocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFF2C2523),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (isLocked) "tap to unlock my corner ♡ >" else "tap to open my corner ♡ >",
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C2523)
                        )
                    }
                }
            }
        } else {
            // WITH LOVE PAGE (Screen 3 in reference image)
            // Torn paper page over warm dark desk background
            Box(
                modifier = Modifier
                    .size(width = 330.dp, height = 520.dp),
                contentAlignment = Alignment.Center
            ) {
                // 1. UNDERNEATH PAGE: Today's Page inside the open journal
                // Revealed as the right edge of "with love," lifts and flips over to the left
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(20.dp, RoundedCornerShape(12.dp))
                        .background(Color(0xFFFAF7F0), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x33000000), RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "today's page",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF2C2523)
                            )
                            JournalBookmarkRibbon(color = themeConfig.ribbonColor, length = 32.dp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "a gentle new beginning ♡",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 15.sp,
                            color = Color(0xFF6B584C)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Ruled lines placeholder for Today page
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            repeat(10) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0x1A000000))
                                )
                            }
                        }
                    }

                    // Left spine red margin line
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                            .width(1.dp)
                            .offset(x = 10.dp)
                            .background(Color(0x22D9534F))
                    )

                    // Dynamic spine shadow cast by the turning page
                    if (isTurningToIntroToday) {
                        val shadowAlpha = (0.42f * (1f - introPageTurn.value)).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = shadowAlpha),
                                            Color.Black.copy(alpha = shadowAlpha * 0.4f),
                                            Color.Transparent
                                        ),
                                        startX = 0f,
                                        endX = 180f
                                    )
                                )
                        )
                    }
                }

                // 2. ON TOP: "WITH LOVE," PHYSICAL PAPER PAGE TURNING
                val turnProgress = introPageTurn.value
                val turnAngle = -88f * turnProgress

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0f, 0.5f) // Physical spine on the left
                            rotationY = turnAngle
                            cameraDistance = 14f * density
                            shadowElevation = ((1f - turnProgress) * 20.dp.toPx()).coerceAtLeast(0f)
                            alpha = if (turnProgress > 0.95f) 0f else 1f
                        }
                        .shadow(
                            elevation = if (turnProgress == 0f) 20.dp else ((1f - turnProgress) * 16).dp,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Color(0xFFFAF7F0), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x33000000), RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Pressed Flower
                        BotanicalDoodle(
                            modifier = Modifier.size(36.dp, 44.dp),
                            tint = Color(0xFF5A4838),
                            flowerTint = Color(0xFF9E6553)
                        )

                        // Handwritten "with love, [name] ♡" in deep charcoal ink
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "with love,",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E1D1B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${userSettings.userName.ifBlank { "fiza" }} ♡",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1D1B)
                            )
                        }

                        // Tilted Polaroid Card with paperclip and "you are enough ♡"
                        Box(
                            modifier = Modifier
                                .rotate(-3f)
                                .shadow(8.dp, RoundedCornerShape(3.dp))
                                .background(Color(0xFFFFFFFF), RoundedCornerShape(3.dp))
                                .border(1.dp, Color(0x18000000), RoundedCornerShape(3.dp))
                                .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 14.dp)
                        ) {
                            // Gold Paperclip on top-left of Polaroid
                            PaperClip(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = (-4).dp, y = (-12).dp),
                                rotation = -12f
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Warm artistic landscape photo
                                Box(
                                    modifier = Modifier
                                        .size(width = 160.dp, height = 110.dp)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                listOf(Color(0xFFE89A78), Color(0xFFDE7D66), Color(0xFF6B4D57))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🌅 🌾 🕊️", fontSize = 28.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                // Pinned kraft sticky note: "you are enough ♡"
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFEFE4D0), RoundedCornerShape(2.dp))
                                        .border(0.5.dp, Color(0x22000000), RoundedCornerShape(2.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "you are enough ♡",
                                        fontFamily = FontFamily.Cursive,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2C2523)
                                    )
                                }
                            }
                        }

                        // Doodled wild roses & text
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HeartDoodle(color = Color(0xFF2C2523), strokeWidth = 1.5.dp)
                                Text(
                                    text = "a little place for everything that matters.",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 15.sp,
                                    color = Color(0xFF4A3C34)
                                )
                                HeartDoodle(color = Color(0xFF2C2523), strokeWidth = 1.5.dp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // High contrast pill button: "turn to today 📖 →"
                            Button(
                                onClick = {
                                    if (!isTurningToIntroToday) {
                                        isTurningToIntroToday = true
                                        coroutineScope.launch {
                                            introPageTurn.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(
                                                    durationMillis = 520,
                                                    easing = CubicBezierEasing(0.35f, 0.0f, 0.25f, 1.0f)
                                                )
                                            )
                                            onEnterJournal()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24201E)),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(46.dp)
                                    .testTag("open_main_journal_button")
                            ) {
                                Text(
                                    text = "turn to today 📖",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFFBF8F2)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Turn to today",
                                    tint = Color(0xFFFBF8F2),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Subtle paper shadow darkening as it rotates into the light
                    if (turnProgress > 0f) {
                        val darkeningAlpha = (0.28f * turnProgress).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = darkeningAlpha))
                        )

                        // Visible paper right edge highlight catching light
                        val curlLight = (0.35f * sin(turnProgress * Math.PI.toFloat())).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(2.5.dp)
                                .background(Color.White.copy(alpha = curlLight))
                        )
                    }
                }
            }
        }

        // Passcode Screen Dialog (Screen 2 in reference image)
        if (showPasscodeDialog) {
            PasscodeUnlockDialog(
                onDismiss = { showPasscodeDialog = false },
                onUnlock = { pin ->
                    val success = onUnlockAttempt(pin)
                    if (success) {
                        showPasscodeDialog = false
                        isCoverOpened = true
                        showIntroPage = true
                    }
                    success
                },
                onBiometric = {
                    onBiometricUnlock()
                    showPasscodeDialog = false
                    isCoverOpened = true
                    showIntroPage = true
                }
            )
        }
    }
}

/**
 * Passcode Screen matching Screen 2 in Reference:
 * - Torn kraft header note: "same girl... bigger dreams ♡"
 * - "it's still you." heading in dark charcoal
 * - "enter your passcode"
 * - Round numeric keypad with paper-textured buttons
 * - "this is your space ♡"
 */
@Composable
fun PasscodeUnlockDialog(
    onDismiss: () -> Unit,
    onUnlock: (String) -> Boolean,
    onBiometric: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .shadow(24.dp, RoundedCornerShape(16.dp))
                .background(Color(0xFFFAF6EE), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0x33000000), RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Taped scrap banner at top: "same girl... bigger dreams ♡"
                TapedPaperScrap(
                    text = "same girl... bigger dreams ♡",
                    rotation = -1.5f,
                    tapeColor = Color(0xFFE2A68C),
                    bgColor = Color(0xFFEFE6D5),
                    fontSize = 13f,
                    textColor = Color(0xFF2C2523)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Antique Padlock sketch
                AntiqueLockDoodle(
                    modifier = Modifier.size(32.dp, 38.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Heading: "it's still you."
                Text(
                    text = "it's still you.",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF1E1D1B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "enter your passcode",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color(0xFF5A4D41)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Masked little hearts entry dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val maxDots = 4
                    for (i in 0 until maxDots) {
                        HeartMaskedDot(
                            isFilled = i < enteredPin.length,
                            filledColor = Color(0xFF24201E),
                            unfilledColor = Color(0xFF9E8E82),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFFB00020),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Keypad (3x4 grid with round paper sticker buttons)
                val keypad = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("bio", "0", "del")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    keypad.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.forEach { key ->
                                when (key) {
                                    "bio" -> {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .shadow(2.dp, CircleShape)
                                                .background(Color(0xFFEFE8DC), CircleShape)
                                                .border(1.dp, Color(0x22000000), CircleShape)
                                                .clickable { onBiometric() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Fingerprint,
                                                contentDescription = "Biometric",
                                                tint = Color(0xFF2C2523),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    "del" -> {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .shadow(2.dp, CircleShape)
                                                .background(Color(0xFFEFE8DC), CircleShape)
                                                .border(1.dp, Color(0x22000000), CircleShape)
                                                .clickable {
                                                    if (enteredPin.isNotEmpty()) {
                                                        enteredPin = enteredPin.dropLast(1)
                                                        errorMessage = ""
                                                    }
                                                }
                                                .testTag("keypad_del"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Backspace,
                                                contentDescription = "Delete",
                                                tint = Color(0xFF2C2523),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    else -> {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .shadow(2.dp, CircleShape)
                                                .background(Color(0xFFFAF7F0), CircleShape)
                                                .border(1.dp, Color(0x22000000), CircleShape)
                                                .clickable {
                                                    if (enteredPin.length < 6) {
                                                        val newPin = enteredPin + key
                                                        enteredPin = newPin
                                                        errorMessage = ""
                                                        if (newPin.length >= 4) {
                                                            val ok = onUnlock(newPin)
                                                            if (!ok && newPin.length == 4) {
                                                                // Allow up to 6 or show error
                                                            } else if (!ok && newPin.length >= 6) {
                                                                errorMessage = "incorrect passcode ♡"
                                                                enteredPin = ""
                                                            }
                                                        }
                                                    }
                                                }
                                                .testTag("keypad_$key"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = key,
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E1D1B)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Paper Strip: "this is your space ♡"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HeartDoodle(color = Color(0xFF5A4D41), strokeWidth = 1.2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "this is your space ♡",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5A4D41)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    HeartDoodle(color = Color(0xFF5A4D41), strokeWidth = 1.2.dp)
                }
            }
        }
    }
}

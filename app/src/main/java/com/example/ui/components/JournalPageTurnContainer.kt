package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class TurnDirection {
    FORWARD, BACKWARD
}

/**
 * Realistic Interactive Journal Page-Turn Container.
 *
 * Implements physical paper page turning:
 * - Dragging from the outer top-right corner or right edge curls the paper forward.
 * - Dragging from the outer top-left corner or left edge curls the paper backward.
 * - Central area is completely untouched so vertical scrolling is 100% unimpeded.
 * - Features dynamic 3D curl, visible paper underside, fold highlight, drop shadow,
 *   and dog-ear corner affordance.
 */
@Composable
fun <T> JournalPageTurnContainer(
    targetState: T,
    modifier: Modifier = Modifier,
    animationDurationMillis: Int = 450,
    canTurnForward: Boolean = true,
    canTurnBackward: Boolean = true,
    onTurnForward: () -> Unit = {},
    onTurnBackward: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    var currentState by remember { mutableStateOf(targetState) }
    var outgoingState by remember { mutableStateOf<T?>(null) }
    val pageTurnAnim = remember { Animatable(0f) }
    var animDirection by remember { mutableStateOf(TurnDirection.FORWARD) }

    // Interactive Drag State
    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }
    var dragDirection by remember { mutableStateOf(TurnDirection.FORWARD) }
    val interactiveProgress = remember { Animatable(0f) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    // Automated turn when targetState changes
    LaunchedEffect(targetState) {
        if (targetState != currentState && !isDragging) {
            outgoingState = currentState
            animDirection = TurnDirection.FORWARD
            pageTurnAnim.snapTo(0f)
            pageTurnAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = animationDurationMillis,
                    easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
                )
            )
            currentState = targetState
            outgoingState = null
            pageTurnAnim.snapTo(0f)
        }
    }

    val activeProgress = if (isDragging) interactiveProgress.value else pageTurnAnim.value
    val isTurning = activeProgress > 0.001f
    val currentTurnDir = if (isDragging) dragDirection else animDirection

    Box(modifier = modifier) {
        if (!isTurning && outgoingState == null) {
            // Idle State: content is rendered cleanly, zero gesture interception in center
            content(currentState)
        } else {
            val progress = activeProgress
            val isForward = currentTurnDir == TurnDirection.FORWARD

            // 1. Underneath Layer (Incoming Page / desk background)
            Box(modifier = Modifier.fillMaxSize()) {
                if (outgoingState != null) {
                    content(targetState)
                } else {
                    // During interactive drag, show underneath desk/paper
                    content(currentState)
                }

                // Dynamic drop shadow falling onto the underneath page
                val shadowAlpha = (0.45f * (1f - progress)).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = if (isForward) {
                                    listOf(
                                        Color.Black.copy(alpha = shadowAlpha * 0.7f),
                                        Color.Black.copy(alpha = shadowAlpha * 0.3f),
                                        Color.Transparent
                                    )
                                } else {
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = shadowAlpha * 0.3f),
                                        Color.Black.copy(alpha = shadowAlpha * 0.7f)
                                    )
                                }
                            )
                        )
                )
            }

            // 2. Outgoing / Curled Page (Lifting & Rotating in 3D perspective)
            val rotationAngle = if (isForward) -82f * progress else 82f * progress
            val spineOrigin = if (isForward) TransformOrigin(0f, 0.5f) else TransformOrigin(1f, 0.5f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = spineOrigin
                        rotationY = rotationAngle
                        cameraDistance = 16f * density.density
                        shadowElevation = ((1f - progress) * 16.dp.toPx()).coerceAtLeast(0f)
                        alpha = if (progress > 0.94f) 0f else 1f
                    }
            ) {
                // Page Content
                if (outgoingState != null) {
                    content(outgoingState!!)
                } else {
                    content(currentState)
                }

                // Paper darkening as it tilts away from light
                val darkeningAlpha = (0.28f * progress).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = darkeningAlpha))
                )

                // 3. Physical Paper Curl Cylinder & Underside on the outer edge
                val curlLight = (0.45f * sin(progress * Math.PI.toFloat())).coerceIn(0f, 1f)
                val curlCylinderWidth = (progress * 48.dp.value).coerceIn(8f, 50f).dp

                if (isForward) {
                    // Right Edge: Curled flap showing authentic paper underside & highlight
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(curlCylinderWidth)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.25f * curlLight),
                                        Color.White.copy(alpha = 0.5f * curlLight),
                                        Color(0xFFEDE4D4), // Warm paper underside
                                        Color(0xFFDFD4C0),
                                        Color.Black.copy(alpha = 0.3f * curlLight)
                                    )
                                )
                            )
                    )
                } else {
                    // Left Edge: Curled flap
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                            .width(curlCylinderWidth)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.3f * curlLight),
                                        Color(0xFFDFD4C0),
                                        Color(0xFFEDE4D4),
                                        Color.White.copy(alpha = 0.5f * curlLight),
                                        Color.Black.copy(alpha = 0.25f * curlLight)
                                    )
                                )
                            )
                    )
                }
            }
        }

        // ==========================================
        // TACTILE DOG-EAR CORNER AFFORDANCE & GESTURES
        // Outer 44.dp boundaries only -> 0% conflict with scrolling!
        // ==========================================

        // 1. FORWARD TURN: Top-Right Corner & Right Edge
        if (canTurnForward) {
            // Tactile Dog-Ear paper fold at top right corner
            if (!isTurning) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp)
                        .testTag("page_curl_dogear_forward")
                        .clickable {
                            coroutineScope.launch {
                                isDragging = true
                                dragDirection = TurnDirection.FORWARD
                                interactiveProgress.snapTo(0f)
                                interactiveProgress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(animationDurationMillis, easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f))
                                )
                                onTurnForward()
                                interactiveProgress.snapTo(0f)
                                isDragging = false
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val foldSize = 28.dp.toPx()
                        val path = Path().apply {
                            moveTo(w - foldSize, 0f)
                            lineTo(w, foldSize)
                            lineTo(w - foldSize, foldSize)
                            close()
                        }
                        // Drop shadow beneath dog-ear
                        drawPath(
                            path = Path().apply {
                                moveTo(w - foldSize - 2f, 0f)
                                lineTo(w, foldSize + 2f)
                                lineTo(w, 0f)
                                close()
                            },
                            color = Color(0x33000000)
                        )
                        // Paper underside
                        drawPath(
                            path = path,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFF9F5EC), Color(0xFFE5DAC7)),
                                start = Offset(w - foldSize, 0f),
                                end = Offset(w, foldSize)
                            )
                        )
                    }
                    Text(
                        text = "turn 📖",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 9.sp,
                        color = Color(0xFF7A6858),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 18.dp, end = 2.dp)
                    )
                }
            }

            // Right-edge gesture listener for dragging the curl
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(44.dp)
                    .pointerInput(canTurnForward) {
                        var accumulatedDrag = 0f
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isDragging = true
                                dragDirection = TurnDirection.FORWARD
                                accumulatedDrag = 0f
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    if (interactiveProgress.value > 0.32f) {
                                        interactiveProgress.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(300, easing = CubicBezierEasing(0.2f, 0f, 0.2f, 1f))
                                        )
                                        onTurnForward()
                                        interactiveProgress.snapTo(0f)
                                    } else {
                                        interactiveProgress.animateTo(0f, tween(200))
                                    }
                                    isDragging = false
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    interactiveProgress.animateTo(0f, tween(200))
                                    isDragging = false
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                if (dragAmount < 0 || accumulatedDrag > 0f) {
                                    accumulatedDrag -= dragAmount
                                    val frac = (accumulatedDrag / (screenWidthPx * 0.7f)).coerceIn(0f, 1f)
                                    coroutineScope.launch {
                                        interactiveProgress.snapTo(frac)
                                    }
                                }
                            }
                        )
                    }
            )
        }

        // 2. BACKWARD TURN: Left Edge
        if (canTurnBackward) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .width(44.dp)
                    .pointerInput(canTurnBackward) {
                        var accumulatedDrag = 0f
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isDragging = true
                                dragDirection = TurnDirection.BACKWARD
                                accumulatedDrag = 0f
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    if (interactiveProgress.value > 0.32f) {
                                        interactiveProgress.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(300, easing = CubicBezierEasing(0.2f, 0f, 0.2f, 1f))
                                        )
                                        onTurnBackward()
                                        interactiveProgress.snapTo(0f)
                                    } else {
                                        interactiveProgress.animateTo(0f, tween(200))
                                    }
                                    isDragging = false
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    interactiveProgress.animateTo(0f, tween(200))
                                    isDragging = false
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                if (dragAmount > 0 || accumulatedDrag > 0f) {
                                    accumulatedDrag += dragAmount
                                    val frac = (accumulatedDrag / (screenWidthPx * 0.7f)).coerceIn(0f, 1f)
                                    coroutineScope.launch {
                                        interactiveProgress.snapTo(frac)
                                    }
                                }
                            }
                        )
                    }
            )
        }
    }
}

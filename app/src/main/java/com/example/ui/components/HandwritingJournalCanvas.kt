package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.HandwritingStroke
import com.example.media.HandwritingStrokeHelper

enum class PenStyle(val label: String, val width: Float, val isHighlighter: Boolean = false) {
    FINE("Fine Pen", 3f),
    FOUNTAIN("Fountain Pen", 6f),
    BRUSH("Brush", 12f),
    HIGHLIGHTER("Highlighter", 24f, isHighlighter = true),
    ERASER("Eraser", 30f)
}

@Composable
fun HandwritingJournalCanvas(
    initialStrokesJson: String = "",
    paperColor: Color = Color(0xFFFBF8F2),
    height: Dp = 320.dp,
    onStrokesChanged: (List<HandwritingStroke>, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strokes = remember { mutableStateListOf<HandwritingStroke>() }
    val undoStack = remember { mutableStateListOf<List<HandwritingStroke>>() }
    val redoStack = remember { mutableStateListOf<List<HandwritingStroke>>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    var selectedPenStyle by remember { mutableStateOf(PenStyle.FOUNTAIN) }
    var selectedInkColor by remember { mutableStateOf(Color(0xFF1E1D1B)) }

    val inkColors = listOf(
        Color(0xFF1E1D1B), // Dark Ink
        Color(0xFF6B4226), // Antique Sepia
        Color(0xFF8B263E), // Burgundy Rose
        Color(0xFF2D5A43), // Forest Sage
        Color(0xFF1E3A5F), // Midnight Navy
        Color(0xFF5D3F6A)  // Vintage Mauve
    )

    // Load initial strokes once
    LaunchedEffect(initialStrokesJson) {
        if (initialStrokesJson.isNotBlank() && strokes.isEmpty()) {
            val loaded = HandwritingStrokeHelper.deserializeStrokes(initialStrokesJson)
            strokes.clear()
            strokes.addAll(loaded)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("handwriting_journal_container")
    ) {
        // Aesthetic Toolbar for Handwriting: Pens, Colors, Undo, Redo, Clear
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF4EFE6), RoundedCornerShape(10.dp))
                .border(0.5.dp, Color(0x335A4D41), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pen styles
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PenStyle.values().forEach { style ->
                        val isSel = selectedPenStyle == style
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSel) Color(0xFF2C2523) else Color(0xFFE8E0D2),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedPenStyle = style }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = style.label,
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = if (isSel) Color.White else Color(0xFF2C2523)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Ink palette
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    inkColors.forEach { color ->
                        val isSel = selectedInkColor == color && selectedPenStyle != PenStyle.ERASER
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (isSel) 2.dp else 0.5.dp,
                                    color = if (isSel) Color(0xFF2C2523) else Color(0x33000000),
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedInkColor = color
                                    if (selectedPenStyle == PenStyle.ERASER) {
                                        selectedPenStyle = PenStyle.FOUNTAIN
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Undo / Redo / Clear
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                redoStack.add(strokes.toList())
                                strokes.removeAt(strokes.size - 1)
                                onStrokesChanged(strokes.toList(), HandwritingStrokeHelper.serializeStrokes(strokes))
                            }
                        },
                        enabled = strokes.isNotEmpty(),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (strokes.isNotEmpty()) Color(0xFF2C2523) else Color(0x552C2523),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (redoStack.isNotEmpty()) {
                                val stateToRestore = redoStack.removeAt(redoStack.size - 1)
                                strokes.clear()
                                strokes.addAll(stateToRestore)
                                onStrokesChanged(strokes.toList(), HandwritingStrokeHelper.serializeStrokes(strokes))
                            }
                        },
                        enabled = redoStack.isNotEmpty(),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (redoStack.isNotEmpty()) Color(0xFF2C2523) else Color(0x552C2523),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                undoStack.add(strokes.toList())
                                strokes.clear()
                                redoStack.clear()
                                onStrokesChanged(emptyList(), "")
                            }
                        },
                        enabled = strokes.isNotEmpty(),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color(0xFF8A7E75),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Interactive Canvas directly embedded on paper
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(Color.Transparent)
                .pointerInput(selectedPenStyle, selectedInkColor) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPoints = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints = currentPoints + change.position
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty()) {
                                redoStack.clear()
                                val isEraser = selectedPenStyle == PenStyle.ERASER
                                val strokeColor = if (isEraser) {
                                    paperColor
                                } else if (selectedPenStyle.isHighlighter) {
                                    selectedInkColor.copy(alpha = 0.35f)
                                } else {
                                    selectedInkColor
                                }

                                val newStroke = HandwritingStroke(
                                    points = currentPoints,
                                    color = strokeColor,
                                    strokeWidth = selectedPenStyle.width,
                                    isEraser = isEraser
                                )
                                strokes.add(newStroke)
                                currentPoints = emptyList()
                                onStrokesChanged(strokes.toList(), HandwritingStrokeHelper.serializeStrokes(strokes))
                            }
                        }
                    )
                }
                .testTag("handwriting_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw existing saved strokes
                for (stroke in strokes) {
                    if (stroke.points.size > 1) {
                        val path = Path()
                        path.moveTo(stroke.points[0].x, stroke.points[0].y)
                        for (i in 1 until stroke.points.size) {
                            path.lineTo(stroke.points[i].x, stroke.points[i].y)
                        }
                        drawPath(
                            path = path,
                            color = stroke.color,
                            style = Stroke(
                                width = stroke.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    } else if (stroke.points.size == 1) {
                        drawCircle(
                            color = stroke.color,
                            radius = stroke.strokeWidth / 2f,
                            center = stroke.points[0]
                        )
                    }
                }

                // Draw active drag stroke in progress
                if (currentPoints.size > 1) {
                    val path = Path()
                    path.moveTo(currentPoints[0].x, currentPoints[0].y)
                    for (i in 1 until currentPoints.size) {
                        path.lineTo(currentPoints[i].x, currentPoints[i].y)
                    }
                    val activeColor = if (selectedPenStyle == PenStyle.ERASER) {
                        paperColor
                    } else if (selectedPenStyle.isHighlighter) {
                        selectedInkColor.copy(alpha = 0.35f)
                    } else {
                        selectedInkColor
                    }
                    drawPath(
                        path = path,
                        color = activeColor,
                        style = Stroke(
                            width = selectedPenStyle.width,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // Subtle indicator watermark if canvas is empty
            if (strokes.isEmpty() && currentPoints.isEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0x335A4D41),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "write or draw freely here with finger or stylus ♡",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 16.sp,
                        color = Color(0x445A4D41)
                    )
                }
            }
        }
    }
}

@Composable
fun ScrapbookHandwritingViewer(
    strokesJson: String,
    modifier: Modifier = Modifier
) {
    val strokes = remember(strokesJson) {
        HandwritingStrokeHelper.deserializeStrokes(strokesJson)
    }

    if (strokes.isEmpty()) return

    Canvas(modifier = modifier) {
        for (stroke in strokes) {
            if (stroke.isEraser) continue
            if (stroke.points.size > 1) {
                val path = Path()
                path.moveTo(stroke.points[0].x, stroke.points[0].y)
                for (i in 1 until stroke.points.size) {
                    path.lineTo(stroke.points[i].x, stroke.points[i].y)
                }
                drawPath(
                    path = path,
                    color = stroke.color,
                    style = Stroke(
                        width = stroke.strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            } else if (stroke.points.size == 1) {
                drawCircle(
                    color = stroke.color,
                    radius = stroke.strokeWidth / 2f,
                    center = stroke.points[0]
                )
            }
        }
    }
}


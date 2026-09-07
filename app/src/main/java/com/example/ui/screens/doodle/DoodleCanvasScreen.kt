package com.example.ui.screens.doodle

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.HandwritingStroke
import com.example.media.HandwritingStrokeHelper
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.theme.DarkParchment
import com.example.ui.theme.LocalJournalTheme
import com.example.ui.theme.WarmKraft

data class DrawnLine(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isHighlighter: Boolean = false
)

enum class DoodleBrush(val label: String, val width: Float, val isHighlighter: Boolean = false) {
    PEN("Pen", 4f),
    MARKER("Marker", 10f),
    HIGHLIGHTER("Highlighter", 22f, isHighlighter = true),
    ERASER("Eraser", 28f)
}

@Composable
fun DoodleCanvasScreen(
    onSaveDoodle: (String) -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalJournalTheme.current
    val lines = remember { mutableStateListOf<DrawnLine>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    var selectedBrush by remember { mutableStateOf(DoodleBrush.PEN) }
    var selectedColor by remember { mutableStateOf(Color(0xFF2C2523)) }
    var selectedPaperPattern by remember { mutableStateOf(PaperPattern.PLAIN) }
    var customPaperBg by remember { mutableStateOf(theme.paperColor) }

    val palette = listOf(
        Color(0xFF2C2523), // Ink Dark
        Color(0xFF8B4513), // Sepia Brown
        Color(0xFFD9534F), // Crimson Rose
        Color(0xFFE29578), // Terracotta
        Color(0xFFE9C46A), // Mustard
        Color(0xFF6B9080), // Sage Green
        Color(0xFF2E4057), // Vintage Blue
        Color(0xFF9E829C)  // Mauve
    )

    JournalPageSurface(
        pageNumber = 5,
        pattern = selectedPaperPattern,
        hasLeftSpineMargin = false
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = theme.textColor)
                    }
                    Text(
                        text = "doodle book 🎨",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = theme.textColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (lines.isNotEmpty()) lines.removeAt(lines.size - 1) },
                        enabled = lines.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = theme.textColor)
                    }
                    IconButton(onClick = { lines.clear() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = theme.secondaryTextColor)
                    }
                    Button(
                        onClick = {
                            val strokes = lines.map { line ->
                                HandwritingStroke(
                                    points = line.points,
                                    color = line.color,
                                    strokeWidth = line.strokeWidth,
                                    isEraser = false
                                )
                            }
                            val json = HandwritingStrokeHelper.serializeStrokes(strokes)
                            onSaveDoodle(json)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_doodle_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("save", fontSize = 12.sp, fontFamily = FontFamily.Serif)
                    }
                }
            }

            // Brush & Tools Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brushes
                DoodleBrush.values().forEach { brush ->
                    val isSelected = selectedBrush == brush
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) theme.coverColor else Color(0xFFEDE5D8),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedBrush = brush }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = brush.label,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Serif,
                            color = if (isSelected) Color.White else Color(0xFF2C2523)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Color Palette
                palette.forEach { color ->
                    val isSelected = selectedColor == color && selectedBrush != DoodleBrush.ERASER
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (isSelected) 2.5.dp else 0.5.dp,
                                color = if (isSelected) Color.White else Color(0x33000000),
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = color
                                if (selectedBrush == DoodleBrush.ERASER) selectedBrush = DoodleBrush.PEN
                            }
                    )
                }
            }

            // Paper Style Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "Plain" to PaperPattern.PLAIN,
                    "Lined" to PaperPattern.RULED_LINES,
                    "Dot Grid" to PaperPattern.DOT_GRID,
                    "Grid" to PaperPattern.SQUARE_GRID
                ).forEach { (label, pattern) ->
                    val isSel = selectedPaperPattern == pattern
                    Box(
                        modifier = Modifier
                            .background(if (isSel) theme.coverAccentColor else Color(0x18000000), RoundedCornerShape(4.dp))
                            .clickable { selectedPaperPattern = pattern }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = label, fontSize = 10.sp, color = if (isSel) Color.White else theme.textColor)
                    }
                }
            }

            // Interactive Drawing Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(selectedBrush, selectedColor) {
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
                                    val finalColor = if (selectedBrush == DoodleBrush.ERASER) {
                                        theme.paperColor
                                    } else if (selectedBrush.isHighlighter) {
                                        selectedColor.copy(alpha = 0.35f)
                                    } else {
                                        selectedColor
                                    }
                                    lines.add(
                                        DrawnLine(
                                            points = currentPoints,
                                            color = finalColor,
                                            strokeWidth = selectedBrush.width,
                                            isHighlighter = selectedBrush.isHighlighter
                                        )
                                    )
                                    currentPoints = emptyList()
                                }
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw committed lines
                    lines.forEach { line ->
                        if (line.points.size > 1) {
                            val path = Path().apply {
                                moveTo(line.points.first().x, line.points.first().y)
                                for (i in 1 until line.points.size) {
                                    lineTo(line.points[i].x, line.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = line.color,
                                style = Stroke(
                                    width = line.strokeWidth.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    // Draw current dragging line
                    if (currentPoints.size > 1) {
                        val path = Path().apply {
                            moveTo(currentPoints.first().x, currentPoints.first().y)
                            for (i in 1 until currentPoints.size) {
                                lineTo(currentPoints[i].x, currentPoints[i].y)
                            }
                        }
                        val liveColor = if (selectedBrush == DoodleBrush.ERASER) {
                            theme.paperColor
                        } else if (selectedBrush.isHighlighter) {
                            selectedColor.copy(alpha = 0.35f)
                        } else {
                            selectedColor
                        }
                        drawPath(
                            path = path,
                            color = liveColor,
                            style = Stroke(
                                width = selectedBrush.width.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens.braindump

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.StickyNoteEntity
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.WashiTape
import com.example.ui.theme.LocalJournalTheme
import com.example.ui.theme.StickyLavender
import com.example.ui.theme.StickyMint
import com.example.ui.theme.StickyPeach
import com.example.ui.theme.StickyRose
import com.example.ui.theme.StickySky
import com.example.ui.theme.StickyYellow
import kotlin.math.roundToInt

@Composable
fun BrainDumpScreen(
    notes: List<StickyNoteEntity>,
    onAddNote: (String, Long) -> Unit,
    onUpdateNote: (StickyNoteEntity) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onConvertToJournal: (StickyNoteEntity) -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalJournalTheme.current
    var showAddDialog by remember { mutableStateOf(false) }

    JournalPageSurface(
        pageNumber = 6,
        pattern = PaperPattern.DOT_GRID,
        hasLeftSpineMargin = false
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
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
                    Column {
                        Text(
                            text = "currently occupying my brain:",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textColor
                        )
                        Text(
                            text = "drag, pin, or crumple thoughts",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = theme.secondaryTextColor
                        )
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_sticky_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Sticky", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("new note", fontSize = 12.sp, fontFamily = FontFamily.Serif)
                }
            }

            // Freeform Board Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                if (notes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "your brain is calm and quiet 🌿\ntap 'new note' to stick a thought here.",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 16.sp,
                            color = theme.secondaryTextColor
                        )
                    }
                } else {
                    notes.forEach { note ->
                        DraggableStickyNote(
                            note = note,
                            onMove = { dx, dy ->
                                onUpdateNote(note.copy(offsetX = note.offsetX + dx, offsetY = note.offsetY + dy))
                            },
                            onDelete = { onDeleteNote(note.id) },
                            onConvert = { onConvertToJournal(note) }
                        )
                    }
                }
            }
        }
    }

    // Add Sticky Note Dialog
    if (showAddDialog) {
        var textInput by remember { mutableStateOf("") }
        var chosenColor by remember { mutableStateOf(StickyYellow) }

        val stickyColors = listOf(StickyYellow, StickyRose, StickyMint, StickyLavender, StickyPeach, StickySky)

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .shadow(12.dp, RoundedCornerShape(10.dp))
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(10.dp))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "stick a thought 📝",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = theme.textColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Color choice
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        stickyColors.forEach { col ->
                            val isSel = chosenColor == col
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(col, CircleShape)
                                    .border(if (isSel) 2.dp else 0.5.dp, if (isSel) Color.Black else Color.Gray, CircleShape)
                                    .clickable { chosenColor = col }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("what's occupying your thoughts right now?") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                onAddNote(textInput, chosenColor.value.toLong())
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("pin note 📌", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableStickyNote(
    note: StickyNoteEntity,
    onMove: (Float, Float) -> Unit,
    onDelete: () -> Unit,
    onConvert: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(note.offsetX) }
    var offsetY by remember { mutableFloatStateOf(note.offsetY) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .rotate(note.rotation)
            .shadow(5.dp, RoundedCornerShape(2.dp))
            .background(Color(note.colorHex), RoundedCornerShape(2.dp))
            .border(0.5.dp, Color(0x22000000), RoundedCornerShape(2.dp))
            .width(150.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    onMove(dragAmount.x, dragAmount.y)
                }
            }
            .padding(10.dp)
    ) {
        Column {
            // Little washi tape on top of sticky
            WashiTape(
                width = 50.dp,
                height = 14.dp,
                rotation = -4f,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.text,
                fontFamily = FontFamily.Cursive,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = Color(0xFF2C2523)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Actions row: Convert to Journal & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onConvert,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Convert to page",
                        tint = Color(0xFF4A3E38),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Crumple",
                        tint = Color(0xFF8C3A3A),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

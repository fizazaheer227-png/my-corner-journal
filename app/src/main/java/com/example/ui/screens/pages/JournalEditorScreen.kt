package com.example.ui.screens.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.JournalEntryEntity
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.NotebookSpineMargin
import com.example.ui.components.PaperClip
import com.example.ui.components.PaperPattern
import com.example.ui.components.ScrapbookWashiTape
import com.example.ui.components.TapedPaperScrap
import com.example.ui.theme.LocalJournalTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalEditorScreen(
    initialEntry: JournalEntryEntity?,
    onSave: (JournalEntryEntity) -> Unit,
    onDelete: ((Long) -> Unit)?,
    onBack: () -> Unit
) {
    val theme = LocalJournalTheme.current

    var title by remember { mutableStateOf(initialEntry?.title ?: "") }
    var content by remember { mutableStateOf(initialEntry?.content ?: "") }
    var mood by remember { mutableStateOf(initialEntry?.mood ?: "Calm") }
    var tags by remember { mutableStateOf(initialEntry?.tags ?: "") }
    var isFavorite by remember { mutableStateOf(initialEntry?.isFavorite ?: false) }

    val fullDateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    val entryDate = initialEntry?.dateMillis?.let { Date(it) } ?: Date()
    val formattedDate = fullDateFormat.format(entryDate)

    Box(modifier = Modifier.fillMaxSize()) {
        JournalPageSurface(
            pageNumber = 3,
            pattern = PaperPattern.RULED_LINES,
            hasLeftSpineMargin = false
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Binder rings on left
                NotebookSpineMargin(
                    modifier = Modifier
                        .fillMaxSize()
                        .width(32.dp)
                )

                // Gold paperclip on top right corner of the page
                PaperClip(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-12).dp, y = 6.dp),
                    rotation = 15f
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 36.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)
                ) {
                    // Top Bar: Back arrow, Centered Date, Done Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1E1D1B)
                            )
                        }

                        // Date pill: "Sat, 06 Sep 2026 ⌄"
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFEDE5D8), RoundedCornerShape(14.dp))
                                .border(0.5.dp, Color(0x33000000), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$formattedDate ⌄",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2C2523)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { isFavorite = !isFavorite }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color(0xFFD9534F) else Color(0xFF6B5848)
                                )
                            }

                            if (initialEntry != null && onDelete != null) {
                                IconButton(onClick = { onDelete(initialEntry.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF6B5848))
                                }
                            }

                            // Done Button
                            Button(
                                onClick = {
                                    val entryToSave = JournalEntryEntity(
                                        id = initialEntry?.id ?: 0,
                                        title = title.ifBlank { "dear diary, ♡" },
                                        content = content,
                                        mood = mood,
                                        template = "classic_diary",
                                        tags = tags,
                                        weatherOrLocation = "gentle desk",
                                        isFavorite = isFavorite,
                                        dateMillis = initialEntry?.dateMillis ?: System.currentTimeMillis()
                                    )
                                    onSave(entryToSave)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24201E)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("save_entry_button")
                            ) {
                                Text(
                                    text = "Done",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFFAF7F0)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Scrollable Entry Body
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Handwritten heading: "dear diary, ♡"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "dear diary, ♡",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1D1B)
                            )

                            // Botanical doodle in margin
                            BotanicalDoodle(
                                modifier = Modifier.size(34.dp, 44.dp),
                                tint = Color(0xFF6B5848),
                                flowerTint = Color(0xFFD49A76)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Title Field (optional)
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = {
                                Text(
                                    "title of this page...",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 18.sp,
                                    color = Color(0xFF8A7E75)
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color(0xFF2C2523),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color(0xFF1E1D1B)
                            ),
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF1E1D1B)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("entry_title_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Main Content Writing Field
                        TextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = {
                                Text(
                                    "today was a day of soft thoughts and little moments...\n\nwrite freely here in dark ink...",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 18.sp,
                                    color = Color(0xFF8A7E75)
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color(0xFF1E1D1B)
                            ),
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Cursive,
                                fontSize = 18.sp,
                                lineHeight = 30.sp,
                                color = Color(0xFF1E1D1B)
                            ),
                            minLines = 9,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("entry_content_field")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Taped kraft reminder note at the bottom: "a reminder: you are doing better than you think ♡"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .align(Alignment.CenterHorizontally)
                                .rotate(-1.5f)
                                .shadow(4.dp, RoundedCornerShape(2.dp))
                                .background(Color(0xFFEFE6D5), RoundedCornerShape(2.dp))
                                .border(0.5.dp, Color(0x33000000), RoundedCornerShape(2.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            PaperClip(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-12).dp),
                                rotation = -20f
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "a reminder:",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 11.sp,
                                    color = Color(0xFF5A4D41)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "you are doing better than you think ♡",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C2523),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(56.dp))
                    }

                    // Bottom Toolbar with tools (Aa, Photo, Sticker, Voice, Doodle)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp))
                            .background(Color(0xFFFCFAF4), RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0x22000000), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.TextFields, contentDescription = "Font", tint = Color(0xFF2C2523))
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Image, contentDescription = "Photo", tint = Color(0xFF2C2523))
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Mood, contentDescription = "Sticker", tint = Color(0xFF2C2523))
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Mic, contentDescription = "Audio", tint = Color(0xFF2C2523))
                            }
                        }
                    }
                }
            }
        }
    }
}

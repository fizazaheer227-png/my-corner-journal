package com.example.ui.screens.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.JournalEntryEntity
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.HeartDoodle
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.NotebookSpineMargin
import com.example.ui.components.PaperPattern
import com.example.ui.components.ScrapbookWashiTape
import com.example.ui.components.TapedPaperScrap
import com.example.ui.theme.LocalJournalTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChapterItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val tapeColor: Color
)

val ScrapbookChapters = listOf(
    ChapterItem("journal", "Everyday Journal", "thoughts, feelings, and everything in between", "📖", Color(0xFFD49A76)),
    ChapterItem("mood", "Mood Tracker", "how are you feeling today?", "🌸", Color(0xFF81C784)),
    ChapterItem("doodle", "Doodle Book", "scribbles, sketches & random ideas", "✏️", Color(0xFFBA68C8)),
    ChapterItem("people", "People I Love", "my favourite humans ♡", "👥", Color(0xFF7986CB)),
    ChapterItem("memories", "Memory Jar & Vault", "little moments, big feelings", "🎞️", Color(0xFFFFA726)),
    ChapterItem("dreams", "Dreams & Horizons", "wishes, collages & letters to future me", "🌌", Color(0xFF5C6BC0)),
    ChapterItem("wishlist", "Wishlist", "things, places, dreams", "📜", Color(0xFF4DB6AC)),
    ChapterItem("visionboard", "Vision Board", "turning dreams into plans", "✨", Color(0xFFFFB74D)),
    ChapterItem("futureme", "Future Me", "letters to my future self", "💌", Color(0xFFE2A68C)),
    ChapterItem("currently", "Currently", "what's on my mind right now", "🎧", Color(0xFF90A4AE)),
    ChapterItem("gratitude", "Gratitude", "always something to be thankful for", "☀️", Color(0xFFFFD54F)),
    ChapterItem("period", "Period Tracker", "for a kinder, more informed you", "🩸", Color(0xFFE57373)),
    ChapterItem("habits", "Habit Garden", "tiny habits, a brighter you", "🌱", Color(0xFFAED581))
)

@Composable
fun JournalEntriesScreen(
    entries: List<JournalEntryEntity>,
    onSelectEntry: (JournalEntryEntity) -> Unit,
    onCreateNewEntry: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    hiddenChapters: Set<String> = emptySet(),
    onNavigateChapter: (String) -> Unit = {}
) {
    val theme = LocalJournalTheme.current
    var viewMode by remember { mutableStateOf("chapters") } // "chapters" or "entries"
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyFavorites by remember { mutableStateOf(false) }

    BackHandler(enabled = viewMode == "entries") {
        viewMode = "chapters"
    }

    val filteredEntries = entries.filter { entry ->
        val matchesQuery = searchQuery.isBlank() ||
                entry.title.contains(searchQuery, ignoreCase = true) ||
                entry.content.contains(searchQuery, ignoreCase = true) ||
                entry.tags.contains(searchQuery, ignoreCase = true)
        val matchesFav = !showOnlyFavorites || entry.isFavorite
        matchesQuery && matchesFav
    }

    Box(modifier = Modifier.fillMaxSize()) {
        JournalPageSurface(
            pageNumber = 2,
            pattern = PaperPattern.DOT_GRID,
            hasLeftSpineMargin = false
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                NotebookSpineMargin(
                    modifier = Modifier
                        .fillMaxSize()
                        .width(32.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 36.dp, end = 16.dp, top = 12.dp)
                ) {
                    // Header: "my pages ♡", "different parts of me, in one place"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (viewMode == "entries") {
                                IconButton(
                                    onClick = { viewMode = "chapters" },
                                    modifier = Modifier.size(36.dp).testTag("entries_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back to Chapters",
                                        tint = Color(0xFF1E1D1B)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (viewMode == "entries") "written entries" else "my pages",
                                        fontFamily = FontFamily.Cursive,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1D1B)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    HeartDoodle(color = Color(0xFF1E1D1B), strokeWidth = 1.6.dp)
                                }
                                Text(
                                    text = if (viewMode == "entries") "words from my heart, one day at a time" else "different parts of me, in one place",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 15.sp,
                                    color = Color(0xFF5A4D41)
                                )
                            }
                        }

                        // Botanical sprig in top right
                        BotanicalDoodle(
                            modifier = Modifier.size(34.dp, 44.dp),
                            tint = Color(0xFF6B5848),
                            flowerTint = Color(0xFFD49A76)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // View Switcher Chips: "chapters index" & "all entries (N)"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .shadow(if (viewMode == "chapters") 2.dp else 0.dp, RoundedCornerShape(12.dp))
                                .background(
                                    if (viewMode == "chapters") Color(0xFF24201E) else Color(0xFFEDE5D8),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewMode = "chapters" }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "scrapbook index 📖",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (viewMode == "chapters") Color(0xFFFBF8F2) else Color(0xFF2C2523)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .shadow(if (viewMode == "entries") 2.dp else 0.dp, RoundedCornerShape(12.dp))
                                .background(
                                    if (viewMode == "entries") Color(0xFF24201E) else Color(0xFFEDE5D8),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewMode = "entries" }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "written entries (${entries.size})",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (viewMode == "entries") Color(0xFFFBF8F2) else Color(0xFF2C2523)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (viewMode == "chapters") {
                        val visibleChapters = remember(hiddenChapters) {
                            ScrapbookChapters.filter { !hiddenChapters.contains(it.id) }
                        }
                        // Screen 5: Chapter index list
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 110.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(visibleChapters) { chapter ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(2.dp, RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                                        .border(0.5.dp, Color(0x33000000), RoundedCornerShape(4.dp))
                                        .clickable {
                                            if (chapter.id == "journal") {
                                                viewMode = "entries"
                                            } else {
                                                onNavigateChapter(chapter.id)
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                        .testTag("chapter_${chapter.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(chapter.tapeColor.copy(alpha = 0.25f), CircleShape)
                                                    .border(1.dp, chapter.tapeColor, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = chapter.iconEmoji, fontSize = 18.sp)
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = chapter.title,
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF1E1D1B)
                                                )
                                                Text(
                                                    text = chapter.description,
                                                    fontFamily = FontFamily.Cursive,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF5A4D41)
                                                )
                                            }
                                        }

                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Open",
                                            tint = Color(0xFF5A4D41),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Entries List Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("search memories...", fontSize = 12.sp) },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF5A4D41))
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2C2523),
                                    unfocusedBorderColor = Color(0x33000000),
                                    focusedTextColor = Color(0xFF1E1D1B)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            )

                            IconButton(onClick = { showOnlyFavorites = !showOnlyFavorites }) {
                                Icon(
                                    imageVector = if (showOnlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorites",
                                    tint = if (showOnlyFavorites) Color(0xFFD9534F) else Color(0xFF5A4D41)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (filteredEntries.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "no pages written yet ♡",
                                        fontFamily = FontFamily.Cursive,
                                        fontSize = 18.sp,
                                        color = Color(0xFF5A4D41)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = onCreateNewEntry,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24201E))
                                    ) {
                                        Text("write first entry ♡", fontFamily = FontFamily.Serif, color = Color(0xFFFBF8F2))
                                    }
                                }
                            }
                        } else {
                            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(top = 4.dp, bottom = 110.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredEntries) { entry ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(3.dp, RoundedCornerShape(4.dp))
                                            .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, Color(0x33000000), RoundedCornerShape(4.dp))
                                            .clickable { onSelectEntry(entry) }
                                            .padding(12.dp)
                                            .testTag("entry_card_${entry.id}")
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = entry.title.ifBlank { "an untitled thought" },
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1E1D1B),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    if (entry.isFavorite) {
                                                        Icon(
                                                            Icons.Default.Favorite,
                                                            contentDescription = "Favorite",
                                                            tint = Color(0xFFD9534F),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                    IconButton(onClick = { onDeleteEntry(entry.id) }) {
                                                        Icon(
                                                            Icons.Default.Delete,
                                                            contentDescription = "Delete",
                                                            tint = Color(0xFF8A7E75),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Text(
                                                text = dateFormat.format(Date(entry.dateMillis)),
                                                fontFamily = FontFamily.Cursive,
                                                fontSize = 11.sp,
                                                color = Color(0xFF5A4D41)
                                            )

                                            // Multimedia badges
                                            val hasPhoto = entry.imageUris.isNotBlank()
                                            val hasVoice = entry.voiceNoteUri.isNotBlank()
                                            val hasVideo = entry.videoUri.isNotBlank()
                                            val hasHandwriting = entry.doodleData.isNotBlank()

                                            if (hasPhoto || hasVoice || hasVideo || hasHandwriting) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (hasPhoto) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(Color(0xFFEDE5D8), RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("📷 photo", fontFamily = FontFamily.Serif, fontSize = 9.sp, color = Color(0xFF2C2523))
                                                        }
                                                    }
                                                    if (hasHandwriting) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(Color(0xFFE8DFD8), RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("✍️ handwritten", fontFamily = FontFamily.Serif, fontSize = 9.sp, color = Color(0xFF2C2523))
                                                        }
                                                    }
                                                    if (hasVoice) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(Color(0xFFF3E9DD), RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("🎙️ voice", fontFamily = FontFamily.Serif, fontSize = 9.sp, color = Color(0xFF2C2523))
                                                        }
                                                    }
                                                    if (hasVideo) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(Color(0xFFE2DCD5), RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("🎬 video", fontFamily = FontFamily.Serif, fontSize = 9.sp, color = Color(0xFF2C2523))
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = entry.content,
                                                fontFamily = FontFamily.Cursive,
                                                fontSize = 13.sp,
                                                color = Color(0xFF2C2523),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating Action Button
                FloatingActionButton(
                    onClick = onCreateNewEntry,
                    containerColor = Color(0xFF24201E),
                    contentColor = Color(0xFFFBF8F2),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 86.dp, end = 16.dp)
                        .testTag("new_entry_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Entry")
                }
            }
        }
    }
}

package com.example.ui.screens.memories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MemoryEntity
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.PolaroidCard
import com.example.ui.components.WashiTape
import com.example.ui.theme.LocalJournalTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemoryVaultScreen(
    memories: List<MemoryEntity>,
    onDeleteMemory: (Long) -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalJournalTheme.current
    var selectedMoodFilter by remember { mutableStateOf<String?>(null) }
    var showOnlyFavorites by remember { mutableStateOf(false) }

    val filtered = memories.filter { m ->
        val matchesMood = selectedMoodFilter == null || m.mood.equals(selectedMoodFilter, ignoreCase = true)
        val matchesFav = !showOnlyFavorites || m.isFavorite
        matchesMood && matchesFav
    }

    JournalPageSurface(
        pageNumber = 10,
        pattern = PaperPattern.RULED_LINES,
        hasLeftSpineMargin = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = theme.textColor)
                    }
                    Column {
                        Text(
                            text = "memory vault",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = theme.textColor
                        )
                        Text(
                            text = "Polaroids & filmstrip archives",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 14.sp,
                            color = theme.secondaryTextColor
                        )
                    }
                }

                IconButton(onClick = { showOnlyFavorites = !showOnlyFavorites }) {
                    Icon(
                        imageVector = if (showOnlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorites",
                        tint = if (showOnlyFavorites) Color(0xFFD9534F) else theme.secondaryTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // "On this day..." highlight banner
            val firstMemory = memories.firstOrNull()
            if (firstMemory != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFF7EA), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0x33E29578), RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "remember this moment?",
                                fontFamily = FontFamily.Cursive,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF5C4033)
                            )
                            Text(
                                text = "“${firstMemory.title}” - ${firstMemory.content.take(45)}...",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                color = Color(0xFF382920)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(null to "All", "Happy" to "Happy ☀️", "Calm" to "Calm 🌿", "Nostalgic" to "Nostalgic ☕").forEach { (moodKey, label) ->
                    val isSel = selectedMoodFilter == moodKey
                    Box(
                        modifier = Modifier
                            .background(if (isSel) theme.coverAccentColor else Color(0xFFEDE5D8), RoundedCornerShape(12.dp))
                            .clickable { selectedMoodFilter = moodKey }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = label, fontFamily = FontFamily.Serif, fontSize = 11.sp, color = if (isSel) Color.White else Color(0xFF382920))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filmstrip / Polaroid list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filtered, key = { it.id }) { memory ->
                    val rot = if (memory.id % 2L == 0L) -1.8f else 1.8f

                    PolaroidCard(
                        modifier = Modifier.fillMaxWidth(),
                        rotation = rot,
                        caption = memory.title,
                        dateText = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(memory.dateMillis)),
                        hasPaperClip = memory.id % 2L == 0L,
                        hasWashiTape = memory.id % 2L != 0L
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF3ECE0))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = memory.content,
                                fontFamily = FontFamily.Cursive,
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF2C2523)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { onDeleteMemory(memory.id) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = theme.secondaryTextColor.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

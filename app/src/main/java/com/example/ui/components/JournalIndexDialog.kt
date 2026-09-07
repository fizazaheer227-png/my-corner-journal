package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalJournalTheme

data class IndexSectionEntry(
    val title: String,
    val pageNumber: String,
    val emoji: String,
    val action: () -> Unit
)

@Composable
fun JournalIndexDialog(
    onDismiss: () -> Unit,
    onNavigate: (sectionKey: String) -> Unit
) {
    val theme = LocalJournalTheme.current

    val indexItems = listOf(
        IndexSectionEntry("Today's Page & Mood", "p. 1", "☀️", { onNavigate("today"); onDismiss() }),
        IndexSectionEntry("Daily Written Pages", "p. 2", "📖", { onNavigate("pages"); onDismiss() }),
        IndexSectionEntry("Mood Garden Spread", "p. 4", "🌸", { onNavigate("mood"); onDismiss() }),
        IndexSectionEntry("Doodle Book & Canvas", "p. 5", "🎨", { onNavigate("doodle"); onDismiss() }),
        IndexSectionEntry("Brain Dump Sticky Notes", "p. 6", "📝", { onNavigate("braindump"); onDismiss() }),
        IndexSectionEntry("People & Polaroids Scrapbook", "p. 7", "👥", { onNavigate("people"); onDismiss() }),
        IndexSectionEntry("The Memory Jar", "p. 9", "🫙", { onNavigate("memoryjar"); onDismiss() }),
        IndexSectionEntry("Photo Memory Vault", "p. 10", "🎞️", { onNavigate("memoryvault"); onDismiss() }),
        IndexSectionEntry("Future Me Time Capsules", "p. 11", "💌", { onNavigate("futureme"); onDismiss() }),
        IndexSectionEntry("Pinterest-Style Wishlist", "p. 12", "📜", { onNavigate("wishlist"); onDismiss() }),
        IndexSectionEntry("Vision Board Collage", "p. 13", "✨", { onNavigate("visionboard"); onDismiss() }),
        IndexSectionEntry("Habit Garden", "p. 14", "🌱", { onNavigate("habits"); onDismiss() }),
        IndexSectionEntry("Currently In This Era", "p. 15", "🎧", { onNavigate("currently"); onDismiss() }),
        IndexSectionEntry("Three Tiny Joys (Gratitude)", "p. 16", "☕", { onNavigate("gratitude"); onDismiss() }),
        IndexSectionEntry("Cycle & Botanical Notes", "p. 17", "🌺", { onNavigate("period"); onDismiss() }),
        IndexSectionEntry("Stationery Letters", "p. 18", "✉️", { onNavigate("letters"); onDismiss() }),
        IndexSectionEntry("Pieces of Me (About Me)", "p. 19", "🌿", { onNavigate("aboutme"); onDismiss() }),
        IndexSectionEntry("Book Cover & Settings", "p. 99", "⚙️", { onNavigate("settings"); onDismiss() })
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .height(520.dp)
                .shadow(16.dp, RoundedCornerShape(10.dp))
                .background(Color(0xFFFCFAF4), RoundedCornerShape(10.dp))
                .border(1.2.dp, Color(0xFF4A3E38), RoundedCornerShape(10.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Old book table of contents style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TABLE OF CONTENTS",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = 2.sp,
                            color = Color(0xFF2C2523)
                        )
                        Text(
                            text = "quick bookmark index",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 13.sp,
                            color = Color(0xFF756A63)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = theme.secondaryTextColor)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dotted line index rows
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(indexItems) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { item.action() }
                                .padding(vertical = 5.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = item.emoji, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.title,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 13.sp,
                                    color = Color(0xFF2C2523)
                                )
                            }
                            Text(
                                text = "....... ${item.pageNumber}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color(0xFF8B7D72)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x33000000))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "turn to any page at your heart’s desire ♡",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 13.sp,
                    color = Color(0xFF6B5A4D),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

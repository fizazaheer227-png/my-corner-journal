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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.PersonEntity
import com.example.data.local.entity.StickyNoteEntity
import com.example.data.local.entity.WishlistItemEntity
import com.example.ui.theme.LocalJournalTheme

data class SearchResultItem(
    val category: String,
    val title: String,
    val snippet: String,
    val onClick: () -> Unit
)

@Composable
fun SearchRibbonDialog(
    entries: List<JournalEntryEntity>,
    memories: List<MemoryEntity>,
    people: List<PersonEntity>,
    notes: List<StickyNoteEntity>,
    wishlist: List<WishlistItemEntity>,
    onSelectEntry: (JournalEntryEntity) -> Unit,
    onSelectPerson: (Long) -> Unit,
    onSelectMemory: ((MemoryEntity) -> Unit)? = null,
    onSelectNote: ((StickyNoteEntity) -> Unit)? = null,
    onSelectWishlist: ((WishlistItemEntity) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val theme = LocalJournalTheme.current
    var query by remember { mutableStateOf("") }

    val results = remember(query, entries, memories, people, notes, wishlist) {
        if (query.isBlank()) return@remember emptyList<SearchResultItem>()
        val list = mutableListOf<SearchResultItem>()

        entries.filter { it.title.contains(query, true) || it.content.contains(query, true) || it.tags.contains(query, true) }
            .take(5).forEach {
                list.add(SearchResultItem("📖 Page", it.title, it.content.take(60), { onSelectEntry(it); onDismiss() }))
            }

        memories.filter { it.title.contains(query, true) || it.content.contains(query, true) }
            .take(3).forEach {
                list.add(SearchResultItem("🎞️ Memory", it.title, it.content.take(60), {
                    onSelectMemory?.invoke(it)
                    onDismiss()
                }))
            }

        people.filter { it.name.contains(query, true) || it.nickname.contains(query, true) || it.insideJokes.contains(query, true) }
            .take(3).forEach {
                list.add(SearchResultItem("👥 Person", it.name, it.nickname, { onSelectPerson(it.id); onDismiss() }))
            }

        notes.filter { it.text.contains(query, true) }
            .take(3).forEach {
                list.add(SearchResultItem("📝 Sticky Note", "Brain Dump", it.text.take(50), {
                    onSelectNote?.invoke(it)
                    onDismiss()
                }))
            }

        wishlist.filter { it.title.contains(query, true) || it.note.contains(query, true) }
            .take(3).forEach {
                list.add(SearchResultItem("✨ Wish", it.title, it.note, {
                    onSelectWishlist?.invoke(it)
                    onDismiss()
                }))
            }

        list
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .height(480.dp)
                .shadow(16.dp, RoundedCornerShape(12.dp))
                .background(Color(0xFFFCFAF4), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0x33000000), RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with Bookmark Ribbon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        JournalBookmarkRibbon(color = theme.ribbonColor, length = 28.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "find a little something...",
                            fontFamily = FontFamily.Cursive,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = theme.textColor
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = theme.secondaryTextColor)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("search pages, people, thoughts...", fontFamily = FontFamily.Cursive) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = theme.secondaryTextColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.coverColor,
                        unfocusedBorderColor = theme.secondaryTextColor.copy(alpha = 0.3f),
                        focusedTextColor = theme.textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (query.isNotBlank() && results.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "nothing found tucked under this word 🍂",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 15.sp,
                            color = theme.secondaryTextColor
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(results) { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFF9EE), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                                    .clickable { item.onClick() }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.title,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = theme.textColor
                                        )
                                        // Category highlighted badge
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFFFF1C5), RoundedCornerShape(2.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = item.category,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Serif,
                                                color = Color(0xFF5C4033)
                                            )
                                        }
                                    }
                                    if (item.snippet.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${item.snippet}...",
                                            fontFamily = FontFamily.Default,
                                            fontSize = 12.sp,
                                            color = theme.secondaryTextColor
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
}

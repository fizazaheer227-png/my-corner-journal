package com.example.ui.screens.memories

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.media.MediaStorageHelper
import java.io.File
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.MemoryEntity
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.HeartDoodle
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.NotebookSpineMargin
import com.example.ui.components.PaperClip
import com.example.ui.components.PaperPattern
import com.example.ui.components.ScrapbookWashiTape
import com.example.ui.components.TapedPaperScrap
import com.example.ui.theme.LocalJournalTheme

data class DefaultScrapbookMemory(
    val title: String,
    val caption: String,
    val category: String,
    val gradient: List<Color>,
    val emoji: String,
    val rotation: Float,
    val tapeColor: Color
)

val DefaultScrapbookMoments = listOf(
    DefaultScrapbookMemory(
        "sunset walk",
        "sunsets & soft thoughts ♡",
        "Happy",
        listOf(Color(0xFFE89874), Color(0xFF8E6088), Color(0xFF4A4E69)),
        "🌅",
        -2.5f,
        Color(0xFFE8A598)
    ),
    DefaultScrapbookMemory(
        "picnic day",
        "good friends brighter days ♡",
        "Happy",
        listOf(Color(0xFF8FB9A8), Color(0xFF769FCD), Color(0xFFF7F4E9)),
        "🧺",
        3f,
        Color(0xFF9EABA2)
    ),
    DefaultScrapbookMemory(
        "coffee morning",
        "coffee & conversations ♡",
        "Milestones",
        listOf(Color(0xFFD4A373), Color(0xFFCCD5AE), Color(0xFFE9EDC9)),
        "☕",
        -1.5f,
        Color(0xFFD49A76)
    ),
    DefaultScrapbookMemory(
        "rainy tea",
        "a day I needed this ♡",
        "Travel",
        listOf(Color(0xFF6B7280), Color(0xFF9CA3AF), Color(0xFFE5E7EB)),
        "🌧️",
        2f,
        Color(0xFFDDA15E)
    ),
    DefaultScrapbookMemory(
        "home feelings",
        "surrounded by people who feel like home ♡",
        "Happy",
        listOf(Color(0xFFE07A5F), Color(0xFF3D405B), Color(0xFF81B29A)),
        "🏡",
        -3f,
        Color(0xFFBA68C8)
    )
)

@Composable
fun MemoryJarScreen(
    memories: List<MemoryEntity>,
    unfoldedMemory: MemoryEntity?,
    onPickMemory: () -> Unit,
    onPutBack: () -> Unit,
    onAddMemory: (String, String, String, String) -> Unit,
    onOpenVault: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val theme = LocalJournalTheme.current
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Happy", "Funny", "Milestones", "Travel")

    Box(modifier = Modifier.fillMaxSize()) {
        JournalPageSurface(
            pageNumber = 7,
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
                        .padding(start = 36.dp, end = 16.dp, top = 12.dp, bottom = 100.dp)
                ) {
                    // Header (Screen 7 from reference image):
                    // Hand-drawn Memory Jar icon + "Memory Jar ♡" + "little moments, big feelings"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (onBack != null) {
                                IconButton(onClick = onBack, modifier = Modifier.testTag("memories_back_button")) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color(0xFF1E1D1B)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            // Jar doodle badge
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color(0xFFEDE5D8), CircleShape)
                                    .border(1.dp, Color(0x33000000), CircleShape)
                                    .clickable { onPickMemory() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🫙", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Memory Jar",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = Color(0xFF1E1D1B)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    HeartDoodle(color = Color(0xFF1E1D1B), strokeWidth = 1.6.dp)
                                }
                                Text(
                                    text = "little moments, big feelings",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 15.sp,
                                    color = Color(0xFF5A4D41)
                                )
                            }
                        }

                        // Dried lavender sprig
                        BotanicalDoodle(
                            modifier = Modifier.size(34.dp, 44.dp),
                            tint = Color(0xFF6B5848),
                            flowerTint = Color(0xFF8E7196)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Filter Pills: All, Happy, Funny, Milestones, Travel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) Color(0xFF24201E) else Color(0xFFEDE5D8),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFFFBF8F2) else Color(0xFF2C2523)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Grid of Polaroid Photos with washi tape at various angles & handwritten captions
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // User saved memories
                        items(memories) { mem ->
                            PolaroidMemoryCard(
                                title = mem.title,
                                caption = mem.content.ifBlank { mem.title },
                                photoUri = mem.imageUri,
                                rotation = if (mem.id % 2 == 0L) 2f else -2f,
                                tapeColor = Color(0xFFE2A68C)
                            )
                        }

                        // Default aesthetic Polaroid scrapbook moments from reference
                        val filteredDefaults = DefaultScrapbookMoments.filter {
                            selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
                        }
                        items(filteredDefaults) { moment ->
                            PolaroidScrapbookDefaultCard(moment = moment)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom Pill Button: "+ add a memory"
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24201E)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("add_memory_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFFFBF8F2))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "add a memory ♡",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFFFBF8F2)
                        )
                    }
                }

                // Add Memory Dialog
                if (showAddDialog) {
                    var title by remember { mutableStateOf("") }
                    var caption by remember { mutableStateOf("") }
                    var mood by remember { mutableStateOf("Happy") }
                    var photoUri by remember { mutableStateOf("") }

                    val photoPicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        if (uri != null) {
                            val saved = MediaStorageHelper.copyUriToInternalStorage(
                                context = context,
                                uri = uri,
                                subDir = "memory_photos",
                                prefix = "memory",
                                extension = "jpg"
                            )
                            if (saved != null) {
                                photoUri = saved
                            }
                        }
                    }

                    Dialog(onDismissRequest = { showAddDialog = false }) {
                        Box(
                            modifier = Modifier
                                .width(320.dp)
                                .shadow(16.dp, RoundedCornerShape(12.dp))
                                .background(Color(0xFFFAF7F0), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0x33000000), RoundedCornerShape(12.dp))
                                .padding(20.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "tuck a memory into the jar 🫙",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1D1B)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text("What happened?") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = caption,
                                    onValueChange = { caption = it },
                                    label = { Text("Handwritten caption on Polaroid") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Photo attach button
                                Button(
                                    onClick = {
                                        photoPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (photoUri.isNotBlank()) Color(0xFF8FB9A8) else Color(0xFFEDE5D8),
                                        contentColor = Color(0xFF2C2523)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (photoUri.isNotBlank()) "✓ Photo Attached 📷" else "📷 Add Real Photo",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        if (title.isNotBlank()) {
                                            onAddMemory(title, caption, mood, photoUri)
                                            showAddDialog = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24201E)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("save to scrapbook ♡", color = Color(0xFFFBF8F2), fontFamily = FontFamily.Serif)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PolaroidMemoryCard(
    title: String,
    caption: String,
    photoUri: String = "",
    rotation: Float,
    tapeColor: Color
) {
    Box(
        modifier = Modifier
            .rotate(rotation)
            .shadow(6.dp, RoundedCornerShape(2.dp))
            .background(Color(0xFFFFFFFF), RoundedCornerShape(2.dp))
            .border(0.5.dp, Color(0x22000000), RoundedCornerShape(2.dp))
            .padding(8.dp)
    ) {
        ScrapbookWashiTape(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp),
            color = tapeColor,
            width = 46.dp,
            height = 12.dp,
            rotation = -rotation
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFE89874), Color(0xFF8E6088), Color(0xFF4A4E69))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri.isNotBlank()) {
                    val photoModel = remember(photoUri) {
                        if (photoUri.startsWith("content://")) photoUri else File(photoUri)
                    }
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoModel)
                            .crossfade(true)
                            .build(),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("📷 ✨", fontSize = 22.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = caption,
                fontFamily = FontFamily.Cursive,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2523),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PolaroidScrapbookDefaultCard(
    moment: DefaultScrapbookMemory
) {
    Box(
        modifier = Modifier
            .rotate(moment.rotation)
            .shadow(6.dp, RoundedCornerShape(2.dp))
            .background(Color(0xFFFFFFFF), RoundedCornerShape(2.dp))
            .border(0.5.dp, Color(0x22000000), RoundedCornerShape(2.dp))
            .padding(8.dp)
    ) {
        ScrapbookWashiTape(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp),
            color = moment.tapeColor,
            width = 48.dp,
            height = 12.dp,
            rotation = -moment.rotation
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(
                        brush = Brush.verticalGradient(moment.gradient)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = moment.emoji, fontSize = 26.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = moment.caption,
                fontFamily = FontFamily.Cursive,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2523),
                textAlign = TextAlign.Center
            )
        }
    }
}

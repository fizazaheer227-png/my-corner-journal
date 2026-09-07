package com.example.ui.screens.dreams

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.components.HeartDoodle
import com.example.ui.components.NewspaperSnippet
import com.example.ui.components.PaperClip
import com.example.ui.components.TapedPaperScrap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.FutureCapsuleEntity
import com.example.data.local.entity.WishlistItemEntity
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.PolaroidCard
import com.example.ui.components.WashiTape
import com.example.ui.theme.LocalJournalTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class DreamsSubTab(val label: String, val emoji: String) {
    WISHLIST("Wishlist", "📜"),
    VISION_BOARD("Vision Board", "✨"),
    FUTURE_ME("Future Me", "💌")
}

@Composable
fun DreamsDashboardScreen(
    wishlist: List<WishlistItemEntity>,
    capsules: List<FutureCapsuleEntity>,
    onSaveWishlistItem: (WishlistItemEntity) -> Unit,
    onToggleWishlistAcquired: (WishlistItemEntity) -> Unit,
    onDeleteWishlistItem: (Long) -> Unit,
    onSaveCapsule: (FutureCapsuleEntity) -> Unit,
    initialSubTab: DreamsSubTab = DreamsSubTab.WISHLIST,
    isDedicatedPage: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    val theme = LocalJournalTheme.current
    var currentSubTab by remember(initialSubTab) { mutableStateOf(initialSubTab) }

    JournalPageSurface(
        pageNumber = 11,
        pattern = PaperPattern.DOT_GRID,
        hasLeftSpineMargin = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 16.dp, top = 14.dp, bottom = 0.dp)
        ) {
            // Header & Sub-tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("dreams_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = theme.textColor
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Column {
                        val headerTitle = when {
                            isDedicatedPage && currentSubTab == DreamsSubTab.WISHLIST -> "my wishlist 📜"
                            isDedicatedPage && currentSubTab == DreamsSubTab.VISION_BOARD -> "vision board ✨"
                            isDedicatedPage && currentSubTab == DreamsSubTab.FUTURE_ME -> "letters to future me 💌"
                            else -> "dreams & horizons"
                        }
                        val headerSubtitle = when {
                            isDedicatedPage && currentSubTab == DreamsSubTab.WISHLIST -> "things, places, dreams & keepsakes"
                            isDedicatedPage && currentSubTab == DreamsSubTab.VISION_BOARD -> "turning dreams into plans & aesthetic collages"
                            isDedicatedPage && currentSubTab == DreamsSubTab.FUTURE_ME -> "time capsules & heartfelt notes to tomorrow"
                            else -> "wishes, collages & letters to future me"
                        }
                        Text(
                            text = headerTitle,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = theme.textColor
                        )
                        Text(
                            text = headerSubtitle,
                            fontFamily = FontFamily.Cursive,
                            fontSize = 15.sp,
                            color = theme.secondaryTextColor
                        )
                    }
                }
            }

            if (!isDedicatedPage) {
                Spacer(modifier = Modifier.height(10.dp))

                // Sub tabs strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DreamsSubTab.values().forEach { tab ->
                        val isSelected = currentSubTab == tab
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) theme.coverColor else Color(0xFFEDE5D8),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { currentSubTab = tab }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("dreams_tab_${tab.name.lowercase()}")
                        ) {
                            Text(
                                text = "${tab.emoji} ${tab.label}",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else Color(0xFF2C2523)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Content
            AnimatedContent(
                targetState = currentSubTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "dreamsTabAnim",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { tab ->
                when (tab) {
                    DreamsSubTab.WISHLIST -> WishlistSubView(
                        wishlist = wishlist,
                        onAdd = onSaveWishlistItem,
                        onToggle = onToggleWishlistAcquired,
                        onDelete = onDeleteWishlistItem
                    )
                    DreamsSubTab.VISION_BOARD -> VisionBoardSubView()
                    DreamsSubTab.FUTURE_ME -> FutureMeSubView(
                        capsules = capsules,
                        onSaveCapsule = onSaveCapsule
                    )
                }
            }
        }
    }
}

// 1. WISHLIST VIEW
@Composable
fun WishlistSubView(
    wishlist: List<WishlistItemEntity>,
    onAdd: (WishlistItemEntity) -> Unit,
    onToggle: (WishlistItemEntity) -> Unit,
    onDelete: (Long) -> Unit
) {
    val theme = LocalJournalTheme.current
    var selectedBoard by remember { mutableStateOf("all") }
    var showAddDialog by remember { mutableStateOf(false) }

    val defaultWishes = remember {
        listOf(
            WishlistItemEntity(id = -101, title = "Vintage 35mm film camera 📷", board = "things I want", isAcquired = false),
            WishlistItemEntity(id = -102, title = "Weekend cabin in the pine woods 🌲", board = "places", isAcquired = false),
            WishlistItemEntity(id = -103, title = "Learn watercolor botanicals 🎨", board = "experiences", isAcquired = true),
            WishlistItemEntity(id = -104, title = "Linen bookbinding set & wax stamp ✉️", board = "things I want", isAcquired = false),
            WishlistItemEntity(id = -105, title = "Before the Coffee Gets Cold (Book) ☕", board = "books", isAcquired = true),
            WishlistItemEntity(id = -106, title = "Cherry blossom picnic in Kyoto 🌸", board = "places", isAcquired = false),
            WishlistItemEntity(id = -107, title = "Woven wicker picnic basket 🧺", board = "things I want", isAcquired = false),
            WishlistItemEntity(id = -108, title = "Pottery wheel workshop for two 🏺", board = "experiences", isAcquired = false),
            WishlistItemEntity(id = -109, title = "Sunlight reading nook with ivy plants 🌿", board = "dream room", isAcquired = false),
            WishlistItemEntity(id = -110, title = "Road trip along the coastal highway 🚗", board = "bucket list", isAcquired = false)
        )
    }

    val displaySource = if (wishlist.isEmpty()) defaultWishes else wishlist

    val boards = listOf(
        "all" to "All Wishes",
        "things I want" to "Things I Want",
        "books" to "Books To Read",
        "places" to "Places To Visit",
        "dream room" to "Dream Room",
        "bucket list" to "Bucket List",
        "experiences" to "Experiences"
    )

    val filteredList = displaySource.filter { item ->
        selectedBoard == "all" || item.board.equals(selectedBoard, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Board selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            boards.forEach { (key, label) ->
                val isSel = selectedBoard == key
                Box(
                    modifier = Modifier
                        .background(if (isSel) theme.coverAccentColor else Color(0x18000000), RoundedCornerShape(8.dp))
                        .clickable { selectedBoard = key }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = label, fontSize = 11.sp, fontFamily = FontFamily.Serif, color = if (isSel) Color.White else theme.textColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Items list with generous bottom padding for bottom nav bar
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList, key = { it.id }) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(4.dp))
                        .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                        .border(0.5.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Checkbox(
                                checked = item.isAcquired,
                                onCheckedChange = { onToggle(item) }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = item.title,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp,
                                    textDecoration = if (item.isAcquired) TextDecoration.LineThrough else null,
                                    color = if (item.isAcquired) theme.secondaryTextColor else theme.textColor
                                )
                                if (item.note.isNotBlank()) {
                                    Text(
                                        text = item.note,
                                        fontFamily = FontFamily.Cursive,
                                        fontSize = 12.sp,
                                        color = theme.secondaryTextColor
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { onDelete(item.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = theme.secondaryTextColor.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showAddDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally).testTag("add_wishlist_item_button")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add wish", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("add to wishlist 📜", fontFamily = FontFamily.Serif)
        }

        Spacer(modifier = Modifier.height(96.dp))
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        var board by remember { mutableStateOf(if (selectedBoard == "all") "things I want" else selectedBoard) }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .shadow(12.dp, RoundedCornerShape(10.dp))
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(10.dp))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "add a wish 📜", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = theme.textColor)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("What do you dream of?") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Details or link") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(WishlistItemEntity(board = board, title = title, note = note))
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor)
                    ) {
                        Text("pin wish", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }
}

// 2. VISION BOARD VIEW (Screen 8 in Reference: Rich Scrapbook Collage)
@Composable
fun VisionBoardSubView() {
    val theme = LocalJournalTheme.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Newspaper Snippet headline
        NewspaperSnippet(
            text = "DISCIPLINE CREATES FREEDOM",
            rotation = -2f,
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Top Collage Row: Airplane Polaroid + Taped Kraft "same girl... bigger dreams ♡"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Airplane / Ocean Polaroid
            PolaroidCard(
                modifier = Modifier.weight(1f),
                rotation = -4f,
                caption = "travel more ♡",
                hasWashiTape = true
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(0xFF56CCF2), Color(0xFF2F80ED), Color(0xFF1B4965))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✈️ 🌊 🏝️", fontSize = 24.sp)
                }
            }

            // Taped Kraft "same girl... bigger dreams ♡" + "that girl ♡"
            Column(
                modifier = Modifier.weight(1.1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TapedPaperScrap(
                    text = "same girl...\nbigger dreams ♡",
                    rotation = 3f,
                    tapeColor = Color(0xFFE2A68C),
                    bgColor = Color(0xFFF3EDE2),
                    fontSize = 13f
                )

                // Cutout badge: "that girl ♡"
                Box(
                    modifier = Modifier
                        .rotate(-2f)
                        .shadow(4.dp, RoundedCornerShape(2.dp))
                        .background(Color(0xFF24201E), RoundedCornerShape(2.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "THAT GIRL ♡",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        color = Color(0xFFFAF7F0)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Middle Row: Cafe Coffee Polaroid + Gold Paperclip Note
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Taped sticky note: "manifest it, work for it ♡"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .rotate(2f)
                    .shadow(4.dp, RoundedCornerShape(2.dp))
                    .background(Color(0xFFFFF9C4), RoundedCornerShape(2.dp))
                    .border(0.5.dp, Color(0x33000000), RoundedCornerShape(2.dp))
                    .padding(10.dp)
            ) {
                PaperClip(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-4).dp, y = (-12).dp),
                    rotation = -15f
                )
                Column {
                    Text(
                        text = "AFFIRMATION",
                        fontFamily = FontFamily.Serif,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF5A4D41)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "manifest it.\nwork for it ♡",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1D1B)
                    )
                }
            }

            // Cozy Cafe Polaroid
            PolaroidCard(
                modifier = Modifier.weight(1.1f),
                rotation = 3f,
                caption = "sunlit mornings & matcha",
                hasPaperClip = false
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(0xFFD4A373), Color(0xFFCCD5AE), Color(0xFFE9EDC9))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("☕ 🥐 📖", fontSize = 24.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Banner: "good health • happy mind • bright future ♡"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .rotate(-1f)
                .shadow(4.dp, RoundedCornerShape(4.dp))
                .background(Color(0xFFEFE6D5), RoundedCornerShape(4.dp))
                .border(1.dp, Color(0x33000000), RoundedCornerShape(4.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeartDoodle(color = Color(0xFF2C2523), strokeWidth = 1.2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "good health • happy mind • bright future",
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E1D1B),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                HeartDoodle(color = Color(0xFF2C2523), strokeWidth = 1.2.dp)
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

// 3. FUTURE ME TIME CAPSULES
@Composable
fun FutureMeSubView(
    capsules: List<FutureCapsuleEntity>,
    onSaveCapsule: (FutureCapsuleEntity) -> Unit
) {
    val theme = LocalJournalTheme.current
    var showWriteDialog by remember { mutableStateOf(false) }
    var selectedOpenedCapsule by remember { mutableStateOf<FutureCapsuleEntity?>(null) }

    val defaultCapsules = remember {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L
        listOf(
            FutureCapsuleEntity(id = -201, title = "To me on my next birthday 🎂", content = "I hope you are gentler with your thoughts and prouder of how far you've walked.", createdAt = now - (30 * oneDay), unlockDateMillis = now - oneDay, isOpened = true),
            FutureCapsuleEntity(id = -202, title = "A message for a rainy evening 🌧️", content = "Remember that difficult seasons always yield to softer mornings. Make some tea.", createdAt = now - (10 * oneDay), unlockDateMillis = now - (2 * oneDay), isOpened = false),
            FutureCapsuleEntity(id = -203, title = "Open on New Year's Eve ✨", content = "What dreams survived the winter? What lessons did you keep in your pockets?", createdAt = now - (5 * oneDay), unlockDateMillis = now + (180 * oneDay), isOpened = false),
            FutureCapsuleEntity(id = -204, title = "When I achieve my biggest dream 🏆", content = "Take a deep breath and look around. You built this from nothing but courage.", createdAt = now, unlockDateMillis = now + (365 * oneDay), isOpened = false),
            FutureCapsuleEntity(id = -205, title = "Open when feeling overwhelmed 🌿", content = "Put down the screen, step outside, and watch the clouds move across the sky.", createdAt = now - oneDay, unlockDateMillis = now - (1000L), isOpened = false)
        )
    }

    val displayCapsules = if (capsules.isEmpty()) defaultCapsules else capsules

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "some things can wait. 💌",
                fontFamily = FontFamily.Cursive,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textColor
            )
            Button(
                onClick = { showWriteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Mail, contentDescription = "New Letter", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("seal a letter", fontSize = 11.sp, fontFamily = FontFamily.Serif)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(displayCapsules, key = { it.id }) { capsule ->
                val isUnlocked = System.currentTimeMillis() >= capsule.unlockDateMillis
                val unlockDateStr = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(capsule.unlockDateMillis))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(6.dp))
                        .background(if (isUnlocked) Color(0xFFFCFAF4) else Color(0xFFF3ECE0), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0x33000000), RoundedCornerShape(6.dp))
                        .clickable {
                            if (isUnlocked) {
                                selectedOpenedCapsule = capsule
                            }
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = "Status",
                                    tint = if (isUnlocked) Color(0xFF2E7D32) else Color(0xFFB23A2B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = capsule.title,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = theme.textColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isUnlocked) "unlocked! tap to open letter ♡" else "sealed with wax until $unlockDateStr",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 13.sp,
                                color = theme.secondaryTextColor
                            )
                        }

                        // Wax seal icon
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .shadow(2.dp, RoundedCornerShape(16.dp))
                                .background(if (isUnlocked) Color(0xFF81C784) else Color(0xFFB23A2B), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isUnlocked) "📜" else "✉️", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    if (showWriteDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var monthsAhead by remember { mutableStateOf(6) }

        Dialog(onDismissRequest = { showWriteDialog = false }) {
            Box(
                modifier = Modifier
                    .width(310.dp)
                    .shadow(12.dp, RoundedCornerShape(10.dp))
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(10.dp))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "seal letter to future me 💌", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = theme.textColor)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Letter headline") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Dear future me...") }, minLines = 4, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Unlock after $monthsAhead months", fontSize = 12.sp, fontFamily = FontFamily.Serif, color = theme.secondaryTextColor)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                val unlockMillis = System.currentTimeMillis() + (monthsAhead * 30L * 24 * 60 * 60 * 1000)
                                onSaveCapsule(
                                    FutureCapsuleEntity(
                                        title = title.ifBlank { "letter to future me" },
                                        content = content,
                                        unlockDateMillis = unlockMillis
                                    )
                                )
                                showWriteDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor)
                    ) {
                        Text("seal with wax 🕯️", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }

    // View Unlocked Letter Dialog
    if (selectedOpenedCapsule != null) {
        val cap = selectedOpenedCapsule!!
        Dialog(onDismissRequest = { selectedOpenedCapsule = null }) {
            Box(
                modifier = Modifier
                    .width(320.dp)
                    .shadow(16.dp, RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFFDF7), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0x33000000), RoundedCornerShape(10.dp))
                    .padding(22.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    WashiTape(width = 80.dp, rotation = 3f)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = cap.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = theme.textColor)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = cap.content, fontFamily = FontFamily.Cursive, fontSize = 16.sp, lineHeight = 24.sp, color = Color(0xFF2C2523))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { selectedOpenedCapsule = null }, colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor)) {
                        Text("keep letter ♡", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }
}

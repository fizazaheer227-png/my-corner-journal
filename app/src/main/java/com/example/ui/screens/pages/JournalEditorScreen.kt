package com.example.ui.screens.pages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.JournalEntryEntity
import com.example.media.MediaStorageHelper
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.HandwritingJournalCanvas
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.NotebookSpineMargin
import com.example.ui.components.PaperClip
import com.example.ui.components.PaperPattern
import com.example.ui.components.ScrapbookPhotoCard
import com.example.ui.components.ScrapbookVideoCard
import com.example.ui.components.ScrapbookVoicePlayerCard
import com.example.ui.components.ScrapbookVoiceRecorderModal
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
    val context = LocalContext.current

    var title by remember { mutableStateOf(initialEntry?.title ?: "") }
    var content by remember { mutableStateOf(initialEntry?.content ?: "") }
    var mood by remember { mutableStateOf(initialEntry?.mood ?: "Calm") }
    var tags by remember { mutableStateOf(initialEntry?.tags ?: "") }
    var isFavorite by remember { mutableStateOf(initialEntry?.isFavorite ?: false) }

    // Multimedia attachments
    var photosList by remember {
        mutableStateOf(
            initialEntry?.imageUris?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }
    var voiceNotePath by remember { mutableStateOf(initialEntry?.voiceNoteUri ?: "") }
    var videoClipPath by remember { mutableStateOf(initialEntry?.videoUri ?: "") }
    var handwritingStrokesJson by remember { mutableStateOf(initialEntry?.doodleData ?: "") }
    var showHandwritingCanvas by remember {
        mutableStateOf(initialEntry?.doodleData?.isNotBlank() == true)
    }

    // Modal dialogs
    var showVoiceRecorderModal by remember { mutableStateOf(false) }
    var showStickerDialog by remember { mutableStateOf(false) }

    // Activity Result Launchers for Photo & Video
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = MediaStorageHelper.copyUriToInternalStorage(
                context = context,
                uri = uri,
                subDir = "photos",
                prefix = "photo",
                extension = "jpg"
            )
            if (savedPath != null) {
                photosList = photosList + savedPath
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = MediaStorageHelper.copyUriToInternalStorage(
                context = context,
                uri = uri,
                subDir = "videos",
                prefix = "video",
                extension = "mp4"
            )
            if (savedPath != null) {
                videoClipPath = savedPath
            }
        }
    }

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

                        // Date pill
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
                                        imageUris = photosList.joinToString(","),
                                        voiceNoteUri = voiceNotePath,
                                        videoUri = videoClipPath,
                                        doodleData = handwritingStrokesJson,
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
                            minLines = 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("entry_content_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // ATTACHMENT 1: Real Handwriting / Drawing on Paper
                        if (showHandwritingCanvas || handwritingStrokesJson.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "handwritten notes & strokes ✍️",
                                            fontFamily = FontFamily.Cursive,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2C2523)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clickable {
                                                    showHandwritingCanvas = !showHandwritingCanvas
                                                }
                                                .padding(4.dp)
                                        ) {
                                            Text(
                                                text = if (showHandwritingCanvas) "hide canvas" else "edit handwriting",
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 11.sp,
                                                color = Color(0xFF8A7E75)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (showHandwritingCanvas) {
                                        HandwritingJournalCanvas(
                                            initialStrokesJson = handwritingStrokesJson,
                                            height = 280.dp,
                                            onStrokesChanged = { _, json ->
                                                handwritingStrokesJson = json
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // ATTACHMENT 2: Photo Attachments (Polaroid Scrapbook Cards)
                        if (photosList.isNotEmpty()) {
                            Text(
                                text = "tucked snapshots 📷",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2523)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                photosList.forEachIndexed { index, photoPath ->
                                    val rot = if (index % 2 == 0) -2.5f else 2.5f
                                    ScrapbookPhotoCard(
                                        imagePathOrUri = photoPath,
                                        caption = "memory #${index + 1} ♡",
                                        rotation = rot,
                                        onRemove = {
                                            photosList = photosList.filterIndexed { i, _ -> i != index }
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // ATTACHMENT 3: Voice Note Attachment (Playable Scrapbook Card)
                        if (voiceNotePath.isNotBlank()) {
                            Text(
                                text = "voice note 🎙️",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2523)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            ScrapbookVoicePlayerCard(
                                audioPath = voiceNotePath,
                                onDelete = { voiceNotePath = "" }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // ATTACHMENT 4: Video Clip Attachment
                        if (videoClipPath.isNotBlank()) {
                            Text(
                                text = "video memory 🎬",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2523)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            ScrapbookVideoCard(
                                videoPath = videoClipPath,
                                caption = "captured moment ♡",
                                onDelete = { videoClipPath = "" }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Taped kraft reminder note at bottom
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

                    // Bottom Functional Toolbar: Photo, Handwriting, Voice, Video, Mood
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp))
                            .background(Color(0xFFFCFAF4), RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0x22000000), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Photo Picker
                            IconButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.testTag("toolbar_photo_button")
                            ) {
                                Icon(Icons.Default.Image, contentDescription = "Add Photo", tint = Color(0xFF2C2523))
                            }

                            // 2. Real Handwriting / Stylus Toggle
                            IconButton(
                                onClick = {
                                    showHandwritingCanvas = !showHandwritingCanvas
                                },
                                modifier = Modifier.testTag("toolbar_handwriting_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Handwriting",
                                    tint = if (showHandwritingCanvas) Color(0xFFB5704D) else Color(0xFF2C2523)
                                )
                            }

                            // 3. Voice Note Recorder
                            IconButton(
                                onClick = { showVoiceRecorderModal = true },
                                modifier = Modifier.testTag("toolbar_voice_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record Voice",
                                    tint = if (voiceNotePath.isNotBlank()) Color(0xFFB5704D) else Color(0xFF2C2523)
                                )
                            }

                            // 4. Video Clip Picker
                            IconButton(
                                onClick = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                modifier = Modifier.testTag("toolbar_video_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Add Video",
                                    tint = if (videoClipPath.isNotBlank()) Color(0xFFB5704D) else Color(0xFF2C2523)
                                )
                            }

                            // 5. Sticker / Mood
                            IconButton(onClick = { showStickerDialog = true }) {
                                Icon(Icons.Default.Mood, contentDescription = "Mood Stickers", tint = Color(0xFF2C2523))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Voice Recording dialog
    if (showVoiceRecorderModal) {
        Dialog(onDismissRequest = { showVoiceRecorderModal = false }) {
            ScrapbookVoiceRecorderModal(
                onRecordingComplete = { path ->
                    voiceNotePath = path
                    showVoiceRecorderModal = false
                },
                onDismiss = { showVoiceRecorderModal = false }
            )
        }
    }

    // Modal: Quick Sticker/Mood Picker dialog
    if (showStickerDialog) {
        Dialog(onDismissRequest = { showStickerDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAF7F0), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0x33000000), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "pick page sticker ♡",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1D1B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val stickers = listOf("🌿", "☕", "✨", "💌", "🌸", "🥐", "🌧️", "🧸", "🕯️", "🍓", "🕊️", "🌙")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        stickers.forEach { sticker ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFFEFE6D5), RoundedCornerShape(8.dp))
                                    .clickable {
                                        content = if (content.isNotBlank()) "$content $sticker" else sticker
                                        showStickerDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(sticker, fontSize = 22.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens.today

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MemoryEntity
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.CreationOption
import com.example.ui.components.HandmadeHeartCheckbox
import com.example.ui.components.HeartDoodle
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

data class MoodOption(val name: String, val emoji: String, val color: Color)

val DefaultMoods = listOf(
    MoodOption("Happy", "☀️", Color(0xFFFF8A65)),
    MoodOption("Calm", "🌿", Color(0xFF64B5F6)),
    MoodOption("Okay", "☕", Color(0xFF5A4D41)),
    MoodOption("Sad", "🌧️", Color(0xFFBA68C8)),
    MoodOption("Angry", "🔥", Color(0xFFE57373)),
    MoodOption("Tired", "🌙", Color(0xFF90A4AE)),
    MoodOption("Excited", "✨", Color(0xFFFFD54F))
)

data class VibrantMoodOption(
    val name: String,
    val emoji: String,
    val borderColor: Color,
    val bgColor: Color
)

val VibrantMoods = listOf(
    VibrantMoodOption("Happy", "☀️", Color(0xFFFF8A65), Color(0xFFFFF1ED)),
    VibrantMoodOption("Calm", "🌿", Color(0xFF64B5F6), Color(0xFFE3F2FD)),
    VibrantMoodOption("Okay", "☕", Color(0xFF5A4D41), Color(0xFFFDFBF7)),
    VibrantMoodOption("Sad", "🌧️", Color(0xFFBA68C8), Color(0xFFF3E5F5)),
    VibrantMoodOption("Growing", "🌱", Color(0xFF81C784), Color(0xFFE8F5E9))
)

@Composable
fun TodayJournalScreen(
    currentPrompt: String,
    dailyCornerNote: String,
    recentMemory: MemoryEntity?,
    onRollPrompt: () -> Unit,
    onRotateDailyCorner: () -> Unit,
    onMoodSelected: (String, Color) -> Unit,
    onQuickCreate: (CreationOption) -> Unit,
    onWriteJournalEntry: () -> Unit,
    onViewMemory: (MemoryEntity) -> Unit
) {
    val theme = LocalJournalTheme.current
    val scrollState = rememberScrollState()

    // Formatted Dates
    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val now = Date()
    val dayOfWeek = dayOfWeekFormat.format(now)
    val formattedDate = fullDateFormat.format(now)

    var selectedMood by remember { mutableStateOf("Happy") }

    // Interactive highlights/checklist
    val checklistItems = remember {
        mutableStateListOf(
            "drink water ♡" to false,
            "be a little kinder to yourself ♡" to true,
            "get something done (no matter how small) ♡" to false,
            "enjoy the little things ♡" to false,
            "you got this ♡" to false
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Journal Paper surface with ivory tone and notebook spine rings on left
        JournalPageSurface(
            pageNumber = 1,
            pattern = PaperPattern.DOT_GRID,
            hasLeftSpineMargin = false
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Realistic spiral binder spine margin on the left
                NotebookSpineMargin(
                    modifier = Modifier
                        .fillMaxSize()
                        .width(32.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(start = 36.dp, end = 16.dp, top = 12.dp)
                ) {
                    // Top Greeting & Taped Note Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Good Morning,",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E1D1B)
                            )
                            Text(
                                text = "Fiza ♡",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1D1B)
                            )
                        }

                        // Top right taped kraft note: "make today count ♡"
                        TapedPaperScrap(
                            text = "make today count ♡",
                            rotation = 4f,
                            tapeColor = Color(0xFFE2A68C),
                            bgColor = Color(0xFFF3EDE2),
                            fontSize = 11f,
                            tapeWidth = 42.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date Scrap Card + Highlights Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Left Date Paper Scrap with Washi Tape & Pressed Flower
                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .shadow(3.dp, RoundedCornerShape(3.dp))
                                .background(Color(0xFFFCFAF4), RoundedCornerShape(3.dp))
                                .border(0.5.dp, Color(0x33000000), RoundedCornerShape(3.dp))
                                .padding(10.dp)
                        ) {
                            ScrapbookWashiTape(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-14).dp),
                                color = Color(0xFF9EABA2),
                                width = 50.dp,
                                height = 14.dp,
                                rotation = -3f
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = dayOfWeek.uppercase(),
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF2C2523)
                                )
                                Text(
                                    text = formattedDate,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp,
                                    color = Color(0xFF5A4D41)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Pressed flower illustration
                                BotanicalDoodle(
                                    modifier = Modifier.size(32.dp, 40.dp),
                                    tint = Color(0xFF6B5848),
                                    flowerTint = Color(0xFFD49A76)
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "a new day,\na new page ♡",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF4A3C34)
                                )
                            }
                        }

                        // Right Highlights Section with heart bullet points
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .shadow(3.dp, RoundedCornerShape(3.dp))
                                .background(Color(0xFFFCFAF4), RoundedCornerShape(3.dp))
                                .border(0.5.dp, Color(0x33000000), RoundedCornerShape(3.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Today's Highlights",
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1E1D1B)
                                    )
                                    HeartDoodle(color = Color(0xFF2C2523), strokeWidth = 1.2.dp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                checklistItems.forEachIndexed { index, (item, isChecked) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                    ) {
                                        HandmadeHeartCheckbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                checklistItems[index] = item to checked
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = item,
                                            fontFamily = FontFamily.Cursive,
                                            fontSize = 12.sp,
                                            color = if (isChecked) Color(0xFF7A6E65) else Color(0xFF2C2523),
                                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Over Perfection Taped Note
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TapedPaperScrap(
                            text = "progress over perfection ♡",
                            rotation = -2f,
                            tapeColor = Color(0xFFD49A76),
                            bgColor = Color(0xFFF0EAE1),
                            fontSize = 12f
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mood Tracker Strip (5 cute mood check-ins)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(4.dp))
                            .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                            .border(0.5.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "how are you feeling today?",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E1D1B)
                                )
                                Text(
                                    text = selectedMood.lowercase(),
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5A4D41)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VibrantMoods.forEach { mood ->
                                    val isSelected = selectedMood == mood.name
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .shadow(if (isSelected) 3.dp else 1.dp, CircleShape)
                                            .background(mood.bgColor, CircleShape)
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) Color(0xFF1E1D1B) else mood.borderColor,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                selectedMood = mood.name
                                                onMoodSelected(mood.name, mood.borderColor)
                                            }
                                            .testTag("mood_${mood.name.lowercase()}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = mood.emoji, fontSize = 18.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Center & Bottom Scrapbook Collage: Polaroid + Sticky Gratitude Note + Happier/Healthier Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left: Tilted Polaroid with photo & smiley sticker
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .rotate(-4f)
                                .shadow(6.dp, RoundedCornerShape(2.dp))
                                .background(Color(0xFFFFFFFF), RoundedCornerShape(2.dp))
                                .border(0.5.dp, Color(0x22000000), RoundedCornerShape(2.dp))
                                .padding(8.dp)
                        ) {
                            // Pink Washi tape on top
                            ScrapbookWashiTape(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-14).dp),
                                color = Color(0xFFE8A598),
                                width = 50.dp,
                                height = 12.dp,
                                rotation = 3f
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(85.dp)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                listOf(Color(0xFFF3A78A), Color(0xFF7A687F), Color(0xFF424B66))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🌅 🌾", fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "sunsets & soft thoughts ♡",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 10.sp,
                                    color = Color(0xFF2C2523),
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Yellow smiley sticker on bottom right corner of Polaroid
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 6.dp, y = 6.dp)
                                    .size(20.dp)
                                    .background(Color(0xFFFFD54F), CircleShape)
                                    .border(1.dp, Color(0xFF2C2523), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("😊", fontSize = 11.sp)
                            }
                        }

                        // Right Column: Sticky Gratitude Note + Happier/Healthier Card
                        Column(
                            modifier = Modifier.weight(1.1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Sticky note: "still so much to be grateful for ♡"
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .rotate(2f)
                                    .shadow(4.dp, RoundedCornerShape(2.dp))
                                    .background(Color(0xFFFFF9C4), RoundedCornerShape(2.dp))
                                    .border(0.5.dp, Color(0x22000000), RoundedCornerShape(2.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "DAILY GRATITUDE",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        letterSpacing = 1.sp,
                                        color = Color(0xFF5A4D41)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "still so much to be grateful for ♡",
                                        fontFamily = FontFamily.Cursive,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1D1B)
                                    )
                                }
                            }

                            // Taped card: "a happier healthier kinder more you ♡"
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .rotate(-1f)
                                    .shadow(4.dp, RoundedCornerShape(2.dp))
                                    .background(Color(0xFFE8DFCD), RoundedCornerShape(2.dp))
                                    .border(0.5.dp, Color(0x22000000), RoundedCornerShape(2.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "a happier, healthier,\nkinder, more you ♡",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C2523),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Prompt Card with Roll Dice Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(4.dp))
                            .background(Color(0xFFF3EDE2), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DAILY PROMPT 🎲",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF5A4D41)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "“$currentPrompt”",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E1D1B)
                                )
                            }

                            IconButton(
                                onClick = onRollPrompt,
                                modifier = Modifier.testTag("roll_prompt_dice")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = "Roll prompt",
                                    tint = Color(0xFF2C2523)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Write Today's Journal Entry CTA Button
                    Button(
                        onClick = onWriteJournalEntry,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24201E)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("write_journal_entry_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Write entry",
                            tint = Color(0xFFFBF8F2),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "write today's entry ♡",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFFFBF8F2)
                        )
                    }

                    // Generous bottom spacer so the final action button scrolls comfortably above the bottom navigation bar
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

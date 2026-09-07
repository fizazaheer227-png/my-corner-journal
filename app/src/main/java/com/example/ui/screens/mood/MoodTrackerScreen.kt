package com.example.ui.screens.mood

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.MoodLogEntity
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.WashiTape
import com.example.ui.screens.today.DefaultMoods
import com.example.ui.theme.LocalJournalTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class MoodSpreadStyle(val label: String, val icon: String) {
    FLOWERS("Flower Garden", "🌸"),
    STARS("Night Sky", "⭐"),
    LEAVES("Pressed Leaves", "🌿"),
    CIRCLES("Ink Stamps", "⭕")
}

@Composable
fun MoodTrackerScreen(
    moodLogs: List<MoodLogEntity>,
    onLogMood: (mood: String, note: String, colorHex: Long) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val theme = LocalJournalTheme.current
    var selectedSpreadStyle by remember { mutableStateOf(MoodSpreadStyle.FLOWERS) }
    var showLogDialogForDay by remember { mutableStateOf<Int?>(null) }

    val currentMonthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    val cal = Calendar.getInstance()
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val todayDay = cal.get(Calendar.DAY_OF_MONTH)

    // Map logs by day of month (assuming current month)
    val logsByDay = remember(moodLogs) {
        val map = mutableMapOf<Int, MoodLogEntity>()
        val monthPrefix = SimpleDateFormat("yyyy-MM-", Locale.getDefault()).format(Date())
        moodLogs.forEach { log ->
            if (log.dateString.startsWith(monthPrefix)) {
                val day = log.dateString.substringAfterLast("-").toIntOrNull()
                if (day != null) {
                    map[day] = log
                }
            }
        }
        map
    }

    // Soft, non-judgmental summary
    val moodCounts = logsByDay.values.groupBy { it.mood }.mapValues { it.value.size }
    val dominantMood = moodCounts.maxByOrNull { it.value }?.key ?: "gentle moments"
    val summaryText = if (logsByDay.isNotEmpty()) {
        "this month has been holding mostly $dominantMood moments."
    } else {
        "your flower garden begins with each day you color in."
    }

    JournalPageSurface(
        pageNumber = 4,
        pattern = PaperPattern.DOT_GRID,
        hasLeftSpineMargin = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 16.dp, top = 14.dp, bottom = 0.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("mood_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = theme.textColor
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Column {
                        Text(
                            text = "mood garden",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = theme.textColor
                        )
                        Text(
                            text = currentMonthYear.lowercase(),
                            fontFamily = FontFamily.Cursive,
                            fontSize = 16.sp,
                            color = theme.secondaryTextColor
                        )
                    }
                }
                WashiTape(width = 75.dp, height = 18.dp, rotation = -2f)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Gentle Monthly Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(6.dp))
                    .border(0.8.dp, Color(0x22000000), RoundedCornerShape(6.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "“$summaryText”",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 15.sp,
                    color = theme.textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spread Style Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MoodSpreadStyle.values().forEach { style ->
                    val isSelected = selectedSpreadStyle == style
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) theme.coverAccentColor else Color(0xFFEDE5D8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedSpreadStyle = style }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${style.icon} ${style.label}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White else Color(0xFF3E2D23)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Days Grid (Interactive Flower Garden / Night Sky / Leaves / Circles)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items((1..daysInMonth).toList()) { day ->
                    val log = logsByDay[day]
                    val isToday = day == todayDay
                    val fillColor = if (log != null) Color(log.colorHex) else Color(0x18000000)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { showLogDialogForDay = day }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(if (log != null) 3.dp else 0.dp, CircleShape)
                                .background(Color(0xFFFCFAF4), CircleShape)
                                .border(
                                    width = if (isToday) 1.5.dp else 0.5.dp,
                                    color = if (isToday) theme.coverColor else Color(0x33000000),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Hand-drawn graphic shape based on style
                            when (selectedSpreadStyle) {
                                MoodSpreadStyle.FLOWERS -> {
                                    Canvas(modifier = Modifier.size(30.dp)) {
                                        val center = Offset(size.width / 2, size.height / 2)
                                        // 5 Petals
                                        for (i in 0 until 5) {
                                            val angle = i * (Math.PI * 2 / 5)
                                            val x = center.x + (10 * Math.cos(angle)).toFloat()
                                            val y = center.y + (10 * Math.sin(angle)).toFloat()
                                            drawCircle(color = fillColor, radius = 5.dp.toPx(), center = Offset(x, y))
                                        }
                                        drawCircle(color = Color(0xFFFFF1C5), radius = 4.dp.toPx(), center = center)
                                    }
                                }
                                MoodSpreadStyle.STARS -> {
                                    Canvas(modifier = Modifier.size(28.dp)) {
                                        val center = Offset(size.width / 2, size.height / 2)
                                        drawCircle(color = fillColor, radius = 10.dp.toPx(), center = center)
                                    }
                                }
                                MoodSpreadStyle.LEAVES -> {
                                    Canvas(modifier = Modifier.size(28.dp)) {
                                        val path = Path().apply {
                                            moveTo(size.width / 2, 2.dp.toPx())
                                            cubicTo(size.width, size.height * 0.3f, size.width, size.height * 0.7f, size.width / 2, size.height - 2.dp.toPx())
                                            cubicTo(0f, size.height * 0.7f, 0f, size.height * 0.3f, size.width / 2, 2.dp.toPx())
                                        }
                                        drawPath(path = path, color = fillColor)
                                    }
                                }
                                MoodSpreadStyle.CIRCLES -> {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(fillColor, CircleShape)
                                    )
                                }
                            }

                            // Day number inside
                            Text(
                                text = "$day",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (log != null) Color(0xFF1E1D1B) else theme.secondaryTextColor
                            )
                        }

                        if (log != null) {
                            Text(
                                text = log.mood.lowercase(),
                                fontFamily = FontFamily.Cursive,
                                fontSize = 11.sp,
                                color = theme.textColor
                            )
                        }
                    }
                }
            }

            // Legend at bottom
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DefaultMoods.forEach { m ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(m.color, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = m.name.lowercase(), fontSize = 11.sp, fontFamily = FontFamily.Serif, color = theme.secondaryTextColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    // Day Mood Log / Edit Dialog
    if (showLogDialogForDay != null) {
        val day = showLogDialogForDay!!
        var noteInput by remember { mutableStateOf(logsByDay[day]?.note ?: "") }
        var selectedMoodForDialog by remember { mutableStateOf(logsByDay[day]?.mood ?: "Calm") }

        Dialog(onDismissRequest = { showLogDialogForDay = null }) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .shadow(12.dp, RoundedCornerShape(12.dp))
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0x33000000), RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "day $day mood flower",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = theme.textColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DefaultMoods.forEach { moodItem ->
                            val isSel = selectedMoodForDialog == moodItem.name
                            Box(
                                modifier = Modifier
                                    .background(if (isSel) moodItem.color else moodItem.color.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    .clickable { selectedMoodForDialog = moodItem.name }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "${moodItem.emoji} ${moodItem.name}", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        placeholder = { Text("little note about today...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val chosenColor = DefaultMoods.firstOrNull { it.name == selectedMoodForDialog }?.color ?: Color(0xFF83C5BE)
                            val colorLong = (chosenColor.value.toLong() shr 32) and 0xFFFFFFFF
                            onLogMood(selectedMoodForDialog, noteInput, chosenColor.value.toLong())
                            showLogDialogForDay = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("color in petal 🌸", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }
}

package com.example.ui.screens.period

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.PeriodLogEntity
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.HeartDoodle
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.NotebookSpineMargin
import com.example.ui.components.PaperClip
import com.example.ui.components.PaperPattern
import com.example.ui.components.ScrapbookWashiTape
import com.example.ui.components.TapedPaperScrap
import com.example.ui.theme.LocalJournalTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

val NonGraphicSymptoms = listOf(
    "cramps",
    "headache",
    "tiredness",
    "bloating",
    "acne",
    "cravings",
    "backache",
    "mood swings",
    "tender breasts"
)

val GentleCycleMoods = listOf(
    "calm ☕",
    "sensitive 🌧️",
    "cozy 🕯️",
    "low energy 🛋️",
    "emotional 💌",
    "gentle 🌿",
    "happy ☀️"
)

val FlowOptions = listOf(
    "Spotting" to "🌸",
    "Light" to "💧",
    "Medium" to "💧💧",
    "Heavy" to "💧💧💧"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PeriodTrackerScreen(
    periodLogs: List<PeriodLogEntity>,
    onLogPeriod: (startDate: String, endDate: String, flow: String, symptoms: String, mood: String, notes: String) -> Unit,
    onDeletePeriod: (Long) -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalJournalTheme.current
    val scrollState = rememberScrollState()

    var showLogDialog by remember { mutableStateOf(false) }
    var calendarMonthOffset by remember { mutableStateOf(0) } // 0 = current month, -1 = prev, +1 = next

    // Calendar Calculations
    val calendar = remember(calendarMonthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, calendarMonthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val currentMonthYearLabel = remember(calendarMonthOffset) {
        monthYearFormat.format(calendar.time).lowercase()
    }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
    val yearMonthPrefix = remember(calendarMonthOffset) {
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
    }

    // Today's date string
    val todayString = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Map of dates to period logs
    val loggedDatesMap = remember(periodLogs) {
        val map = mutableMapOf<String, PeriodLogEntity>()
        periodLogs.forEach { log ->
            map[log.startDate] = log
        }
        map
    }

    // Calculate approximate cycle and period statistics
    val cycleStats by remember(periodLogs) {
        derivedStateOf {
            calculateApproximateCycleStats(periodLogs)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        JournalPageSurface(
            pageNumber = 17,
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
                        .verticalScroll(scrollState)
                        .padding(start = 36.dp, end = 16.dp, top = 12.dp, bottom = 120.dp)
                ) {
                    // Header Bar with Back button and Private indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("period_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Pages",
                                    tint = theme.textColor
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "botanical cycle notes 🌸",
                                    fontFamily = FontFamily.Cursive,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = Color(0xFF2C2523)
                                )
                                Text(
                                    text = "for a kinder, more informed you ♡",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B5848)
                                )
                            }
                        }

                        // Discreet lock badge
                        Box(
                            modifier = Modifier
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .background(Color(0xFFF3EAE1), RoundedCornerShape(12.dp))
                                .border(0.5.dp, Color(0x33000000), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Private lock",
                                    tint = Color(0xFF8A6D58),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "private",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6E5645)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Monthly Hand-Drawn Calendar Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(4.dp))
                            .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                            .border(0.5.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                            .padding(14.dp)
                    ) {
                        ScrapbookWashiTape(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-18).dp),
                            color = Color(0xFFE8A598),
                            width = 64.dp,
                            height = 14.dp,
                            rotation = -2f
                        )

                        Column {
                            // Calendar Month Switcher
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { calendarMonthOffset -= 1 },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month", tint = Color(0xFF5A4D41))
                                }

                                Text(
                                    text = currentMonthYearLabel,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1E1D1B)
                                )

                                IconButton(
                                    onClick = { calendarMonthOffset += 1 },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Next month", tint = Color(0xFF5A4D41))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Day of Week Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                                    Text(
                                        text = day,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF8A7A6E),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Days Grid
                            val totalSlots = (firstDayOfWeek - 1) + daysInMonth
                            val totalRows = (totalSlots + 6) / 7

                            for (row in 0 until totalRows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    for (col in 0 until 7) {
                                        val slotIndex = row * 7 + col
                                        val dayNumber = slotIndex - (firstDayOfWeek - 1) + 1

                                        if (dayNumber in 1..daysInMonth) {
                                            val dayString = String.format("%s-%02d", yearMonthPrefix, dayNumber)
                                            val isToday = dayString == todayString
                                            val log = loggedDatesMap[dayString]
                                            val isLoggedPeriod = log != null
                                            val isApproxNextWindow = cycleStats.estimatedNextDateString == dayString

                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clickable {
                                                        showLogDialog = true
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isLoggedPeriod) {
                                                    // Watercolor pink / rose highlight circle
                                                    Box(
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .background(Color(0xFFF7C8D0), CircleShape)
                                                            .border(1.dp, Color(0xFFE57373), CircleShape)
                                                    )
                                                } else if (isApproxNextWindow) {
                                                    // Dashed floral estimate circle
                                                    Canvas(modifier = Modifier.size(28.dp)) {
                                                        drawCircle(
                                                            color = Color(0xFFD49A76),
                                                            style = Stroke(
                                                                width = 1.2.dp.toPx(),
                                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                                                            )
                                                        )
                                                    }
                                                } else if (isToday) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .border(1.dp, Color(0xFF5A4D41), CircleShape)
                                                    )
                                                }

                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "$dayNumber",
                                                        fontFamily = FontFamily.Serif,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isLoggedPeriod || isToday) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isLoggedPeriod) Color(0xFF5C1D24) else Color(0xFF2C2523)
                                                    )
                                                    if (isLoggedPeriod && log?.symptoms?.isNotBlank() == true) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(3.dp)
                                                                .background(Color(0xFF8B424C), CircleShape)
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.size(36.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Approximate Estimates Card (with clear disclaimer)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(3.dp))
                            .background(Color(0xFFF9F5EC), RoundedCornerShape(3.dp))
                            .border(0.5.dp, Color(0x33000000), RoundedCornerShape(3.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "gentle rhythm & approximations ♡",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF2C2523)
                                )
                                BotanicalDoodle(modifier = Modifier.size(20.dp), tint = Color(0xFF9E8A78))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "approx. cycle length",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 11.sp,
                                        color = Color(0xFF7A6E65)
                                    )
                                    Text(
                                        text = "~${cycleStats.avgCycleLengthDays} days",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2C2523)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "approx. period length",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 11.sp,
                                        color = Color(0xFF7A6E65)
                                    )
                                    Text(
                                        text = "~${cycleStats.avgPeriodLengthDays} days",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2C2523)
                                    )
                                }

                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(
                                        text = "next cycle estimate",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 11.sp,
                                        color = Color(0xFF7A6E65)
                                    )
                                    Text(
                                        text = cycleStats.nextCycleEstimateDescription,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF8B424C)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Crucial Medical Certainty Disclaimer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEFE8DB), RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "♡ note: predictions are gentle approximations calculated from your logged entries, not medical certainty or advice.",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B5848),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Log Button
                    Button(
                        onClick = { showLogDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8989F)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(20.dp))
                            .testTag("log_period_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌸", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+ log cycle note",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Past Cycle Logs (Scrapbook List)
                    Text(
                        text = "past cycle reflections 📜",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF2C2523)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (periodLogs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "no cycle notes logged yet.\ntap '+ log cycle note' whenever you're ready ♡",
                                fontFamily = FontFamily.Cursive,
                                fontSize = 14.sp,
                                color = Color(0xFF7A6E65),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        periodLogs.forEach { log ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .shadow(2.dp, RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "🌸 ${log.startDate}",
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF2C2523)
                                            )
                                            if (log.endDate.isNotBlank() && log.endDate != log.startDate) {
                                                Text(
                                                    text = " → ${log.endDate}",
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF6E5645)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeletePeriod(log.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete entry",
                                                tint = Color(0xFF9E8A78),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Flow and Mood Pills
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFF7D8DE), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "flow: ${log.flow.lowercase()}",
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 11.sp,
                                                color = Color(0xFF5C1D24)
                                            )
                                        }

                                        if (log.mood.isNotBlank()) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFEFE6D5), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = log.mood,
                                                    fontFamily = FontFamily.Cursive,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF4A3C34)
                                                )
                                            }
                                        }
                                    }

                                    if (log.symptoms.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "feelings: ${log.symptoms}",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 11.sp,
                                            color = Color(0xFF7A6E65)
                                        )
                                    }

                                    if (log.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "“${log.notes}”",
                                            fontFamily = FontFamily.Cursive,
                                            fontSize = 13.sp,
                                            color = Color(0xFF382920)
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

    // Log Period Modal Dialog
    if (showLogDialog) {
        PeriodLogDialog(
            defaultStartDate = todayString,
            onDismiss = { showLogDialog = false },
            onSave = { sDate, eDate, flow, symptoms, mood, notes ->
                onLogPeriod(sDate, eDate, flow, symptoms, mood, notes)
                showLogDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PeriodLogDialog(
    defaultStartDate: String,
    onDismiss: () -> Unit,
    onSave: (startDate: String, endDate: String, flow: String, symptoms: String, mood: String, notes: String) -> Unit
) {
    var startDate by remember { mutableStateOf(defaultStartDate) }
    var durationDays by remember { mutableStateOf(5) }
    var selectedFlow by remember { mutableStateOf("Medium") }
    val selectedSymptoms = remember { mutableStateListOf<String>() }
    var selectedMood by remember { mutableStateOf("calm ☕") }
    var notes by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }

    val computedEndDate = remember(startDate, durationDays) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(startDate) ?: Date()
            val cal = Calendar.getInstance().apply {
                time = date
                add(Calendar.DAY_OF_MONTH, durationDays - 1)
            }
            sdf.format(cal.time)
        } catch (e: Exception) {
            startDate
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(14.dp))
                .background(Color(0xFFFCFAF4), RoundedCornerShape(14.dp))
                .border(1.dp, Color(0x33000000), RoundedCornerShape(14.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Taped header scrap
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "log cycle note 🌸",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2C2523)
                    )
                    HeartDoodle(color = Color(0xFFE8989F), strokeWidth = 1.5.dp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Start Date
                Text(
                    text = "start date (YYYY-MM-DD):",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = Color(0xFF5A4D41)
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("period_start_date_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE8989F),
                        unfocusedBorderColor = Color(0x33000000)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Duration Selector
                Text(
                    text = "approximate duration: $durationDays days (ends $computedEndDate)",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = Color(0xFF5A4D41)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(3, 4, 5, 6, 7).forEach { days ->
                        val isSel = durationDays == days
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSel) 2.dp else 0.dp, RoundedCornerShape(6.dp))
                                .background(
                                    if (isSel) Color(0xFFE8989F) else Color(0xFFF3EDE2),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { durationDays = days }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$days d",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                color = if (isSel) Color.White else Color(0xFF382920)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Flow
                Text(
                    text = "flow intensity:",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = Color(0xFF5A4D41)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FlowOptions.forEach { (flowName, icon) ->
                        val isSel = selectedFlow == flowName
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSel) 2.dp else 0.dp, RoundedCornerShape(6.dp))
                                .background(
                                    if (isSel) Color(0xFFE8989F) else Color(0xFFF3EDE2),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedFlow = flowName }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(icon, fontSize = 12.sp)
                                Text(
                                    text = flowName,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 10.sp,
                                    color = if (isSel) Color.White else Color(0xFF382920)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Symptoms (Non-graphic options)
                Text(
                    text = "how does your body feel?",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = Color(0xFF5A4D41)
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NonGraphicSymptoms.forEach { symptom ->
                        val isChecked = selectedSymptoms.contains(symptom)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (isChecked) selectedSymptoms.remove(symptom)
                                else selectedSymptoms.add(symptom)
                            },
                            label = {
                                Text(
                                    text = symptom,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE8989F),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mood
                Text(
                    text = "gentle mood:",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = Color(0xFF5A4D41)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GentleCycleMoods.forEach { mood ->
                        val isSel = selectedMood == mood
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSel) Color(0xFFD4A373) else Color(0xFFF3EDE2),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedMood = mood }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = mood,
                                fontFamily = FontFamily.Cursive,
                                fontSize = 12.sp,
                                color = if (isSel) Color.White else Color(0xFF382920)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes / Comfort Thoughts
                Text(
                    text = "comfort notes / cravings / reflections:",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = Color(0xFF5A4D41)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = {
                        Text(
                            "e.g. cozy chamomile tea, warm water bottle, resting early...",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 12.sp,
                            color = Color(0xFF9E8E82)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE8989F),
                        unfocusedBorderColor = Color(0x33000000)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Optional reminder setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (reminderEnabled) Icons.Default.Notifications else Icons.Default.NotificationsNone,
                            contentDescription = "Reminders",
                            tint = Color(0xFF8A6D58),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "gentle reminder 2 days before estimate",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = Color(0xFF5A4D41)
                        )
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFE8989F)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Save and Cancel buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECE4D6))
                    ) {
                        Text("cancel", fontFamily = FontFamily.Serif, color = Color(0xFF382920), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val symptomsStr = selectedSymptoms.joinToString(", ")
                            onSave(startDate, computedEndDate, selectedFlow, symptomsStr, selectedMood, notes)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8989F)),
                        modifier = Modifier.testTag("save_period_log_button")
                    ) {
                        Text("save to journal ♡", fontFamily = FontFamily.Serif, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

data class CycleStats(
    val avgCycleLengthDays: Int,
    val avgPeriodLengthDays: Int,
    val estimatedNextDateString: String?,
    val nextCycleEstimateDescription: String
)

fun calculateApproximateCycleStats(logs: List<PeriodLogEntity>): CycleStats {
    val defaultCycle = 28
    val defaultPeriod = 5
    if (logs.isEmpty()) {
        return CycleStats(
            avgCycleLengthDays = defaultCycle,
            avgPeriodLengthDays = defaultPeriod,
            estimatedNextDateString = null,
            nextCycleEstimateDescription = "log 1-2 cycles to see estimate"
        )
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sortedDates = logs.mapNotNull {
        try { sdf.parse(it.startDate) } catch (e: Exception) { null }
    }.sorted()

    if (sortedDates.isEmpty()) {
        return CycleStats(defaultCycle, defaultPeriod, null, "log 1-2 cycles to see estimate")
    }

    // Average cycle length between start dates
    val intervals = mutableListOf<Long>()
    for (i in 0 until sortedDates.size - 1) {
        val diffMs = sortedDates[i + 1].time - sortedDates[i].time
        val diffDays = diffMs / (1000 * 60 * 60 * 24)
        if (diffDays in 15..60) {
            intervals.add(diffDays)
        }
    }

    val avgCycle = if (intervals.isNotEmpty()) intervals.average().roundToInt() else defaultCycle

    // Calculate approximate next date
    val latestDate = sortedDates.last()
    val nextCal = Calendar.getInstance().apply {
        time = latestDate
        add(Calendar.DAY_OF_MONTH, avgCycle)
    }
    val nextDate = nextCal.time
    val nextDateStr = sdf.format(nextDate)

    val today = Date()
    val daysUntilNext = ((nextDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()

    val desc = when {
        daysUntilNext > 0 -> "in ~$daysUntilNext days"
        daysUntilNext == 0 -> "expected today (~approx)"
        else -> "~${-daysUntilNext} days past approx"
    }

    return CycleStats(
        avgCycleLengthDays = avgCycle,
        avgPeriodLengthDays = defaultPeriod,
        estimatedNextDateString = nextDateStr,
        nextCycleEstimateDescription = desc
    )
}

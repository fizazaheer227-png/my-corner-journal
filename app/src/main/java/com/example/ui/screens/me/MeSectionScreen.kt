package com.example.ui.screens.me

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.LetterEntity
import com.example.data.local.entity.PeriodLogEntity
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.PolaroidCard
import com.example.ui.components.WashiTape
import com.example.ui.theme.LocalJournalTheme

enum class MeSubPage(val label: String, val icon: String) {
    HABIT_GARDEN("Habit Garden", "🌱"),
    CURRENTLY("Currently...", "🎧"),
    GRATITUDE("Daily Gratitude", "☕"),
    PERIOD_TRACKER("Cycle Notes", "🌸"),
    STATIONERY_LETTERS("Letters", "💌"),
    PIECES_OF_ME("Pieces of Me", "✨")
}

@Composable
fun MeSectionScreen(
    habits: List<HabitEntity>,
    periodLogs: List<PeriodLogEntity>,
    letters: List<LetterEntity>,
    onCompleteHabit: (HabitEntity) -> Unit,
    onAddHabit: (String, String) -> Unit,
    onLogPeriod: (startDate: String, flow: String, symptoms: String, notes: String) -> Unit,
    onSaveLetter: (LetterEntity) -> Unit,
    onOpenDoodle: () -> Unit,
    onOpenBrainDump: () -> Unit,
    onOpenSettings: () -> Unit,
    initialSubPage: MeSubPage = MeSubPage.HABIT_GARDEN,
    isDedicatedPage: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    val theme = LocalJournalTheme.current
    var currentSubPage by remember(initialSubPage) { mutableStateOf(initialSubPage) }

    JournalPageSurface(
        pageNumber = 12,
        pattern = PaperPattern.RULED_LINES,
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
                        IconButton(onClick = onBack, modifier = Modifier.testTag("me_back_button")) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = theme.textColor
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Column {
                        val headerTitle = when {
                            isDedicatedPage && currentSubPage == MeSubPage.HABIT_GARDEN -> "my habit garden 🌱"
                            isDedicatedPage && currentSubPage == MeSubPage.CURRENTLY -> "currently in this era 🎧"
                            isDedicatedPage && currentSubPage == MeSubPage.GRATITUDE -> "daily gratitude ☕"
                            isDedicatedPage && currentSubPage == MeSubPage.STATIONERY_LETTERS -> "stationery letters 💌"
                            isDedicatedPage && currentSubPage == MeSubPage.PIECES_OF_ME -> "pieces of me ✨"
                            else -> "my inner garden 🌿"
                        }
                        val headerSubtitle = when {
                            isDedicatedPage && currentSubPage == MeSubPage.HABIT_GARDEN -> "tiny daily seeds blooming into gentle habits"
                            isDedicatedPage && currentSubPage == MeSubPage.CURRENTLY -> "music on repeat, reading, craving, feeling"
                            isDedicatedPage && currentSubPage == MeSubPage.GRATITUDE -> "three tiny joys to hold close"
                            isDedicatedPage && currentSubPage == MeSubPage.STATIONERY_LETTERS -> "warm thoughts written down and sealed"
                            isDedicatedPage && currentSubPage == MeSubPage.PIECES_OF_ME -> "quotes, tiny reflections & milestones"
                            else -> "rituals, habits & letters that stay safe"
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

                Button(
                    onClick = onOpenSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.coverAccentColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("open_settings_button")
                ) {
                    Text("settings / book ⚙️", fontFamily = FontFamily.Serif, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-nav strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MeSubPage.values().forEach { subPage ->
                    val isSel = currentSubPage == subPage
                    Box(
                        modifier = Modifier
                            .background(if (isSel) theme.coverColor else Color(0xFFEDE5D8), RoundedCornerShape(12.dp))
                            .clickable { currentSubPage = subPage }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("me_tab_${subPage.name.lowercase()}")
                    ) {
                        Text(
                            text = "${subPage.icon} ${subPage.label}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = if (isSel) Color.White else Color(0xFF382920)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-pages content
            AnimatedContent(
                targetState = currentSubPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "meSubPageAnim",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    MeSubPage.HABIT_GARDEN -> HabitGardenView(
                        habits = habits,
                        onComplete = onCompleteHabit,
                        onAdd = onAddHabit
                    )
                    MeSubPage.CURRENTLY -> CurrentlyScrapbookView()
                    MeSubPage.GRATITUDE -> GratitudeJournalView()
                    MeSubPage.PERIOD_TRACKER -> BotanicalPeriodTrackerView(
                        periodLogs = periodLogs,
                        onLog = onLogPeriod
                    )
                    MeSubPage.STATIONERY_LETTERS -> LettersStationeryView(
                        letters = letters,
                        onSave = onSaveLetter
                    )
                    MeSubPage.PIECES_OF_ME -> PiecesOfMeView()
                }
            }
        }
    }
}

// 1. HABIT GARDEN (Gentle, flowers never die)
@Composable
fun HabitGardenView(
    habits: List<HabitEntity>,
    onComplete: (HabitEntity) -> Unit,
    onAdd: (String, String) -> Unit
) {
    val theme = LocalJournalTheme.current
    var showAddDialog by remember { mutableStateOf(false) }

    val defaultHabits = remember {
        listOf(
            HabitEntity(id = -301, title = "drink a tall glass of cool water", plantType = "daisy", totalGrowth = 4),
            HabitEntity(id = -302, title = "step outside for 5 mins of fresh air", plantType = "sunflower", totalGrowth = 3),
            HabitEntity(id = -303, title = "speak kindly to my inner reflection", plantType = "rose", totalGrowth = 2),
            HabitEntity(id = -304, title = "read 5 pages of poetry or fiction", plantType = "fern", totalGrowth = 5),
            HabitEntity(id = -305, title = "stretch shoulders & breathe deeply", plantType = "lavender", totalGrowth = 1),
            HabitEntity(id = -306, title = "unplug phone 30 mins before sleeping", plantType = "succulent", totalGrowth = 2)
        )
    }

    val displayHabits = if (habits.isEmpty()) defaultHabits else habits

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3F8F2), RoundedCornerShape(8.dp))
                .border(0.8.dp, Color(0x3383C5BE), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Text(
                text = "“a plant does not die if you miss a day. it just waits gently for you.”",
                fontFamily = FontFamily.Cursive,
                fontSize = 14.sp,
                color = Color(0xFF2E4D34),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayHabits, key = { it.id }) { habit ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(6.dp))
                        .background(Color(0xFFFCFAF4), RoundedCornerShape(6.dp))
                        .border(0.5.dp, Color(0x22000000), RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Flower stage icon
                            val flowerEmoji = when (habit.totalGrowth) {
                                0 -> "🌱"
                                1 -> "🌿"
                                2 -> "🪴"
                                3 -> "🌸"
                                else -> "🌻"
                            }
                            Text(text = flowerEmoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = habit.title,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = theme.textColor
                                )
                                Text(
                                    text = "bloomed ${habit.totalGrowth} times with love",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 13.sp,
                                    color = theme.secondaryTextColor
                                )
                            }
                        }

                        Button(
                            onClick = { onComplete(habit) },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("water_habit_${habit.id}")
                        ) {
                            Text("water 🌱", fontFamily = FontFamily.Serif, fontSize = 11.sp)
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
            modifier = Modifier.align(Alignment.CenterHorizontally).testTag("add_habit_button")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add plant", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("plant a new habit 🌱", fontFamily = FontFamily.Serif)
        }

        Spacer(modifier = Modifier.height(96.dp))
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var plantType by remember { mutableStateOf("daisy") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .shadow(12.dp, RoundedCornerShape(10.dp))
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(10.dp))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "plant a gentle seed 🌱", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = theme.textColor)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("What habit to nurture?") },
                        placeholder = { Text("e.g. fresh air, warm tea...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(title, plantType)
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor)
                    ) {
                        Text("plant seed in garden ♡", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }
}

// 2. CURRENTLY... VIEW
@Composable
fun CurrentlyScrapbookView() {
    val theme = LocalJournalTheme.current
    var listeningTo by remember { mutableStateOf("acoustic folk & coffee shop rain sounds") }
    var watching by remember { mutableStateOf("Studio Ghibli films under cozy blankets") }
    var reading by remember { mutableStateOf("poetry anthology by Mary Oliver") }
    var loving by remember { mutableStateOf("the crisp smell of morning air before anyone is awake") }
    var craving by remember { mutableStateOf("cinnamon rolls with extra glaze") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WashiTape(width = 110.dp, rotation = -2.5f)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "currently in this era...",
            fontFamily = FontFamily.Cursive,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textColor
        )
        Spacer(modifier = Modifier.height(12.dp))

        listOf(
            "listening to:" to listeningTo,
            "watching:" to watching,
            "reading:" to reading,
            "loving:" to loving,
            "craving:" to craving
        ).forEach { (label, value) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                    .border(0.6.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(text = label, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = theme.coverAccentColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = value, fontFamily = FontFamily.Cursive, fontSize = 15.sp, color = theme.textColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

// 3. DAILY GRATITUDE
@Composable
fun GratitudeJournalView() {
    val theme = LocalJournalTheme.current
    var joy1 by remember { mutableStateOf("the warmth of fresh bread from the oven") }
    var joy2 by remember { mutableStateOf("how quiet the world gets right after sunset") }
    var joy3 by remember { mutableStateOf("a text from someone asking if I got home safe") }
    var personAppreciate by remember { mutableStateOf("my best friend who never judges my silly rambles") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WashiTape(width = 100.dp, rotation = 3f)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "three tiny joys from today ☕",
            fontFamily = FontFamily.Cursive,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textColor
        )
        Spacer(modifier = Modifier.height(12.dp))

        listOf("1." to joy1, "2." to joy2, "3." to joy3).forEach { (num, joy) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = num, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = theme.coverColor)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                        .border(0.5.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                        .padding(10.dp)
                ) {
                    Text(text = joy, fontFamily = FontFamily.Cursive, fontSize = 15.sp, color = theme.textColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "one person I deeply appreciate:", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = theme.textColor)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF8EE), RoundedCornerShape(6.dp))
                .border(1.dp, Color(0x33E29578), RoundedCornerShape(6.dp))
                .padding(12.dp)
        ) {
            Text(text = "“$personAppreciate”", fontFamily = FontFamily.Cursive, fontSize = 16.sp, color = Color(0xFF4A3E38))
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

// 4. PRIVATE BOTANICAL PERIOD TRACKER
@Composable
fun BotanicalPeriodTrackerView(
    periodLogs: List<PeriodLogEntity>,
    onLog: (startDate: String, flow: String, symptoms: String, notes: String) -> Unit
) {
    val theme = LocalJournalTheme.current
    var showLogDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(6.dp))
                .background(Color(0xFFFFF6F5), RoundedCornerShape(6.dp))
                .border(1.dp, Color(0x33DDA15E), RoundedCornerShape(6.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌸", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "botanical cycle notes (strictly private)",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF5C4033)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "predicted next cycle: in ~12 days • feeling: craving warmth & cozy tea",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 14.sp,
                    color = Color(0xFF756A63)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "cycle entries:", fontFamily = FontFamily.Serif, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = theme.textColor)
            Button(
                onClick = { showLogDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+ log cycle", fontSize = 11.sp, fontFamily = FontFamily.Serif)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (periodLogs.isEmpty()) {
            Text(text = "no cycle notes logged yet. tap '+ log cycle' when ready.", fontFamily = FontFamily.Cursive, fontSize = 14.sp, color = theme.secondaryTextColor)
        } else {
            periodLogs.forEach { log ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFFCFAF4), RoundedCornerShape(4.dp))
                        .border(0.5.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(text = "${log.startDate} • flow: ${log.flow}", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = theme.textColor)
                        if (log.symptoms.isNotBlank()) {
                            Text(text = "symptoms: ${log.symptoms}", fontFamily = FontFamily.Cursive, fontSize = 13.sp, color = theme.secondaryTextColor)
                        }
                        if (log.notes.isNotBlank()) {
                            Text(text = log.notes, fontFamily = FontFamily.Default, fontSize = 12.sp, color = theme.textColor)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }

    if (showLogDialog) {
        var flow by remember { mutableStateOf("Medium") }
        var symptoms by remember { mutableStateOf("Cramps, cozy cravings") }
        var notes by remember { mutableStateOf("Lots of warm peppermint tea today.") }

        Dialog(onDismissRequest = { showLogDialog = false }) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .shadow(12.dp, RoundedCornerShape(10.dp))
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(10.dp))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "log cycle note 🌸", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = theme.textColor)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = symptoms, onValueChange = { symptoms = it }, label = { Text("Symptoms (gentle)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Care notes") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onLog("Today", flow, symptoms, notes)
                            showLogDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor)
                    ) {
                        Text("save quietly ♡", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }
}

// 5. LETTERS & STATIONERY
@Composable
fun LettersStationeryView(
    letters: List<LetterEntity>,
    onSave: (LetterEntity) -> Unit
) {
    val theme = LocalJournalTheme.current
    var showWriteLetter by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "stationery letters 💌", fontFamily = FontFamily.Cursive, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = theme.textColor)
            Button(
                onClick = { showWriteLetter = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+ write letter", fontSize = 11.sp, fontFamily = FontFamily.Serif)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 4.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(letters, key = { it.id }) { letter ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFFDF8), RoundedCornerShape(4.dp))
                        .border(0.8.dp, Color(0x33000000), RoundedCornerShape(4.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = letter.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = theme.textColor)
                            Text(text = letter.category, fontFamily = FontFamily.Cursive, fontSize = 12.sp, color = theme.coverAccentColor)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "to: ${letter.recipient}", fontFamily = FontFamily.Cursive, fontSize = 13.sp, color = theme.secondaryTextColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = letter.body, fontFamily = FontFamily.Default, fontSize = 13.sp, lineHeight = 19.sp, color = Color(0xFF2C2523))
                    }
                }
            }
        }
    }

    if (showWriteLetter) {
        var title by remember { mutableStateOf("") }
        var recipient by remember { mutableStateOf("") }
        var body by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("someone I love") }

        Dialog(onDismissRequest = { showWriteLetter = false }) {
            Box(
                modifier = Modifier
                    .width(310.dp)
                    .shadow(12.dp, RoundedCornerShape(10.dp))
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(10.dp))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "handwrite letter 💌", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = theme.textColor)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = recipient, onValueChange = { recipient = it }, label = { Text("To whom?") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Dear...") }, minLines = 4, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (body.isNotBlank()) {
                                onSave(LetterEntity(category = category, recipient = recipient.ifBlank { "someone" }, title = title.ifBlank { "a letter" }, body = body))
                                showWriteLetter = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor)
                    ) {
                        Text("tuck letter away ♡", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }
}

// 6. PIECES OF ME (Scrapbook Identity)
@Composable
fun PiecesOfMeView() {
    val theme = LocalJournalTheme.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WashiTape(width = 110.dp, rotation = -2f)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "pieces of me ✨", fontFamily = FontFamily.Cursive, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = theme.textColor)
        Spacer(modifier = Modifier.height(12.dp))

        listOf(
            "Comfort Movies:" to "Little Women, Howl's Moving Castle, Pride & Prejudice",
            "Favorite Songs of All Time:" to "Vienna - Billy Joel, Like Real People Do - Hozier, Bloom - The Paper Kites",
            "Dream Travel Spots:" to "Scottish Highlands in autumn, Kyoto tea garden, coastal Amalfi",
            "My Current Era:" to "The Soft Living & Slow Creative era ☕"
        ).forEach { (category, answer) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(Color(0xFFFCFAF4), RoundedCornerShape(6.dp))
                    .border(0.6.dp, Color(0x22000000), RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(text = category, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = theme.coverAccentColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = answer, fontFamily = FontFamily.Cursive, fontSize = 16.sp, color = theme.textColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}

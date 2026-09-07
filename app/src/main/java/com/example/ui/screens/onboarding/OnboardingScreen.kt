package com.example.ui.screens.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.AntiqueLockDoodle
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.HandwrittenArrowDoodle
import com.example.ui.components.HeartDoodle
import com.example.ui.components.HeartMaskedDot
import com.example.ui.components.JournalCoverView
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.TapedPaperScrap
import com.example.ui.components.WashiTape
import com.example.ui.theme.JournalThemes
import com.example.ui.theme.getThemeByName

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: (
        name: String,
        passcode: String,
        themeName: String,
        quote: String,
        handwritingStyle: String,
        handwritingSampleUri: String?,
        sections: String
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1 to 5

    // State collected across steps
    var name by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }
    var passcodeError by remember { mutableStateOf("") }

    var selectedTheme by remember { mutableStateOf(JournalThemes[0].name) }
    var coverQuote by remember { mutableStateOf("every little moment counts.") }

    var handwritingStyle by remember { mutableStateOf("Cursive") }
    var handwritingSampleUri by remember { mutableStateOf<Uri?>(null) }

    // Real Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            handwritingSampleUri = uri
        }
    }

    val allSections = listOf(
        "journal" to ("Everyday Journal" to "daily thoughts, feelings, & scrapbook entries"),
        "mood" to ("Mood Tracker" to "gentle emotion logs & color check-ins"),
        "doodle" to ("Doodle Book" to "freeform sketching & cute stickers"),
        "braindump" to ("Brain Dump Sticky Notes" to "tuck away chaotic quick notes"),
        "people" to ("People I Love Scrapbook" to "profiles of the humans who make life warm"),
        "memoryjar" to ("Memory Jar & Vault" to "folded little moments to look back on"),
        "futureme" to ("Future Me Time Capsules" to "letters sealed until a future date"),
        "wishlist" to ("Pinterest-style Wishlist" to "things, books, and dreams I crave"),
        "visionboard" to ("Vision Board Collage" to "aesthetic inspiration and visions"),
        "currently" to ("Currently Page" to "reading, listening, watching, craving"),
        "gratitude" to ("Daily Gratitude" to "3 little gifts from today"),
        "period" to ("Private Period Tracker" to "intimate, cycle-friendly notes"),
        "habits" to ("Habit Garden" to "nurture gentle daily intentions"),
        "letters" to ("Stationery Letters" to "unsent letters with wax seals"),
        "aboutme" to ("Pieces of Me" to "personal questionnaire & self-discovery")
    )
    val selectedSections = remember { mutableStateOf(allSections.map { it.first }.toSet()) }

    val currentThemeConfig = getThemeByName(selectedTheme)

    if (step == 5) {
        // Page 5: Dedicated screen with exact required structure:
        // Column / Screen
        //     Fixed header
        //     ↓
        //     LazyColumn WITH weight(1f)
        //     ↓
        //     Fixed bottom “open my corner ♡” button
        JournalPageSurface(
            pageNumber = 5,
            pattern = PaperPattern.RULED_LINES,
            hasLeftSpineMargin = true
        ) {
            val darkInk = Color(0xFF1E1C1A)
            val secondaryInk = Color(0xFF382E28)
            val accentInk = Color(0xFF5A3E2B)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(start = 28.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            ) {
                // FIXED HEADER
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WashiTape(
                            color = currentThemeConfig.washiColors.firstOrNull() ?: Color(0xFFE29578),
                            width = 110.dp,
                            height = 18.dp,
                            rotation = 2f
                        )

                        Text(
                            text = "page 5 of 5",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 15.sp,
                            color = currentThemeConfig.secondaryTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "what belongs here?",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = darkInk,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "choose what you'd like to begin with (all can be changed later).",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 16.sp,
                        color = secondaryInk,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick toggles: Select all / Count / Clear all
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { selectedSections.value = allSections.map { it.first }.toSet() }
                        ) {
                            Text(
                                text = "✦ Select all",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = accentInk
                            )
                        }

                        Text(
                            text = "${selectedSections.value.size} of ${allSections.size} chosen",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = secondaryInk
                        )

                        TextButton(
                            onClick = { selectedSections.value = emptySet() }
                        ) {
                            Text(
                                text = "Clear all ✕",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = Color(0xFF8A5A4A)
                            )
                        }
                    }

                    if (selectedSections.value.isEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Please select at least one section for your journal.",
                            color = Color(0xFFB71C1C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // SCROLLABLE LAZYCOLUMN WITH weight(1f)
                val romanNumerals = listOf(
                    "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV"
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("onboarding_sections_lazy_column"),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(allSections, key = { _, item -> item.first }) { index, (key, info) ->
                        val (title, description) = info
                        val isChecked = selectedSections.value.contains(key)
                        val roman = romanNumerals.getOrElse(index) { "${index + 1}" }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = if (isChecked) 2.5.dp else 0.8.dp,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isChecked) Color(0xFFFAF6EE) else Color(0xFFF2ECE2)
                                )
                                .border(
                                    width = if (isChecked) 1.5.dp else 1.dp,
                                    color = if (isChecked) Color(0xFF6B4834) else Color(0x35000000),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    val updated = selectedSections.value.toMutableSet()
                                    if (isChecked) updated.remove(key) else updated.add(key)
                                    selectedSections.value = updated
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        val updated = selectedSections.value.toMutableSet()
                                        if (checked) updated.add(key) else updated.remove(key)
                                        selectedSections.value = updated
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF5A4436),
                                        checkmarkColor = Color(0xFFFAF6EE),
                                        uncheckedColor = Color(0xFF6B584B)
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$roman.",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF8A6D56)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp,
                                        color = darkInk
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = description,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 11.5.sp,
                                        lineHeight = 15.sp,
                                        color = secondaryInk
                                    )
                                }
                            }
                        }
                    }
                }

                // FIXED BOTTOM “open my corner ♡” BUTTON
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { step = 4 },
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = currentThemeConfig.textColor
                        )
                    }

                    // 5 Page indicator dots
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (i in 1..5) {
                            Box(
                                modifier = Modifier
                                    .size(if (i == 5) 9.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i == 5) currentThemeConfig.coverColor
                                        else currentThemeConfig.secondaryTextColor.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (selectedSections.value.isNotEmpty()) {
                                onComplete(
                                    name.ifBlank { "Dreamer" },
                                    passcode,
                                    selectedTheme,
                                    coverQuote,
                                    handwritingStyle,
                                    handwritingSampleUri?.toString(),
                                    selectedSections.value.joinToString(",")
                                )
                            }
                        },
                        enabled = selectedSections.value.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentThemeConfig.coverColor,
                            disabledContainerColor = currentThemeConfig.coverColor.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("onboarding_finish_button")
                    ) {
                        Text(
                            text = "open my corner ♡",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    } else {
        JournalPageSurface(
            pageNumber = step,
            pattern = PaperPattern.RULED_LINES,
            hasLeftSpineMargin = true
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(start = 28.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            ) {
                // Header Tape & Page indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WashiTape(
                        color = currentThemeConfig.washiColors.firstOrNull() ?: Color(0xFFE29578),
                        width = 110.dp,
                        height = 18.dp,
                        rotation = if (step % 2 == 0) -2f else 2f
                    )

                    Text(
                        text = "page $step of 5",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 15.sp,
                        color = currentThemeConfig.secondaryTextColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Step Content Area (scrollable within available viewport)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = step,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                            } else {
                                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                            }
                        },
                        label = "onboardingAnim",
                        modifier = Modifier.fillMaxSize()
                    ) { currentStep ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (currentStep) {
                                1 -> {
                                // Page 1: Hello, you.
                                val darkInk = Color(0xFF1E1C1A)
                                val secondaryInk = Color(0xFF382E28)

                                // Taped scrap banner: "welcome to your cozy space ♡"
                                TapedPaperScrap(
                                    text = "welcome to your corner ♡",
                                    rotation = -2f,
                                    tapeColor = Color(0xFFE2A68C),
                                    bgColor = Color(0xFFFAF6EE),
                                    fontSize = 13f,
                                    textColor = darkInk
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                BotanicalDoodle(
                                    modifier = Modifier.size(38.dp, 48.dp),
                                    tint = Color(0xFF7A6553),
                                    flowerTint = Color(0xFFB57281)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "hello, you.",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = darkInk,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "what should this journal call you?",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 19.sp,
                                    color = secondaryInk,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    placeholder = {
                                        Text(
                                            "your name or nickname...",
                                            fontFamily = FontFamily.Cursive,
                                            fontSize = 16.sp,
                                            color = Color(0xFF8A7A6E)
                                        )
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = darkInk,
                                        unfocusedTextColor = darkInk,
                                        focusedBorderColor = Color(0xFF5A3E2B),
                                        unfocusedBorderColor = Color(0x55000000),
                                        cursorColor = Color(0xFF5A3E2B)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .testTag("onboarding_name_input")
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "“a place where you can write freely, without judgment or noise.”",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 16.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = secondaryInk,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            2 -> {
                                // Page 2: Keep it yours. Passcode (Mandatory 4–6 digits, masked, confirmed)
                                val darkInk = Color(0xFF1E1C1A)
                                val secondaryInk = Color(0xFF382E28)

                                // Antique padlock sketch at top
                                AntiqueLockDoodle(
                                    modifier = Modifier.size(34.dp, 40.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "keep it yours.",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = darkInk,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "some things are meant just for you.",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 18.sp,
                                    color = secondaryInk,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(18.dp))

                                Text(
                                    text = "Create a 4–6 digit journal passcode:",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = darkInk
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = passcode,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }.take(6)
                                        passcode = digits
                                        passcodeError = when {
                                            digits.length in 1..3 -> "Passcode must be at least 4 digits"
                                            confirmPasscode.isNotEmpty() && digits != confirmPasscode -> "Passcodes do not match"
                                            else -> ""
                                        }
                                    },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Next
                                    ),
                                    placeholder = {
                                        Text(
                                            "4–6 digits",
                                            color = Color(0xFF8A7A6E)
                                        )
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = darkInk,
                                        unfocusedTextColor = darkInk,
                                        focusedBorderColor = Color(0xFF5A3E2B),
                                        unfocusedBorderColor = Color(0x55000000),
                                        cursorColor = Color(0xFF5A3E2B)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .testTag("onboarding_passcode_input")
                                )

                                // Masked heart indicators for primary passcode
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (i in 0 until 6) {
                                        HeartMaskedDot(
                                            isFilled = passcode.length > i,
                                            filledColor = Color(0xFF24201E),
                                            unfilledColor = Color(0xFF9E8E82),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Confirm passcode:",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = darkInk
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = confirmPasscode,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }.take(6)
                                        confirmPasscode = digits
                                        passcodeError = when {
                                            digits.isNotEmpty() && digits != passcode -> "Passcodes do not match"
                                            passcode.length < 4 -> "Passcode must be at least 4 digits"
                                            else -> ""
                                        }
                                    },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Done
                                    ),
                                    placeholder = {
                                        Text(
                                            "re-enter 4–6 digits",
                                            color = Color(0xFF8A7A6E)
                                        )
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = darkInk,
                                        unfocusedTextColor = darkInk,
                                        focusedBorderColor = if (confirmPasscode.isNotEmpty() && confirmPasscode != passcode) Color(0xFFD32F2F) else Color(0xFF5A3E2B),
                                        unfocusedBorderColor = Color(0x55000000),
                                        cursorColor = Color(0xFF5A3E2B)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .testTag("onboarding_passcode_confirm")
                                )

                                // Masked heart indicators for confirm passcode
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (i in 0 until 6) {
                                        HeartMaskedDot(
                                            isFilled = confirmPasscode.length > i,
                                            filledColor = Color(0xFF24201E),
                                            unfilledColor = Color(0xFF9E8E82),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                if (passcodeError.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = passcodeError,
                                        color = Color(0xFFD32F2F),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                } else if (passcode.length in 4..6 && passcode == confirmPasscode) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✓ Passcodes match!",
                                        color = Color(0xFF2E7D32),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Quick On-Screen Number Keypad
                                Text(
                                    text = "Quick Keypad",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 13.sp,
                                    color = currentThemeConfig.secondaryTextColor
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val numpadRows = listOf(
                                    listOf("1", "2", "3"),
                                    listOf("4", "5", "6"),
                                    listOf("7", "8", "9"),
                                    listOf("clear", "0", "del")
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    numpadRows.forEach { row ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            row.forEach { key ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 54.dp, height = 36.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFFEDE5D8))
                                                        .border(1.dp, Color(0x33000000), RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            when (key) {
                                                                "clear" -> {
                                                                    if (confirmPasscode.isNotEmpty()) {
                                                                        confirmPasscode = ""
                                                                    } else {
                                                                        passcode = ""
                                                                    }
                                                                    passcodeError = ""
                                                                }
                                                                "del" -> {
                                                                    if (confirmPasscode.isNotEmpty()) {
                                                                        confirmPasscode = confirmPasscode.dropLast(1)
                                                                    } else if (passcode.isNotEmpty()) {
                                                                        passcode = passcode.dropLast(1)
                                                                    }
                                                                    passcodeError = ""
                                                                }
                                                                else -> {
                                                                    if (passcode.length < 4 || (passcode.length < 6 && confirmPasscode.isEmpty())) {
                                                                        passcode += key
                                                                    } else if (confirmPasscode.length < 6) {
                                                                        confirmPasscode += key
                                                                    }
                                                                    if (confirmPasscode.isNotEmpty() && confirmPasscode != passcode) {
                                                                        passcodeError = "Passcodes do not match"
                                                                    } else {
                                                                        passcodeError = ""
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        .testTag("onboarding_numpad_$key"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = key,
                                                        fontFamily = FontFamily.Serif,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = if (key.length > 1) 11.sp else 16.sp,
                                                        color = Color(0xFF2C2523)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Stored securely on-device. Required to open your journal.",
                                    fontFamily = FontFamily.Default,
                                    fontSize = 11.sp,
                                    color = currentThemeConfig.secondaryTextColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            3 -> {
                                // Page 3: Pick your journal cover
                                val darkInk = Color(0xFF1E1C1A)
                                val secondaryInk = Color(0xFF382E28)

                                Text(
                                    text = "pick your journal.",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = darkInk,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "how should your book feel in your hands?",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 18.sp,
                                    color = secondaryInk,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                // Preview Journal Cover
                                JournalCoverView(
                                    theme = currentThemeConfig,
                                    userName = name.ifBlank { "Dreamer" },
                                    quote = coverQuote,
                                    modifier = Modifier.size(width = 220.dp, height = 270.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Choose a journal theme:",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = darkInk
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    JournalThemes.forEach { themeItem ->
                                        val isSelected = selectedTheme == themeItem.name
                                        Box(
                                            modifier = Modifier
                                                .padding(3.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) themeItem.coverColor else themeItem.paperColor)
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) themeItem.coverAccentColor else Color(0x35000000),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedTheme = themeItem.name }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = themeItem.name,
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 12.sp,
                                                color = if (isSelected) Color.White else darkInk
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = coverQuote,
                                    onValueChange = { coverQuote = it },
                                    label = { Text("Cover quote / inscription", color = Color(0xFF5A3E2B)) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = darkInk,
                                        unfocusedTextColor = darkInk,
                                        focusedBorderColor = Color(0xFF5A3E2B),
                                        unfocusedBorderColor = Color(0x55000000),
                                        cursorColor = Color(0xFF5A3E2B)
                                    ),
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                )
                            }

                            4 -> {
                                // Page 4: Leave your mark (Handwriting with real image picker)
                                val darkInk = Color(0xFF1E1C1A)
                                val secondaryInk = Color(0xFF382E28)

                                Text(
                                    text = "leave your mark.",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = darkInk,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "handwriting brings pages to life.",
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 18.sp,
                                    color = secondaryInk,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Select your handwriting fallback style:",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = darkInk
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                listOf("Cursive", "Script", "Typewriter", "Vintage Serif").forEach { style ->
                                    val isSelected = handwritingStyle == style
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(0.92f)
                                            .padding(vertical = 3.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFFFAF6EE) else Color(0xFFF2ECE2))
                                            .border(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) Color(0xFF6B4834) else Color(0x30000000),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { handwritingStyle = style }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = style,
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = darkInk
                                            )
                                            Text(
                                                text = "“with love, from my little corner”",
                                                fontFamily = when (style.lowercase()) {
                                                    "cursive" -> FontFamily.Cursive
                                                    "script" -> FontFamily.Cursive
                                                    "typewriter" -> FontFamily.Monospace
                                                    else -> FontFamily.Serif
                                                },
                                                fontSize = 13.5.sp,
                                                color = secondaryInk
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color(0xFF5A3E2B)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Real handwriting upload section
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.92f)
                                        .shadow(2.dp, RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFAF6EE), RoundedCornerShape(10.dp))
                                        .border(1.dp, Color(0x35000000), RoundedCornerShape(10.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = null,
                                                tint = Color(0xFF5A3E2B),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Personal Handwriting Keepsake (Optional)",
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = darkInk
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Explicit guidelines
                                        Text(
                                            text = "For best results, upload a clear photo of handwriting on unlined paper containing:\n" +
                                                    " • A–Z (uppercase letters)\n" +
                                                    " • a–z (lowercase letters)\n" +
                                                    " • 0–9 (numbers)\n" +
                                                    " • Common punctuation (, . ! ? ' -)",
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Serif,
                                            lineHeight = 16.sp,
                                            color = currentThemeConfig.textColor.copy(alpha = 0.85f)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Your sample is preserved as an image keepsake in your journal vault. Journal pages use the selected handwriting font style above.",
                                            fontSize = 11.sp,
                                            fontStyle = FontStyle.Italic,
                                            lineHeight = 15.sp,
                                            color = currentThemeConfig.secondaryTextColor
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        if (handwritingSampleUri != null) {
                                            // Only displayed AFTER a real image has been selected
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.White)
                                                    .border(1.dp, Color(0x22000000), RoundedCornerShape(8.dp))
                                                    .padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "✓ Sample attached",
                                                        fontFamily = FontFamily.Serif,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 13.sp,
                                                        color = Color(0xFF2E7D32)
                                                    )
                                                    TextButton(
                                                        onClick = { handwritingSampleUri = null },
                                                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Delete,
                                                            contentDescription = "Remove",
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Remove Sample", fontSize = 12.sp)
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(6.dp))

                                                AsyncImage(
                                                    model = handwritingSampleUri,
                                                    contentDescription = "Selected handwriting sample",
                                                    modifier = Modifier
                                                        .size(width = 200.dp, height = 120.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .border(1.dp, Color(0x33000000), RoundedCornerShape(6.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    photoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = currentThemeConfig.coverColor),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("onboarding_attach_handwriting_button")
                                            ) {
                                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Attach Handwriting Sample",
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamily.Serif
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

            // Pinned Bottom Navigation Footer (ALWAYS REACHABLE & NEVER PUSHED OFF-SCREEN)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        IconButton(
                            onClick = {
                                passcodeError = ""
                                step--
                            },
                            modifier = Modifier.testTag("onboarding_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = currentThemeConfig.textColor
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    // 5 Page indicator dots
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (i in 1..5) {
                            Box(
                                modifier = Modifier
                                    .size(if (step == i) 9.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (step == i) currentThemeConfig.coverColor
                                        else currentThemeConfig.secondaryTextColor.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }

                    val isStep5 = step == 5
                    val canProceed = when (step) {
                        1 -> name.isNotBlank()
                        2 -> passcode.length in 4..6 && passcode == confirmPasscode
                        3 -> true
                        4 -> true
                        5 -> selectedSections.value.isNotEmpty()
                        else -> true
                    }

                    Button(
                        onClick = {
                            when (step) {
                                1 -> {
                                    if (name.isBlank()) name = "Dreamer"
                                    step++
                                }
                                2 -> {
                                    if (passcode.length < 4) {
                                        passcodeError = "Passcode must be 4–6 digits"
                                        return@Button
                                    }
                                    if (confirmPasscode.isEmpty()) {
                                        passcodeError = "Please confirm your passcode"
                                        return@Button
                                    }
                                    if (passcode != confirmPasscode) {
                                        passcodeError = "Passcodes do not match"
                                        return@Button
                                    }
                                    passcodeError = ""
                                    step++
                                }
                                3 -> step++
                                4 -> step++
                                5 -> {
                                    if (selectedSections.value.isEmpty()) return@Button
                                    onComplete(
                                        name.ifBlank { "Dreamer" },
                                        passcode,
                                        selectedTheme,
                                        coverQuote,
                                        handwritingStyle,
                                        handwritingSampleUri?.toString(),
                                        selectedSections.value.joinToString(",")
                                    )
                                }
                            }
                        },
                        enabled = canProceed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentThemeConfig.coverColor,
                            disabledContainerColor = currentThemeConfig.coverColor.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag(if (isStep5) "onboarding_finish_button" else "onboarding_next_button")
                    ) {
                        Text(
                            text = if (isStep5) "open my corner ♡" else "next",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = if (isStep5) 15.sp else 14.sp,
                            color = Color.White
                        )
                        if (!isStep5) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White,
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

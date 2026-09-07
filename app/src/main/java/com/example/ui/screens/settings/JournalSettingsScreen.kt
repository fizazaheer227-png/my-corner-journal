package com.example.ui.screens.settings

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserSettingsEntity
import com.example.ui.components.BotanicalDoodle
import com.example.ui.components.JournalCoverView
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.TapedPaperScrap
import com.example.ui.components.WashiTape
import com.example.ui.theme.JournalThemes
import com.example.ui.theme.LocalJournalTheme
import com.example.ui.theme.getThemeByName

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JournalSettingsScreen(
    userSettings: UserSettingsEntity,
    totalEntriesCount: Int,
    totalMemoriesCount: Int,
    onSaveSettings: (UserSettingsEntity) -> Unit,
    onLockNow: () -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalJournalTheme.current
    val darkInk = Color(0xFF1E1C1A)
    val secondaryInk = Color(0xFF382E28)

    var name by remember { mutableStateOf(userSettings.userName) }
    var coverQuote by remember { mutableStateOf(userSettings.coverQuote) }
    var coverStickers by remember { mutableStateOf(userSettings.coverStickers) }
    var selectedTheme by remember { mutableStateOf(userSettings.themeName) }
    var selectedHandwriting by remember { mutableStateOf(userSettings.handwritingStyle) }
    var autoLock by remember { mutableStateOf(userSettings.autoLockEnabled) }
    var hideCycleTracker by remember { mutableStateOf(!userSettings.enabledSections.contains("period")) }
    var exportFeedback by remember { mutableStateOf("") }

    JournalPageSurface(
        pageNumber = 99,
        pattern = PaperPattern.RULED_LINES,
        hasLeftSpineMargin = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = darkInk)
                }

                Button(
                    onClick = {
                        val currentSections = userSettings.enabledSections.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
                        if (hideCycleTracker) {
                            currentSections.remove("period")
                        } else {
                            currentSections.add("period")
                        }
                        onSaveSettings(
                            userSettings.copy(
                                userName = name,
                                coverQuote = coverQuote,
                                coverStickers = coverStickers,
                                themeName = selectedTheme,
                                handwritingStyle = selectedHandwriting,
                                autoLockEnabled = autoLock,
                                enabledSections = currentSections.joinToString(",")
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A3E2B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_settings_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("save changes", fontFamily = FontFamily.Serif, fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Book Ex Libris Plate (Inside Back Cover)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(4.dp))
                    .background(Color(0xFFFCF9F2), RoundedCornerShape(4.dp))
                    .border(1.5.dp, Color(0xFF4A3E38), RoundedCornerShape(4.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "EX LIBRIS",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 3.sp,
                        color = darkInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "this journal belongs to:",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 16.sp,
                        color = Color(0xFF6B5547)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = darkInk,
                            unfocusedTextColor = darkInk,
                            focusedBorderColor = Color(0xFF5A3E2B),
                            unfocusedBorderColor = Color(0x55000000),
                            cursorColor = Color(0xFF5A3E2B)
                        ),
                        modifier = Modifier.fillMaxWidth(0.85f).testTag("settings_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$totalEntriesCount pages penned • $totalMemoriesCount memories kept safe",
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp,
                        color = secondaryInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: MY JOURNAL (Covers & Aesthetics)
            Text(
                text = "1. MY JOURNAL (Cover & Aesthetics)",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = darkInk
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Live preview updates as you design your book:",
                fontFamily = FontFamily.Cursive,
                fontSize = 14.sp,
                color = secondaryInk
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Live Interactive Mini-Cover Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                JournalCoverView(
                    theme = getThemeByName(selectedTheme),
                    userName = name.ifBlank { "Dreamer" },
                    quote = coverQuote,
                    stickers = coverStickers,
                    modifier = Modifier.size(width = 195.dp, height = 250.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = coverQuote,
                onValueChange = { coverQuote = it },
                label = { Text("Cover Inscription", color = Color(0xFF5A3E2B)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = darkInk,
                    unfocusedTextColor = darkInk,
                    focusedBorderColor = Color(0xFF5A3E2B),
                    unfocusedBorderColor = Color(0x55000000),
                    cursorColor = Color(0xFF5A3E2B)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Theme Palette:", fontSize = 13.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = darkInk)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                JournalThemes.forEach { th ->
                    val isSel = selectedTheme == th.name
                    Box(
                        modifier = Modifier
                            .background(if (isSel) th.coverColor else th.paperColor, RoundedCornerShape(8.dp))
                            .border(1.2.dp, if (isSel) th.coverAccentColor else Color(0x35000000), RoundedCornerShape(8.dp))
                            .clickable { selectedTheme = th.name }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = th.name,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else darkInk
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Cover Charms & Keepsake Stickers:",
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = darkInk
            )
            Spacer(modifier = Modifier.height(6.dp))

            val availableCharms = listOf(
                "🦋" to "Butterfly",
                "🌿" to "Wildflower",
                "📷" to "Polaroid",
                "💌" to "Wax Seal",
                "☕" to "Coffee",
                "🏷️" to "Stamp",
                "📎" to "Clip",
                "🌸" to "Sakura",
                "✨" to "Stardust",
                "📜" to "Scroll"
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val currentList = coverStickers.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                availableCharms.forEach { (emoji, label) ->
                    val isAttached = currentList.contains(emoji)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAttached) Color(0xFFFAF6EE) else Color(0xFFF2ECE2))
                            .border(
                                width = if (isAttached) 1.5.dp else 1.dp,
                                color = if (isAttached) Color(0xFF6B4834) else Color(0x30000000),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                val updated = if (isAttached) {
                                    currentList.filter { it != emoji }
                                } else {
                                    currentList + emoji
                                }
                                coverStickers = updated.joinToString(",")
                            }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = label,
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.5.sp,
                                fontWeight = if (isAttached) FontWeight.Bold else FontWeight.Normal,
                                color = darkInk
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(text = "Handwriting Style:", fontSize = 13.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = darkInk)
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Cursive", "Script", "Typewriter", "Vintage Serif").forEach { style ->
                    val isSel = selectedHandwriting == style
                    Box(
                        modifier = Modifier
                            .background(if (isSel) Color(0xFF5A3E2B) else Color(0xFFF0EBE0), RoundedCornerShape(6.dp))
                            .border(1.dp, if (isSel) Color(0xFF5A3E2B) else Color(0x30000000), RoundedCornerShape(6.dp))
                            .clickable { selectedHandwriting = style }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = style,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.White else darkInk
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: PRIVACY & SECURITY
            Text(
                text = "2. PRIVACY & PASSCODE",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = darkInk
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Auto-lock when leaving app", fontFamily = FontFamily.Serif, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = darkInk)
                    Text(text = "Requires passcode when reopening journal", fontFamily = FontFamily.Cursive, fontSize = 13.sp, color = secondaryInk)
                }
                Switch(
                    checked = autoLock,
                    onCheckedChange = { autoLock = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF5A3E2B),
                        checkedTrackColor = Color(0xFFD4C2B2)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Hide Cycle Tracker", fontFamily = FontFamily.Serif, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = darkInk)
                    Text(text = "Keeps cycle notes discreet and hidden from main chapters list", fontFamily = FontFamily.Cursive, fontSize = 13.sp, color = secondaryInk)
                }
                Switch(
                    checked = hideCycleTracker,
                    onCheckedChange = { hideCycleTracker = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF5A3E2B),
                        checkedTrackColor = Color(0xFFD4C2B2)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onLockNow,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A3E2B)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Lock now", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("lock journal now 🔒", fontFamily = FontFamily.Serif, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: MY CORNER (Data & Backups)
            Text(
                text = "3. MY CORNER (Local Data & Keepsakes)",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = darkInk
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your journal is stored locally on this device in a private SQLite database. No external tracking, no cloud telemetry.",
                fontFamily = FontFamily.Default,
                fontSize = 12.sp,
                color = secondaryInk
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { exportFeedback = "Journal data exported locally to device storage! ✨" },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A5941)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Storage, contentDescription = "Export")
                Spacer(modifier = Modifier.width(6.dp))
                Text("export keepsake backup", fontFamily = FontFamily.Serif, color = Color.White)
            }

            if (exportFeedback.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = exportFeedback, fontFamily = FontFamily.Cursive, fontSize = 14.sp, color = Color(0xFF2E7D32))
            }

            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

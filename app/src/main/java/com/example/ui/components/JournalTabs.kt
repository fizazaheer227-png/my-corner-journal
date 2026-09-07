package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalJournalTheme

enum class JournalTab(val title: String, val iconEmoji: String) {
    TODAY("today", "☀️"),
    PAGES("pages", "📖"),
    MOOD("mood", "🌸"),
    PEOPLE("people", "👥"),
    MEMORIES("memories", "🎞️"),
    DREAMS("dreams", "✨"),
    ME("me", "🌿")
}

/**
 * Physical Journal Tabs protruding like bookmark dividers
 */
@Composable
fun JournalTabBar(
    currentTab: JournalTab,
    onTabSelected: (JournalTab) -> Unit,
    onSearchClick: () -> Unit,
    onIndexClick: () -> Unit,
    onLockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalJournalTheme.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(theme.paperColor)
    ) {
        // Top Toolbar: Bookmark Ribbon, Search, Lock & Index
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bookmark Ribbon (clickable to open search or index)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onIndexClick() }
            ) {
                JournalBookmarkRibbon(
                    color = theme.ribbonColor,
                    length = 34.dp,
                    onClick = onIndexClick
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "My Corner",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = theme.textColor
                    )
                    Text(
                        text = "open index / jump",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 12.sp,
                        color = theme.secondaryTextColor
                    )
                }
            }

            // Quick actions: Search, Lock
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("search_ribbon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search journal",
                        tint = theme.textColor
                    )
                }
                IconButton(
                    onClick = onLockClick,
                    modifier = Modifier.testTag("manual_lock_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock journal",
                        tint = theme.secondaryTextColor
                    )
                }
            }
        }

        // Horizontal Physical Bookmark Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            JournalTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val elevation by animateDpAsState(targetValue = if (isSelected) 6.dp else 2.dp, label = "tabElev")
                val offsetY by animateDpAsState(targetValue = if (isSelected) (-2).dp else 2.dp, label = "tabOffset")

                val tabColor = when (tab) {
                    JournalTab.TODAY -> Color(0xFFFF8A65)
                    JournalTab.PAGES -> Color(0xFFBA68C8)
                    JournalTab.MOOD -> Color(0xFF81C784)
                    JournalTab.PEOPLE -> Color(0xFF7986CB)
                    JournalTab.MEMORIES -> Color(0xFFFFA726)
                    JournalTab.DREAMS -> Color(0xFF4DB6AC)
                    JournalTab.ME -> Color(0xFFD4E157)
                }

                // Bookmark Tab Shape
                Box(
                    modifier = Modifier
                        .offset(y = offsetY)
                        .shadow(elevation, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                        .background(
                            color = if (isSelected) tabColor else tabColor.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) Color(0xFF2D2926) else Color(0x33000000),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tab.iconEmoji,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = tab.title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isSelected) Color(0xFF2D2926) else Color(0xFF5D4037)
                        )
                    }
                }
            }
        }

        // Journal Page Edge separator (subtle deckle / divider)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(theme.coverColor.copy(alpha = 0.15f))
        )
    }
}

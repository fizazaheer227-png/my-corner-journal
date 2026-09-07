package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalJournalTheme

enum class CreationOption(val title: String, val emoji: String, val rotation: Float, val color: Color) {
    TEXT("write page", "✍️", -3f, Color(0xFFFFF8E7)),
    PHOTO("add polaroid", "📷", 2.5f, Color(0xFFFCFAF5)),
    DOODLE("draw something", "🎨", -2f, Color(0xFFF3E5F5)),
    STICKER("add sticker", "🌸", 4f, Color(0xFFFFF0F5)),
    SONG("music reference", "🎵", -3.5f, Color(0xFFE8F5E9)),
    VOICE("whisper / note", "🎙️", 2f, Color(0xFFE0F7FA))
}

/**
 * Universal Creation Button with unfolding paper scraps
 */
@Composable
fun UniversalCreationScraps(
    onOptionSelected: (CreationOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalJournalTheme.current
    var isExpanded by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fabRot"
    )

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Unfolded Paper Scraps
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + scaleIn(initialScale = 0.6f),
            exit = fadeOut() + scaleOut(targetScale = 0.6f)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                CreationOption.values().forEach { option ->
                    PaperScrapButton(
                        option = option,
                        onClick = {
                            isExpanded = false
                            onOptionSelected(option)
                        }
                    )
                }
            }
        }

        // The Main "+ add something" unfolded button (Vibrant Palette Pill)
        Box(
            modifier = Modifier
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(50))
                .background(Color(0xFF2D2926), shape = RoundedCornerShape(50))
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .testTag("universal_creation_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Add something",
                    tint = Color.White,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(fabRotation)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isExpanded) "CLOSE" else "ADD SOMETHING",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PaperScrapButton(
    option: CreationOption,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .rotate(option.rotation)
            .shadow(4.dp, shape = RoundedCornerShape(4.dp))
            .background(option.color, shape = RoundedCornerShape(4.dp))
            .border(0.8.dp, Color(0x33000000), shape = RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("scrap_${option.name.lowercase()}"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = option.emoji,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = option.title,
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C2523)
            )
        }
    }
}

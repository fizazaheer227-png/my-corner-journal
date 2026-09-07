package com.example.ui.screens.people

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.io.File
import com.example.data.local.entity.PersonEntity
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.PolaroidCard
import com.example.ui.components.WashiTape
import com.example.ui.theme.LocalJournalTheme

@Composable
fun PeopleScrapbookScreen(
    people: List<PersonEntity>,
    onSelectPerson: (Long) -> Unit,
    onAddPerson: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val theme = LocalJournalTheme.current

    Box(modifier = Modifier.fillMaxSize()) {
        JournalPageSurface(
            pageNumber = 7,
            pattern = PaperPattern.RULED_LINES,
            hasLeftSpineMargin = true
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp, end = 16.dp, top = 14.dp, bottom = 0.dp)
            ) {
                // Headline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBack != null) {
                            IconButton(onClick = onBack, modifier = Modifier.testTag("people_back_button")) {
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
                                text = "the people who make my world softer ♡",
                                fontFamily = FontFamily.Cursive,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = theme.textColor
                            )
                            Text(
                                text = "polaroids, inside jokes & little details",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                color = theme.secondaryTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (people.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "no polaroids yet.\nadd someone important to your world ♡",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = theme.secondaryTextColor
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 110.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(people, key = { it.id }) { person ->
                            val rot = if (person.id % 2L == 0L) -2.5f else 2.5f

                            PolaroidCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectPerson(person.id) }
                                    .testTag("person_polaroid_${person.id}"),
                                rotation = rot,
                                caption = person.nickname.ifBlank { person.name },
                                dateText = if (person.insideJokes.isNotBlank()) "“${person.insideJokes.take(24)}...”" else "",
                                hasPaperClip = person.id % 2L == 0L,
                                hasWashiTape = person.id % 2L != 0L
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .background(Color(0xFFE8E2D5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (person.photoUri.isNotBlank()) {
                                        val photoModel = remember(person.photoUri) {
                                            if (person.photoUri.startsWith("content://")) person.photoUri else File(person.photoUri)
                                        }
                                        SubcomposeAsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(photoModel)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = person.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = person.name.firstOrNull()?.uppercase() ?: "♡",
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 32.sp,
                                                color = Color(0xFF6B5547)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = person.name,
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 12.sp,
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

        // Add Person FAB
        FloatingActionButton(
            onClick = onAddPerson,
            containerColor = theme.coverColor,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 86.dp, end = 16.dp)
                .testTag("add_person_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Person")
        }
    }
}

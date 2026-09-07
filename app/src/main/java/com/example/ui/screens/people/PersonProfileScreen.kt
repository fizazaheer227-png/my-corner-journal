package com.example.ui.screens.people

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.local.entity.PersonEntity
import com.example.media.MediaStorageHelper
import com.example.ui.components.JournalPageSurface
import com.example.ui.components.PaperPattern
import com.example.ui.components.PolaroidCard
import com.example.ui.components.WashiTape
import com.example.ui.theme.LocalJournalTheme
import java.io.File

@Composable
fun PersonProfileScreen(
    person: PersonEntity?,
    onSavePerson: (PersonEntity) -> Unit,
    onDeletePerson: ((Long) -> Unit)?,
    onBack: () -> Unit
) {
    val theme = LocalJournalTheme.current
    val context = LocalContext.current

    var name by remember { mutableStateOf(person?.name ?: "") }
    var nickname by remember { mutableStateOf(person?.nickname ?: "") }
    var photoUri by remember { mutableStateOf(person?.photoUri ?: "") }
    var howWeMet by remember { mutableStateOf(person?.howWeMet ?: "") }
    var favoriteMemories by remember { mutableStateOf(person?.favoriteMemories ?: "") }
    var insideJokes by remember { mutableStateOf(person?.insideJokes ?: "") }
    var thingsNeverForget by remember { mutableStateOf(person?.thingsNeverForget ?: "") }
    var coffeeOrder by remember { mutableStateOf(person?.coffeeOrder ?: "") }
    var favoriteSnack by remember { mutableStateOf(person?.favoriteSnack ?: "") }
    var favoriteFlower by remember { mutableStateOf(person?.favoriteFlower ?: "") }
    var giftIdeas by remember { mutableStateOf(person?.giftIdeas ?: "") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val saved = MediaStorageHelper.copyUriToInternalStorage(
                context = context,
                uri = uri,
                subDir = "people_photos",
                prefix = "person",
                extension = "jpg"
            )
            if (saved != null) {
                photoUri = saved
            }
        }
    }

    JournalPageSurface(
        pageNumber = 8,
        pattern = PaperPattern.RULED_LINES,
        hasLeftSpineMargin = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = theme.textColor)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (person != null && onDeletePerson != null) {
                        IconButton(onClick = { onDeletePerson(person.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = theme.secondaryTextColor)
                        }
                    }

                    Button(
                        onClick = {
                            val entity = PersonEntity(
                                id = person?.id ?: 0,
                                name = name.ifBlank { "Someone special" },
                                nickname = nickname,
                                photoUri = photoUri,
                                howWeMet = howWeMet,
                                favoriteMemories = favoriteMemories,
                                insideJokes = insideJokes,
                                thingsNeverForget = thingsNeverForget,
                                coffeeOrder = coffeeOrder,
                                favoriteSnack = favoriteSnack,
                                favoriteFlower = favoriteFlower,
                                giftIdeas = giftIdeas
                            )
                            onSavePerson(entity)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.coverColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_person_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("keep safe ♡", fontFamily = FontFamily.Serif, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Polaroid Snapshot at top of profile - Tapping opens Photo Picker!
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PolaroidCard(
                    rotation = 1.8f,
                    caption = nickname.ifBlank { name.ifBlank { "someone special" } },
                    hasPaperClip = true,
                    modifier = Modifier
                        .width(220.dp)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("person_profile_polaroid")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(Color(0xFFE8E0D2)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri.isNotBlank()) {
                            val photoModel = remember(photoUri) {
                                if (photoUri.startsWith("content://")) photoUri else File(photoUri)
                            }
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(photoModel)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = name.firstOrNull()?.uppercase() ?: "♡",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = Color(0xFF6B5547)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Add Photo",
                                        tint = Color(0xFF8A7E75),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "tap to add photo",
                                        fontFamily = FontFamily.Cursive,
                                        fontSize = 12.sp,
                                        color = Color(0xFF8A7E75)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Basic Info
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Their name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("person_name_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname / pet name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section: How We Met
            Text(
                text = "how our stories crossed 🌿",
                fontFamily = FontFamily.Cursive,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = howWeMet,
                onValueChange = { howWeMet = it },
                placeholder = { Text("where were we, what happened, how did it feel?") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section: Things I Never Want to Forget About You
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(6.dp))
                    .background(Color(0xFFFFF9EE), RoundedCornerShape(6.dp))
                    .border(1.dp, Color(0x33E29578), RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "things I never want to forget about you ♡",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5C4033)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = thingsNeverForget,
                        onValueChange = { thingsNeverForget = it },
                        placeholder = { Text("the way you laugh, the things you do when no one is watching...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section: Inside Jokes & Memories
            Text(
                text = "inside jokes & moments that only we understand",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = theme.textColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = insideJokes,
                onValueChange = { insideJokes = it },
                placeholder = { Text("flying blueberries, midnight whispers, secret codes...") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section: The Little Things
            Text(
                text = "the little things (to remember & cherish) ☕",
                fontFamily = FontFamily.Cursive,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = coffeeOrder,
                onValueChange = { coffeeOrder = it },
                label = { Text("Coffee / Tea order") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = favoriteSnack,
                onValueChange = { favoriteSnack = it },
                label = { Text("Favorite snack / treat") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = favoriteFlower,
                onValueChange = { favoriteFlower = it },
                label = { Text("Favorite flower or scent") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = giftIdeas,
                onValueChange = { giftIdeas = it },
                label = { Text("Secret gift ideas / wishlist for them") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.io.File

@Composable
fun ScrapbookPhotoCard(
    imagePathOrUri: String,
    caption: String = "",
    rotation: Float = -2f,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFullPreview by remember { mutableStateOf(false) }

    val imageModel = remember(imagePathOrUri) {
        if (imagePathOrUri.startsWith("content://") || imagePathOrUri.startsWith("http")) {
            imagePathOrUri
        } else {
            File(imagePathOrUri)
        }
    }

    Box(
        modifier = modifier
            .rotate(rotation)
            .shadow(6.dp, RoundedCornerShape(2.dp))
            .background(Color(0xFFFFFFFF), RoundedCornerShape(2.dp))
            .border(0.5.dp, Color(0x33000000), RoundedCornerShape(2.dp))
            .padding(8.dp)
            .testTag("scrapbook_photo_card")
    ) {
        // Aesthetic washi tape pinned at top
        ScrapbookWashiTape(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-13).dp),
            color = Color(0xFFE2A68C),
            width = 46.dp,
            height = 12.dp,
            rotation = -rotation
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFEDE5D8))
                    .clickable { showFullPreview = true },
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Journal Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF8A7E75),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(contentAlignment = Alignment.Center) {
                            Text("📷", fontSize = 28.sp)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = caption.ifBlank { "tucked snapshot ♡" },
                    fontFamily = FontFamily.Cursive,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2523),
                    modifier = Modifier.weight(1f)
                )

                if (onRemove != null) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove photo",
                            tint = Color(0xFF8A7E75),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }

    if (showFullPreview) {
        Dialog(onDismissRequest = { showFullPreview = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAF7F0))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = caption.ifBlank { "photo snapshot ♡" },
                            fontFamily = FontFamily.Cursive,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1D1B)
                        )
                        IconButton(onClick = { showFullPreview = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF1E1D1B))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageModel)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Full photo preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

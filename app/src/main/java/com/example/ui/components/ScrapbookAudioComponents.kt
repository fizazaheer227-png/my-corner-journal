package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.media.AudioPlaybackManager
import com.example.media.AudioRecordingManager
import kotlinx.coroutines.delay

@Composable
fun ScrapbookVoicePlayerCard(
    audioPath: String,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val player = remember { AudioPlaybackManager() }
    val isPlaying by player.isPlaying.collectAsState()
    val currentPos by player.currentPosition.collectAsState()
    val duration by player.duration.collectAsState()

    DisposableEffect(audioPath) {
        onDispose {
            player.release()
        }
    }

    val progress = if (duration > 0) (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f

    val totalSeconds = (if (duration > 0) duration else 0) / 1000
    val currentSeconds = currentPos / 1000
    val timeLabel = "${currentSeconds / 60}:${"%02d".format(currentSeconds % 60)} / ${totalSeconds / 60}:${"%02d".format(totalSeconds % 60)}"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .rotate(-0.8f)
            .shadow(3.dp, RoundedCornerShape(10.dp))
            .background(Color(0xFFF7F2EA), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0x335A4D41), RoundedCornerShape(10.dp))
            .padding(12.dp)
            .testTag("scrapbook_voice_player")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Cassette/Tape icon + Play button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF2C2523), CircleShape)
                        .clickable {
                            if (isPlaying) {
                                player.pause()
                            } else {
                                player.play(audioPath)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFFFAF7F0),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "voice memory 🎙️",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF2C2523)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "cassette tape",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 11.sp,
                            color = Color(0xFF8A7E75)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .width(140.dp)
                            .height(5.dp),
                        color = Color(0xFFB5704D),
                        trackColor = Color(0xFFE2D9CC)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = timeLabel,
                        fontFamily = FontFamily.Serif,
                        fontSize = 10.sp,
                        color = Color(0xFF6B5848)
                    )
                }
            }

            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete recording",
                        tint = Color(0xFF8A7E75),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScrapbookVoiceRecorderModal(
    onRecordingComplete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val recorder = remember { AudioRecordingManager() }
    var isRecording by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            permissionDeniedMessage = "Microphone permission is needed to record voice notes in your journal."
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            secondsElapsed = 0
            while (isRecording) {
                delay(1000)
                secondsElapsed++
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (recorder.isRecording) {
                recorder.cancelRecording()
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .background(Color(0xFFFAF7F0), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x33000000), RoundedCornerShape(16.dp))
            .padding(18.dp)
            .testTag("voice_recorder_modal")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "record voice note 🎙️",
                fontFamily = FontFamily.Cursive,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1D1B)
            )

            Text(
                text = "whisper your thoughts into the page",
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                color = Color(0xFF7A6B5F)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasPermission) {
                Text(
                    text = permissionDeniedMessage ?: "Please grant microphone access to record voice notes.",
                    fontSize = 12.sp,
                    color = Color(0xFF8B4513),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(onClick = onDismiss) {
                        Text("cancel", fontFamily = FontFamily.Serif, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2C2523), RoundedCornerShape(12.dp))
                            .clickable { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "allow microphone",
                            fontFamily = FontFamily.Serif,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Recording status & elapsed time
                if (isRecording) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .alpha(pulseAlpha)
                                .background(Color(0xFFD9534F), CircleShape)
                        )
                        Text(
                            text = "recording... ${secondsElapsed / 60}:${"%02d".format(secondsElapsed % 60)}",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFD9534F)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Cancel
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8E0D2), RoundedCornerShape(14.dp))
                                .clickable {
                                    recorder.cancelRecording()
                                    isRecording = false
                                    onDismiss()
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text("cancel", fontFamily = FontFamily.Serif, fontSize = 13.sp, color = Color(0xFF4A3B32))
                        }

                        // Stop & Save
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2C2523), RoundedCornerShape(14.dp))
                                .clickable {
                                    val savedPath = recorder.stopRecording()
                                    isRecording = false
                                    if (!savedPath.isNullOrBlank()) {
                                        onRecordingComplete(savedPath)
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("stop_recording_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("save voice note ♡", fontFamily = FontFamily.Serif, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                } else {
                    // Ready to record
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(6.dp, CircleShape)
                            .background(Color(0xFF2C2523), CircleShape)
                            .clickable {
                                val result = recorder.startRecording(context)
                                if (result.isSuccess) {
                                    isRecording = true
                                } else {
                                    permissionDeniedMessage = "Failed to start recording. Please try again."
                                }
                            }
                            .testTag("start_recording_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Tap to Record",
                            tint = Color(0xFFFBF8F2),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "tap mic to begin recording",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 15.sp,
                        color = Color(0xFF5A4D41)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(8.dp)
                    ) {
                        Text("cancel", fontFamily = FontFamily.Serif, fontSize = 12.sp, color = Color(0xFF8A7E75))
                    }
                }
            }
        }
    }
}

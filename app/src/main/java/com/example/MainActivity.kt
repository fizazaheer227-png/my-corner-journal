package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.local.entity.UserSettingsEntity
import com.example.ui.JournalMainScaffold
import com.example.ui.screens.desk.JournalDeskScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.theme.MyCornerTheme
import com.example.viewmodel.JournalViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: JournalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userSettings by viewModel.userSettings.collectAsState()
            val isLocked by viewModel.isLocked.collectAsState()
            val isJournalOpen by viewModel.isJournalOpen.collectAsState()
            val isSettingsLoaded by viewModel.isSettingsLoaded.collectAsState()

            val themeName = userSettings?.themeName ?: "Vibrant Palette"

            MyCornerTheme(themeName = themeName) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!isSettingsLoaded) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFFFDFBF7)
                        ) {}
                    } else {
                        val isOnboardingDone = userSettings?.isOnboardingCompleted == true

                        AnimatedContent(
                            targetState = Triple(isOnboardingDone, isJournalOpen, isLocked),
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "appRootFlow",
                            modifier = Modifier.fillMaxSize()
                        ) { (onboarded, open, locked) ->
                            if (!onboarded) {
                                OnboardingScreen(
                                    onComplete = { name, passcode, theme, quote, handwriting, sampleUri, sections ->
                                        viewModel.completeOnboarding(
                                            name = name,
                                            passcode = passcode,
                                            themeName = theme,
                                            coverQuote = quote,
                                            handwritingStyle = handwriting,
                                            handwritingSampleUri = sampleUri,
                                            enabledSections = sections
                                        )
                                    }
                                )
                            } else if (!open || locked) {
                                JournalDeskScreen(
                                    userSettings = userSettings ?: UserSettingsEntity(userName = "Dreamer"),
                                    isLocked = locked,
                                    onUnlockAttempt = { pin ->
                                        viewModel.unlockJournalWithPasscode(pin)
                                    },
                                    onBiometricUnlock = {
                                        viewModel.unlockWithBiometrics()
                                    },
                                    onEnterJournal = {
                                        viewModel.openJournal()
                                    }
                                )
                            } else {
                                JournalMainScaffold(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppForegrounded()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppBackgrounded()
    }
}

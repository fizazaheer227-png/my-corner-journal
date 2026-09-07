package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalJournalTheme = staticCompositionLocalOf { JournalThemes[0] }

@Composable
fun MyCornerTheme(
    themeName: String = "Vibrant Palette",
    content: @Composable () -> Unit
) {
    val journalTheme = getThemeByName(themeName)

    val isDark = themeName.equals("Midnight", ignoreCase = true)
    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = journalTheme.coverAccentColor,
            secondary = journalTheme.ribbonColor,
            background = journalTheme.paperColor,
            surface = journalTheme.coverColor,
            onPrimary = Color.White,
            onBackground = journalTheme.textColor,
            onSurface = journalTheme.textColor
        )
    } else {
        lightColorScheme(
            primary = journalTheme.coverColor,
            secondary = journalTheme.ribbonColor,
            background = journalTheme.paperColor,
            surface = journalTheme.paperColor,
            onPrimary = Color.White,
            onBackground = journalTheme.textColor,
            onSurface = journalTheme.textColor
        )
    }

    CompositionLocalProvider(LocalJournalTheme provides journalTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = JournalTypography,
            content = content
        )
    }
}

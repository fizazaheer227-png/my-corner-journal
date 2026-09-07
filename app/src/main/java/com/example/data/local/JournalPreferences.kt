package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.entity.UserSettingsEntity

/**
 * JournalPreferences provides synchronous, reliable persistent storage for user session,
 * onboarding completion status, PIN security hash, theme preferences and journal personalization.
 * This guarantees instant state availability upon cold app launch without waiting for database initialization.
 */
class JournalPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "my_corner_journal_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "is_onboarding_completed"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_PASSCODE_HASH = "passcode_hash"
        private const val KEY_IS_PASSCODE_ENABLED = "is_passcode_enabled"
        private const val KEY_AUTO_LOCK_ENABLED = "auto_lock_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_THEME_NAME = "theme_name"
        private const val KEY_COVER_COLOR = "cover_color"
        private const val KEY_COVER_QUOTE = "cover_quote"
        private const val KEY_COVER_STICKERS = "cover_stickers"
        private const val KEY_HANDWRITING_MODE = "handwriting_mode"
        private const val KEY_HANDWRITING_STYLE = "handwriting_style"
        private const val KEY_HANDWRITING_SAMPLE_URI = "handwriting_sample_uri"
        private const val KEY_ENABLED_SECTIONS = "enabled_sections"
    }

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).commit()
        }

    fun saveSettings(settings: UserSettingsEntity) {
        prefs.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, settings.isOnboardingCompleted)
            .putString(KEY_USER_NAME, settings.userName)
            .putString(KEY_PASSCODE_HASH, settings.passcodeHash)
            .putBoolean(KEY_IS_PASSCODE_ENABLED, settings.isPasscodeEnabled)
            .putBoolean(KEY_AUTO_LOCK_ENABLED, settings.autoLockEnabled)
            .putBoolean(KEY_BIOMETRIC_ENABLED, settings.biometricEnabled)
            .putString(KEY_THEME_NAME, settings.themeName)
            .putLong(KEY_COVER_COLOR, settings.coverColor)
            .putString(KEY_COVER_QUOTE, settings.coverQuote)
            .putString(KEY_COVER_STICKERS, settings.coverStickers)
            .putBoolean(KEY_HANDWRITING_MODE, settings.handwritingMode)
            .putString(KEY_HANDWRITING_STYLE, settings.handwritingStyle)
            .putString(KEY_HANDWRITING_SAMPLE_URI, settings.handwritingSampleUri)
            .putString(KEY_ENABLED_SECTIONS, settings.enabledSections)
            .commit()
    }

    fun getSettings(): UserSettingsEntity {
        return UserSettingsEntity(
            id = 1,
            userName = prefs.getString(KEY_USER_NAME, "Dreamer") ?: "Dreamer",
            passcodeHash = prefs.getString(KEY_PASSCODE_HASH, "") ?: "",
            isPasscodeEnabled = prefs.getBoolean(KEY_IS_PASSCODE_ENABLED, false),
            autoLockEnabled = prefs.getBoolean(KEY_AUTO_LOCK_ENABLED, true),
            biometricEnabled = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false),
            themeName = prefs.getString(KEY_THEME_NAME, "Vibrant Palette") ?: "Vibrant Palette",
            coverColor = prefs.getLong(KEY_COVER_COLOR, 0xFF5C4033),
            coverQuote = prefs.getString(KEY_COVER_QUOTE, "every little moment counts.") ?: "every little moment counts.",
            coverStickers = prefs.getString(KEY_COVER_STICKERS, "🌸,✨,📜") ?: "🌸,✨,📜",
            handwritingMode = prefs.getBoolean(KEY_HANDWRITING_MODE, true),
            handwritingStyle = prefs.getString(KEY_HANDWRITING_STYLE, "Cursive") ?: "Cursive",
            handwritingSampleUri = prefs.getString(KEY_HANDWRITING_SAMPLE_URI, null),
            enabledSections = prefs.getString(
                KEY_ENABLED_SECTIONS,
                "journal,mood,doodle,braindump,people,memoryjar,vault,futureme,wishlist,visionboard,currently,gratitude,period,habits,letters,lifecapsules,aboutme"
            ) ?: "journal,mood,doodle,braindump,people,memoryjar,vault,futureme,wishlist,visionboard,currently,gratitude,period,habits,letters,lifecapsules,aboutme",
            isOnboardingCompleted = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        )
    }

    fun clear() {
        prefs.edit().clear().commit()
    }
}

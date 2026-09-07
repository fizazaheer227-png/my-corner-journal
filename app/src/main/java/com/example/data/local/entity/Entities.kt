package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Dreamer",
    val passcodeHash: String = "",
    val isPasscodeEnabled: Boolean = false,
    val autoLockEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val themeName: String = "Vibrant Palette", // Vibrant Palette, Vintage Newspaper, Cozy Scrapbook, Botanical, Midnight, Y2K / Colorful, Minimal Paper, Soft Neutral, Classic Diary
    val coverColor: Long = 0xFF5C4033,
    val coverQuote: String = "every little moment counts.",
    val coverStickers: String = "🌸,✨,📜",
    val handwritingMode: Boolean = true,
    val handwritingStyle: String = "Cursive", // Cursive, Script, Casual, Typewriter, Vintage Serif
    val handwritingSampleUri: String? = null,
    val enabledSections: String = "journal,mood,doodle,braindump,people,memoryjar,vault,futureme,wishlist,visionboard,currently,gratitude,period,habits,letters,lifecapsules,aboutme",
    val isOnboardingCompleted: Boolean = false
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val mood: String = "Calm", // Happy, Calm, Okay, Sad, Angry, Tired, Excited
    val template: String = "messy_scrapbook", // messy_scrapbook, newspaper_editorial, classic_diary, polaroid_story, minimal_journal
    val tags: String = "", // comma-separated
    val imageUris: String = "", // comma-separated
    val doodleData: String = "", // svg path / line data or uri
    val voiceNoteUri: String = "",
    val taggedPersonName: String = "",
    val isFavorite: Boolean = false,
    val isDraft: Boolean = false,
    val weatherOrLocation: String = ""
)

@Entity(tableName = "mood_logs")
data class MoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val mood: String, // Happy, Calm, Okay, Sad, Angry, Tired, Excited
    val note: String = "",
    val colorHex: Long = 0xFFD89D7E,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "people")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nickname: String = "",
    val photoUri: String = "",
    val birthday: String = "",
    val howWeMet: String = "",
    val favoriteMemories: String = "",
    val insideJokes: String = "",
    val favoriteThings: String = "",
    val thingsNeverForget: String = "",
    // Little Things
    val coffeeOrder: String = "",
    val favoriteSnack: String = "",
    val favoriteFlower: String = "",
    val favoriteMusic: String = "",
    val dreamDestination: String = "",
    val giftIdeas: String = "",
    val thingsCheerThemUp: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val imageUri: String = "",
    val taggedPersonName: String = "",
    val mood: String = "Happy",
    val isFavorite: Boolean = false,
    val inJar: Boolean = true
)

@Entity(tableName = "sticky_notes")
data class StickyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val colorHex: Long = 0xFFFFF9C4, // Soft yellow, pastel pink, mint, lavender, peach
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f,
    val widthDp: Float = 160f,
    val heightDp: Float = 140f,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "future_capsules")
data class FutureCapsuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val imageUri: String = "",
    val voiceNoteUri: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val unlockDateMillis: Long,
    val isOpened: Boolean = false
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val board: String = "things I want", // things I want, books, clothes, places, dream room, bucket list, experiences, custom
    val title: String,
    val note: String = "",
    val imageUri: String = "",
    val link: String = "",
    val price: String = "",
    val isAcquired: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vision_board_items")
data class VisionBoardItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val boardCategory: String = "dream life", // year, career, travel, dream life, custom
    val itemType: String = "photo", // photo, quote, sticker, tape, magazine_cutout
    val content: String, // imageUri, text, or sticker emoji
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val tintColor: Long = 0
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val plantType: String = "daisy", // daisy, rose, lavender, succulent, sunflower, fern
    val completedDates: String = "", // Comma-separated YYYY-MM-DD
    val totalGrowth: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "period_logs")
data class PeriodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: String, // YYYY-MM-DD
    val endDate: String = "",
    val flow: String = "Medium", // Light, Medium, Heavy, Spotting
    val symptoms: String = "", // Cramps, Headache, Fatigue, Mood swings, Bloating
    val mood: String = "Calm",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "letters")
data class LetterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String = "future me", // future me, younger me, someone I love, letter I'll never send, gratitude letter, custom
    val recipient: String = "",
    val title: String,
    val body: String,
    val paperStyle: String = "vintage_parchment", // vintage_parchment, floral, blue_lined, dark_stationery
    val dateMillis: Long = System.currentTimeMillis(),
    val imageUri: String = ""
)

@Entity(tableName = "life_capsules")
data class LifeCapsuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val yearOrAge: String,
    val favoriteSong: String = "",
    val favoriteMemory: String = "",
    val favoritePerson: String = "",
    val biggestLesson: String = "",
    val currentDream: String = "",
    val favoritePhotoUri: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

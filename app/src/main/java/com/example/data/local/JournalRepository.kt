package com.example.data.local

import android.content.Context
import com.example.data.local.entity.FutureCapsuleEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.LetterEntity
import com.example.data.local.entity.LifeCapsuleEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.MoodLogEntity
import com.example.data.local.entity.PeriodLogEntity
import com.example.data.local.entity.PersonEntity
import com.example.data.local.entity.StickyNoteEntity
import com.example.data.local.entity.UserSettingsEntity
import com.example.data.local.entity.VisionBoardItemEntity
import com.example.data.local.entity.WishlistItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalRepository(private val db: AppDatabase) {

    // User Settings
    val userSettings: Flow<UserSettingsEntity?> = db.userSettingsDao().getUserSettings()

    suspend fun getUserSettingsDirect(): UserSettingsEntity? = withContext(Dispatchers.IO) {
        db.userSettingsDao().getUserSettingsDirect()
    }

    suspend fun saveUserSettings(settings: UserSettingsEntity) = withContext(Dispatchers.IO) {
        db.userSettingsDao().insertOrUpdate(settings)
    }

    // Journal Entries
    val allEntries: Flow<List<JournalEntryEntity>> = db.journalDao().getAllEntries()
    val favoriteEntries: Flow<List<JournalEntryEntity>> = db.journalDao().getFavoriteEntries()

    fun searchEntries(query: String): Flow<List<JournalEntryEntity>> = db.journalDao().searchEntries(query)
    fun getEntriesForPerson(person: String): Flow<List<JournalEntryEntity>> = db.journalDao().getEntriesForPerson(person)

    suspend fun getEntryById(id: Long): JournalEntryEntity? = withContext(Dispatchers.IO) {
        db.journalDao().getEntryById(id)
    }

    suspend fun saveEntry(entry: JournalEntryEntity): Long = withContext(Dispatchers.IO) {
        db.journalDao().insertEntry(entry)
    }

    suspend fun deleteEntry(id: Long) = withContext(Dispatchers.IO) {
        db.journalDao().deleteEntry(id)
    }

    // Mood Logs
    val allMoodLogs: Flow<List<MoodLogEntity>> = db.moodDao().getAllMoodLogs()

    suspend fun logMood(mood: String, note: String = "", colorHex: Long) = withContext(Dispatchers.IO) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        db.moodDao().insertMood(
            MoodLogEntity(
                dateString = today,
                mood = mood,
                note = note,
                colorHex = colorHex
            )
        )
    }

    // People
    val allPeople: Flow<List<PersonEntity>> = db.personDao().getAllPeople()

    suspend fun getPersonById(id: Long): PersonEntity? = withContext(Dispatchers.IO) {
        db.personDao().getPersonById(id)
    }

    suspend fun savePerson(person: PersonEntity): Long = withContext(Dispatchers.IO) {
        db.personDao().insertPerson(person)
    }

    suspend fun deletePerson(id: Long) = withContext(Dispatchers.IO) {
        db.personDao().deletePerson(id)
    }

    // Memories & Memory Jar
    val allMemories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()

    suspend fun getRandomJarMemory(): MemoryEntity? = withContext(Dispatchers.IO) {
        val list = db.memoryDao().getJarMemories()
        if (list.isNotEmpty()) list.random() else null
    }

    suspend fun saveMemory(memory: MemoryEntity): Long = withContext(Dispatchers.IO) {
        db.memoryDao().insertMemory(memory)
    }

    suspend fun deleteMemory(id: Long) = withContext(Dispatchers.IO) {
        db.memoryDao().deleteMemory(id)
    }

    // Sticky Notes (Brain Dump)
    val allNotes: Flow<List<StickyNoteEntity>> = db.stickyNoteDao().getAllNotes()

    suspend fun saveNote(note: StickyNoteEntity): Long = withContext(Dispatchers.IO) {
        db.stickyNoteDao().insertNote(note)
    }

    suspend fun updateNote(note: StickyNoteEntity) = withContext(Dispatchers.IO) {
        db.stickyNoteDao().updateNote(note)
    }

    suspend fun deleteNote(id: Long) = withContext(Dispatchers.IO) {
        db.stickyNoteDao().deleteNote(id)
    }

    // Future Capsules
    val allCapsules: Flow<List<FutureCapsuleEntity>> = db.futureCapsuleDao().getAllCapsules()

    suspend fun saveCapsule(capsule: FutureCapsuleEntity): Long = withContext(Dispatchers.IO) {
        db.futureCapsuleDao().insertCapsule(capsule)
    }

    suspend fun updateCapsule(capsule: FutureCapsuleEntity) = withContext(Dispatchers.IO) {
        db.futureCapsuleDao().updateCapsule(capsule)
    }

    suspend fun deleteCapsule(id: Long) = withContext(Dispatchers.IO) {
        db.futureCapsuleDao().deleteCapsule(id)
    }

    // Wishlist
    val allWishlist: Flow<List<WishlistItemEntity>> = db.wishlistDao().getAllItems()
    fun getWishlistByBoard(board: String): Flow<List<WishlistItemEntity>> = db.wishlistDao().getItemsByBoard(board)

    suspend fun saveWishlistItem(item: WishlistItemEntity): Long = withContext(Dispatchers.IO) {
        db.wishlistDao().insertItem(item)
    }

    suspend fun updateWishlistItem(item: WishlistItemEntity) = withContext(Dispatchers.IO) {
        db.wishlistDao().updateItem(item)
    }

    suspend fun deleteWishlistItem(id: Long) = withContext(Dispatchers.IO) {
        db.wishlistDao().deleteItem(id)
    }

    // Vision Board
    fun getVisionBoardItems(category: String): Flow<List<VisionBoardItemEntity>> = db.visionBoardDao().getItemsByCategory(category)

    suspend fun saveVisionBoardItem(item: VisionBoardItemEntity): Long = withContext(Dispatchers.IO) {
        db.visionBoardDao().insertItem(item)
    }

    suspend fun deleteVisionBoardItem(id: Long) = withContext(Dispatchers.IO) {
        db.visionBoardDao().deleteItem(id)
    }

    // Habit Garden
    val allHabits: Flow<List<HabitEntity>> = db.habitDao().getAllHabits()

    suspend fun saveHabit(habit: HabitEntity): Long = withContext(Dispatchers.IO) {
        db.habitDao().insertHabit(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        db.habitDao().updateHabit(habit)
    }

    suspend fun deleteHabit(id: Long) = withContext(Dispatchers.IO) {
        db.habitDao().deleteHabit(id)
    }

    // Period Tracker
    val allPeriodLogs: Flow<List<PeriodLogEntity>> = db.periodDao().getAllLogs()

    suspend fun savePeriodLog(log: PeriodLogEntity): Long = withContext(Dispatchers.IO) {
        db.periodDao().insertLog(log)
    }

    suspend fun deletePeriodLog(id: Long) = withContext(Dispatchers.IO) {
        db.periodDao().deleteLog(id)
    }

    // Letters
    val allLetters: Flow<List<LetterEntity>> = db.letterDao().getAllLetters()

    suspend fun saveLetter(letter: LetterEntity): Long = withContext(Dispatchers.IO) {
        db.letterDao().insertLetter(letter)
    }

    suspend fun deleteLetter(id: Long) = withContext(Dispatchers.IO) {
        db.letterDao().deleteLetter(id)
    }

    // Life Capsules
    val allLifeCapsules: Flow<List<LifeCapsuleEntity>> = db.lifeCapsuleDao().getAllCapsules()

    suspend fun saveLifeCapsule(capsule: LifeCapsuleEntity): Long = withContext(Dispatchers.IO) {
        db.lifeCapsuleDao().insertCapsule(capsule)
    }

    suspend fun deleteLifeCapsule(id: Long) = withContext(Dispatchers.IO) {
        db.lifeCapsuleDao().deleteCapsule(id)
    }

    // Seed Initial Gentle Data if empty
    suspend fun seedInitialDataIfNeeded(userName: String) = withContext(Dispatchers.IO) {
        val existing = db.journalDao().getEntryById(1)
        if (existing == null) {
            // Seed a welcome journal entry with scrapbook styling
            db.journalDao().insertEntry(
                JournalEntryEntity(
                    title = "the first page of my corner ♡",
                    content = "Today I opened this journal. A quiet place where thoughts don't have to be perfect, memories are tucked between washi tape, and I can just exist without expectations.\n\nHere's to little moments, warm cups of tea, and keeping track of what truly matters to my heart.",
                    mood = "Calm",
                    template = "messy_scrapbook",
                    tags = "beginnings, cozy, gratitude",
                    weatherOrLocation = "quiet morning, desk nook"
                )
            )

            // Seed a sample memory
            db.memoryDao().insertMemory(
                MemoryEntity(
                    title = "sunlight through the curtains",
                    content = "Sitting on the floor listening to acoustic guitar while golden light danced across the rug. Nothing happened, but everything felt safe.",
                    mood = "Calm",
                    inJar = true,
                    isFavorite = true
                )
            )
            db.memoryDao().insertMemory(
                MemoryEntity(
                    title = "laughing so hard my stomach hurt",
                    content = "Late night phone call about the most silly inside joke. We couldn't even breathe.",
                    mood = "Happy",
                    inJar = true,
                    isFavorite = true
                )
            )

            // Seed sticky notes for Brain Dump
            db.stickyNoteDao().insertNote(
                StickyNoteEntity(
                    text = "finish reading chapter 4 ☕",
                    colorHex = 0xFFFFF9C4,
                    offsetX = 20f,
                    offsetY = 30f,
                    rotation = -3f
                )
            )
            db.stickyNoteDao().insertNote(
                StickyNoteEntity(
                    text = "order lavender seeds for spring windowsill 🌿",
                    colorHex = 0xFFE1BEE7,
                    offsetX = 160f,
                    offsetY = 80f,
                    rotation = 4f
                )
            )
            db.stickyNoteDao().insertNote(
                StickyNoteEntity(
                    text = "be gentler with yourself today.",
                    colorHex = 0xFFFFCCBC,
                    offsetX = 50f,
                    offsetY = 220f,
                    rotation = -1.5f
                )
            )

            // Seed sample habits in garden
            db.habitDao().insertHabit(
                HabitEntity(
                    title = "Drink warm water",
                    plantType = "daisy",
                    totalGrowth = 3
                )
            )
            db.habitDao().insertHabit(
                HabitEntity(
                    title = "Read 10 pages",
                    plantType = "lavender",
                    totalGrowth = 4
                )
            )
            db.habitDao().insertHabit(
                HabitEntity(
                    title = "Gentle evening stretch",
                    plantType = "sunflower",
                    totalGrowth = 2
                )
            )

            // Seed sample wishlist item
            db.wishlistDao().insertItem(
                WishlistItemEntity(
                    board = "books",
                    title = "The House in the Cerulean Sea",
                    note = "Recommended for cozy rainy afternoons",
                    isAcquired = false
                )
            )
            db.wishlistDao().insertItem(
                WishlistItemEntity(
                    board = "things I want",
                    title = "Linen journal cover with embroidered flowers",
                    note = "For keepsakes and ticket stubs",
                    isAcquired = true
                )
            )

            // Seed sample important person
            db.personDao().insertPerson(
                PersonEntity(
                    name = "Maya",
                    nickname = "May-bee",
                    howWeMet = "Sitting on the floor of the local bookstore trying to reach the same vintage poetry book.",
                    favoriteMemories = "Driving home with all windows down singing out of tune at 11 PM.",
                    insideJokes = "The flying blueberry incident.",
                    coffeeOrder = "Iced oat vanilla latte, extra cinnamon",
                    favoriteSnack = "Sea salt dark chocolate pretzels",
                    favoriteFlower = "Sunflowers and white daisies",
                    thingsNeverForget = "How she always remembers what you said three weeks ago, and sends a song when you need it most.",
                    giftIdeas = "Vintage music box or handmade bookmark"
                )
            )

            // Seed sample Future Me letter
            db.futureCapsuleDao().insertCapsule(
                FutureCapsuleEntity(
                    title = "to the version of me in six months",
                    content = "Dear future me, I hope you are drinking enough water and breathing deeply. Whatever you were worrying about when you wrote this... did it all work out? Remember how strong your heart is.",
                    unlockDateMillis = System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000)
                )
            )

            // Seed sample letter
            db.letterDao().insertLetter(
                LetterEntity(
                    category = "someone I love",
                    recipient = "to someone special",
                    title = "thank you for making this world softer",
                    body = "I don't say it aloud often enough, but your presence makes the heaviest days feel manageable. Thank you for your warmth.",
                    paperStyle = "vintage_parchment"
                )
            )
        }
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        db.clearAllTables()
    }
}

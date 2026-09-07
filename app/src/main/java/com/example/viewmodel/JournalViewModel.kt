package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.JournalRepository
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
import com.example.ui.components.JournalTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository
    private var cachedPasscodeHash: String = ""

    init {
        val db = AppDatabase.getInstance(application)
        repository = JournalRepository(db)
    }

    val userSettings: StateFlow<UserSettingsEntity?> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val allEntries: StateFlow<List<JournalEntryEntity>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMoodLogs: StateFlow<List<MoodLogEntity>> = repository.allMoodLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPeople: StateFlow<List<PersonEntity>> = repository.allPeople
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMemories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<StickyNoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCapsules: StateFlow<List<FutureCapsuleEntity>> = repository.allCapsules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWishlist: StateFlow<List<WishlistItemEntity>> = repository.allWishlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHabits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPeriodLogs: StateFlow<List<PeriodLogEntity>> = repository.allPeriodLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLetters: StateFlow<List<LetterEntity>> = repository.allLetters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLifeCapsules: StateFlow<List<LifeCapsuleEntity>> = repository.allLifeCapsules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation & Locks
    private val _isLocked = MutableStateFlow(false)
    val isLocked = _isLocked.asStateFlow()

    private val _isJournalOpen = MutableStateFlow(false)
    val isJournalOpen = _isJournalOpen.asStateFlow()

    private val _currentTab = MutableStateFlow(JournalTab.TODAY)
    val currentTab = _currentTab.asStateFlow()

    private val _activeSubScreen = MutableStateFlow<String?>(null)
    val activeSubScreen = _activeSubScreen.asStateFlow()

    private val _selectedPersonId = MutableStateFlow<Long?>(null)
    val selectedPersonId = _selectedPersonId.asStateFlow()

    private val _selectedEntryForEdit = MutableStateFlow<JournalEntryEntity?>(null)
    val selectedEntryForEdit = _selectedEntryForEdit.asStateFlow()

    // Random Jar Unfolded Memory
    private val _unfoldedJarMemory = MutableStateFlow<MemoryEntity?>(null)
    val unfoldedJarMemory = _unfoldedJarMemory.asStateFlow()

    // Prompt dice
    private val promptList = listOf(
        "what is one little detail from yesterday that made you smile?",
        "describe the smell of your favorite place in the world.",
        "what song feels like home right now, and why?",
        "write a gentle letter to the person you were three years ago.",
        "what's an inside joke that will never stop being funny?",
        "if your heart was a room right now, what would it look like?",
        "name 3 things you love about the way your mind works.",
        "what are you craving that food cannot satisfy?",
        "who made you feel truly listened to recently?",
        "what is something you're secretly proud of accomplishing?",
        "describe your dream morning with zero alarms and endless coffee.",
        "what is a truth you've been avoiding saying aloud?"
    )

    private val _currentPrompt = MutableStateFlow(promptList[0])
    val currentPrompt = _currentPrompt.asStateFlow()

    // Daily comforting thoughts
    private val dailyCornerNotes = listOf(
        "you do not have to have everything figured out to have a soft and meaningful day. be here.",
        "remember: the flowers don't compare themselves to the tree. they just blossom.",
        "you are deeply, quietly loved in ways you don't even see.",
        "send a gentle text to someone who crossed your mind today.",
        "take a long breath, unclench your shoulders, and sip some warm water."
    )

    private val _dailyCornerNote = MutableStateFlow(dailyCornerNotes[0])
    val dailyCornerNote = _dailyCornerNote.asStateFlow()

    private val _isSettingsLoaded = MutableStateFlow(false)
    val isSettingsLoaded = _isSettingsLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = repository.getUserSettingsDirect()
            if (settings != null) {
                cachedPasscodeHash = settings.passcodeHash
                if (settings.isPasscodeEnabled && settings.passcodeHash.isNotEmpty()) {
                    _isLocked.value = true
                }
            }
            _isSettingsLoaded.value = true
            repository.seedInitialDataIfNeeded(settings?.userName ?: "Dreamer")
        }
    }

    fun rollPromptDice() {
        _currentPrompt.value = promptList.random()
    }

    fun rotateDailyCorner() {
        _dailyCornerNote.value = dailyCornerNotes.random()
    }

    fun setTab(tab: JournalTab) {
        _currentTab.value = tab
        _activeSubScreen.value = null
    }

    fun openSubScreen(screenId: String) {
        _activeSubScreen.value = screenId
    }

    fun closeSubScreen() {
        _activeSubScreen.value = null
    }

    private var lastActiveTime: Long = System.currentTimeMillis()
    private var backgroundedTime: Long = 0L
    val INACTIVITY_AUTO_LOCK_TIMEOUT_MS = 5 * 60 * 1000L

    fun recordUserActivity() {
        lastActiveTime = System.currentTimeMillis()
    }

    fun onAppForegrounded() {
        val hash = userSettings.value?.passcodeHash?.takeIf { it.isNotEmpty() } ?: cachedPasscodeHash
        val isPasscodeEnabled = userSettings.value?.isPasscodeEnabled ?: hash.isNotEmpty()
        val isAutoLock = userSettings.value?.autoLockEnabled ?: true
        if (isPasscodeEnabled && hash.isNotEmpty() && isAutoLock) {
            val now = System.currentTimeMillis()
            if (backgroundedTime > 0L || (now - lastActiveTime > INACTIVITY_AUTO_LOCK_TIMEOUT_MS)) {
                lockJournal()
            }
        }
        backgroundedTime = 0L
        lastActiveTime = System.currentTimeMillis()
    }

    fun onAppBackgrounded() {
        backgroundedTime = System.currentTimeMillis()
    }

    fun checkInactivityTimeout(): Boolean {
        val hash = userSettings.value?.passcodeHash?.takeIf { it.isNotEmpty() } ?: cachedPasscodeHash
        val isPasscodeEnabled = userSettings.value?.isPasscodeEnabled ?: hash.isNotEmpty()
        val isAutoLock = userSettings.value?.autoLockEnabled ?: true
        if (!_isLocked.value && isPasscodeEnabled && hash.isNotEmpty() && isAutoLock) {
            val now = System.currentTimeMillis()
            if (now - lastActiveTime > INACTIVITY_AUTO_LOCK_TIMEOUT_MS) {
                lockJournal()
                return true
            }
        }
        return false
    }

    fun openJournal() {
        _isLocked.value = false
        _isJournalOpen.value = true
        _currentTab.value = JournalTab.TODAY
        _activeSubScreen.value = null
        recordUserActivity()
    }

    fun closeJournalToCover() {
        _isJournalOpen.value = false
    }

    fun lockJournal() {
        val hash = userSettings.value?.passcodeHash?.takeIf { it.isNotEmpty() } ?: cachedPasscodeHash
        val isPasscodeEnabled = userSettings.value?.isPasscodeEnabled ?: hash.isNotEmpty()
        if (isPasscodeEnabled && hash.isNotEmpty()) {
            _isLocked.value = true
        } else {
            _isLocked.value = false
        }
        _isJournalOpen.value = false
    }

    fun unlockJournalWithPasscode(passcode: String): Boolean {
        val hashToMatch = userSettings.value?.passcodeHash?.takeIf { it.isNotEmpty() } ?: cachedPasscodeHash
        if (hashToMatch.isEmpty()) return false
        val hashed = hashPasscode(passcode)
        if (hashed == hashToMatch) {
            _isLocked.value = false
            recordUserActivity()
            return true
        }
        return false
    }

    fun unlockWithBiometrics() {
        _isLocked.value = false
        recordUserActivity()
    }

    fun completeOnboarding(
        name: String,
        passcode: String,
        themeName: String,
        coverQuote: String,
        handwritingStyle: String,
        handwritingSampleUri: String?,
        enabledSections: String
    ) {
        val hash = if (passcode.isNotEmpty()) hashPasscode(passcode) else ""
        cachedPasscodeHash = hash
        _isLocked.value = hash.isNotEmpty()
        _isJournalOpen.value = false
        viewModelScope.launch {
            val newSettings = UserSettingsEntity(
                id = 1,
                userName = name.ifBlank { "Dreamer" },
                passcodeHash = hash,
                isPasscodeEnabled = hash.isNotEmpty(),
                autoLockEnabled = true,
                themeName = themeName,
                coverQuote = coverQuote.ifBlank { "every little moment counts." },
                handwritingStyle = handwritingStyle,
                handwritingSampleUri = handwritingSampleUri,
                enabledSections = enabledSections,
                isOnboardingCompleted = true
            )
            repository.saveUserSettings(newSettings)
            repository.seedInitialDataIfNeeded(newSettings.userName)
        }
    }

    fun updateSettings(settings: UserSettingsEntity) {
        cachedPasscodeHash = settings.passcodeHash
        viewModelScope.launch {
            repository.saveUserSettings(settings)
        }
    }

    fun logMood(mood: String, note: String = "", colorHex: Long) {
        viewModelScope.launch {
            repository.logMood(mood, note, colorHex)
        }
    }

    fun saveJournalEntry(entry: JournalEntryEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveEntry(entry)
            _selectedEntryForEdit.value = null
            onComplete()
        }
    }

    fun deleteJournalEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteEntry(id)
        }
    }

    fun editEntry(entry: JournalEntryEntity) {
        _selectedEntryForEdit.value = entry
        _activeSubScreen.value = "editor"
    }

    fun clearEntrySelection() {
        _selectedEntryForEdit.value = null
    }

    // Sticky Notes
    fun addStickyNote(text: String, colorHex: Long) {
        viewModelScope.launch {
            val count = allNotes.value.size
            repository.saveNote(
                StickyNoteEntity(
                    text = text,
                    colorHex = colorHex,
                    offsetX = (count % 3) * 60f + 20f,
                    offsetY = (count / 3) * 70f + 20f,
                    rotation = if (count % 2 == 0) -2.5f else 3f
                )
            )
        }
    }

    fun updateStickyNote(note: StickyNoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteStickyNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    fun convertNoteToJournal(note: StickyNoteEntity) {
        viewModelScope.launch {
            repository.saveEntry(
                JournalEntryEntity(
                    title = "brain spark 💡",
                    content = note.text,
                    mood = "Calm",
                    template = "messy_scrapbook",
                    tags = "braindump, notes"
                )
            )
            repository.deleteNote(note.id)
        }
    }

    // People
    fun savePerson(person: PersonEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.savePerson(person)
            onComplete()
        }
    }

    fun deletePerson(id: Long) {
        viewModelScope.launch {
            repository.deletePerson(id)
            if (_selectedPersonId.value == id) {
                _selectedPersonId.value = null
                _activeSubScreen.value = null
            }
        }
    }

    fun selectPerson(id: Long) {
        _selectedPersonId.value = id
        _activeSubScreen.value = "person_profile"
    }

    // Memory Jar
    fun pickMemoryFromJar() {
        viewModelScope.launch {
            _unfoldedJarMemory.value = repository.getRandomJarMemory()
        }
    }

    fun putMemoryBackInJar() {
        _unfoldedJarMemory.value = null
    }

    fun saveMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            repository.saveMemory(memory)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    // Wishlist
    fun saveWishlistItem(item: WishlistItemEntity) {
        viewModelScope.launch {
            repository.saveWishlistItem(item)
        }
    }

    fun toggleWishlistAcquired(item: WishlistItemEntity) {
        viewModelScope.launch {
            repository.updateWishlistItem(item.copy(isAcquired = !item.isAcquired))
        }
    }

    fun deleteWishlistItem(id: Long) {
        viewModelScope.launch {
            repository.deleteWishlistItem(id)
        }
    }

    // Habits
    fun completeHabitToday(habit: HabitEntity) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val dates = habit.completedDates.split(",").filter { it.isNotBlank() }.toMutableList()
            if (!dates.contains(today)) {
                dates.add(today)
                repository.updateHabit(
                    habit.copy(
                        completedDates = dates.joinToString(","),
                        totalGrowth = habit.totalGrowth + 1
                    )
                )
            }
        }
    }

    fun addHabit(title: String, plantType: String) {
        viewModelScope.launch {
            repository.saveHabit(
                HabitEntity(
                    title = title,
                    plantType = plantType,
                    totalGrowth = 1
                )
            )
        }
    }

    fun deleteHabit(id: Long) {
        viewModelScope.launch {
            repository.deleteHabit(id)
        }
    }

    // Period Tracker
    fun logPeriod(startDate: String, flow: String, symptoms: String, notes: String) {
        logPeriod(startDate = startDate, endDate = "", flow = flow, symptoms = symptoms, mood = "Calm", notes = notes)
    }

    fun logPeriod(startDate: String, endDate: String = "", flow: String = "Medium", symptoms: String = "", mood: String = "Calm", notes: String = "") {
        viewModelScope.launch {
            repository.savePeriodLog(
                PeriodLogEntity(
                    startDate = startDate,
                    endDate = endDate,
                    flow = flow,
                    symptoms = symptoms,
                    mood = mood,
                    notes = notes
                )
            )
        }
    }

    fun deletePeriodLog(id: Long) {
        viewModelScope.launch {
            repository.deletePeriodLog(id)
        }
    }

    // Letters
    fun saveLetter(letter: LetterEntity) {
        viewModelScope.launch {
            repository.saveLetter(letter)
        }
    }

    // Future Capsules
    fun saveFutureCapsule(capsule: FutureCapsuleEntity) {
        viewModelScope.launch {
            repository.saveCapsule(capsule)
        }
    }

    // Life Capsules
    fun saveLifeCapsule(capsule: LifeCapsuleEntity) {
        viewModelScope.launch {
            repository.saveLifeCapsule(capsule)
        }
    }

    // Helper navigation and actions
    fun openNewEntry(prompt: String = "") {
        _selectedEntryForEdit.value = if (prompt.isNotBlank()) {
            JournalEntryEntity(
                title = "prompt response",
                content = "“$prompt”\n\n"
            )
        } else null
        _activeSubScreen.value = "editor"
    }

    fun openEditEntry(entry: JournalEntryEntity) {
        _selectedEntryForEdit.value = entry
        _activeSubScreen.value = "editor"
    }

    fun openPersonProfile(personId: Long) {
        _selectedPersonId.value = personId
        _activeSubScreen.value = "person_profile"
    }

    fun openNewPersonProfile() {
        _selectedPersonId.value = null
        _activeSubScreen.value = "person_profile"
    }

    fun pickRandomJarMemory() {
        pickMemoryFromJar()
    }

    fun dismissJarMemory() {
        putMemoryBackInJar()
    }

    fun addMemory(title: String, content: String, mood: String) {
        saveMemory(
            MemoryEntity(
                title = title,
                content = content,
                mood = mood
            )
        )
    }

    fun waterHabit(habit: HabitEntity) {
        completeHabitToday(habit)
    }

    companion object {
        fun hashPasscode(passcode: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(passcode.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}

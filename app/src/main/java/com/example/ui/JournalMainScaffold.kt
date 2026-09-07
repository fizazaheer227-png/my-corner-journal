package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.JournalEntryEntity
import com.example.ui.components.CreationOption
import com.example.ui.components.JournalIndexDialog
import com.example.ui.components.JournalPageTurnContainer
import com.example.ui.components.JournalTab
import com.example.ui.components.JournalTabBar
import com.example.ui.components.ScrapbookBottomNavBar
import com.example.ui.components.SearchRibbonDialog
import com.example.ui.components.UniversalCreationScraps
import com.example.ui.screens.braindump.BrainDumpScreen
import com.example.ui.screens.doodle.DoodleCanvasScreen
import com.example.ui.screens.dreams.DreamsDashboardScreen
import com.example.ui.screens.dreams.DreamsSubTab
import com.example.ui.screens.me.MeSectionScreen
import com.example.ui.screens.me.MeSubPage
import com.example.ui.screens.memories.MemoryJarScreen
import com.example.ui.screens.memories.MemoryVaultScreen
import com.example.ui.screens.mood.MoodTrackerScreen
import com.example.ui.screens.pages.JournalEditorScreen
import com.example.ui.screens.pages.JournalEntriesScreen
import com.example.ui.screens.people.PeopleScrapbookScreen
import com.example.ui.screens.people.PersonProfileScreen
import com.example.ui.screens.period.PeriodTrackerScreen
import com.example.ui.screens.settings.JournalSettingsScreen
import com.example.ui.screens.today.TodayJournalScreen
import com.example.ui.theme.DeskWoodWarm
import com.example.ui.theme.LocalJournalTheme
import com.example.viewmodel.JournalViewModel

@Composable
fun JournalMainScaffold(
    viewModel: JournalViewModel
) {
    val theme = LocalJournalTheme.current

    val currentTab by viewModel.currentTab.collectAsState()
    val activeSubScreen by viewModel.activeSubScreen.collectAsState()
    val selectedPersonId by viewModel.selectedPersonId.collectAsState()
    val selectedEntryForEdit by viewModel.selectedEntryForEdit.collectAsState()
    val unfoldedJarMemory by viewModel.unfoldedJarMemory.collectAsState()

    val entries by viewModel.allEntries.collectAsState()
    val moodLogs by viewModel.allMoodLogs.collectAsState()
    val people by viewModel.allPeople.collectAsState()
    val memories by viewModel.allMemories.collectAsState()
    val notes by viewModel.allNotes.collectAsState()
    val capsules by viewModel.allCapsules.collectAsState()
    val wishlist by viewModel.allWishlist.collectAsState()
    val habits by viewModel.allHabits.collectAsState()
    val periodLogs by viewModel.allPeriodLogs.collectAsState()
    val letters by viewModel.allLetters.collectAsState()
    val currentPrompt by viewModel.currentPrompt.collectAsState()
    val dailyCornerNote by viewModel.dailyCornerNote.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }
    var showIndexDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = activeSubScreen != null || currentTab != JournalTab.TODAY) {
        if (activeSubScreen != null) {
            viewModel.closeSubScreen()
        } else if (currentTab != JournalTab.TODAY) {
            viewModel.setTab(JournalTab.TODAY)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeskWoodWarm)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Physical Journal Tabs & Ribbon Header
            JournalTabBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    viewModel.setTab(tab)
                },
                onSearchClick = { showSearchDialog = true },
                onIndexClick = { showIndexDialog = true },
                onLockClick = { viewModel.lockJournal() }
            )

            // Main Page Surface with Page Transitions
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val allTabs = remember {
                    listOf(
                        JournalTab.TODAY,
                        JournalTab.PAGES,
                        JournalTab.MOOD,
                        JournalTab.PEOPLE,
                        JournalTab.MEMORIES,
                        JournalTab.DREAMS,
                        JournalTab.ME
                    )
                }
                val curTabIndex = allTabs.indexOf(currentTab)
                val canTurnForward = activeSubScreen == null && curTabIndex < allTabs.lastIndex
                val canTurnBackward = activeSubScreen != null || curTabIndex > 0

                JournalPageTurnContainer(
                    targetState = activeSubScreen to currentTab,
                    modifier = Modifier.fillMaxSize(),
                    animationDurationMillis = 450,
                    canTurnForward = canTurnForward,
                    canTurnBackward = canTurnBackward,
                    onTurnForward = {
                        if (canTurnForward) {
                            viewModel.setTab(allTabs[curTabIndex + 1])
                        }
                    },
                    onTurnBackward = {
                        if (activeSubScreen != null) {
                            viewModel.closeSubScreen()
                        } else if (curTabIndex > 0) {
                            viewModel.setTab(allTabs[curTabIndex - 1])
                        }
                    }
                ) { (subScreen, tab) ->
                    when (subScreen) {
                        "period_tracker" -> {
                            PeriodTrackerScreen(
                                periodLogs = periodLogs,
                                onLogPeriod = { sDate, eDate, flow, symptoms, mood, notes ->
                                    viewModel.logPeriod(sDate, eDate, flow, symptoms, mood, notes)
                                },
                                onDeletePeriod = { id ->
                                    viewModel.deletePeriodLog(id)
                                },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "editor" -> {
                            JournalEditorScreen(
                                initialEntry = selectedEntryForEdit,
                                onSave = { entry ->
                                    viewModel.saveJournalEntry(entry) {
                                        viewModel.closeSubScreen()
                                    }
                                },
                                onDelete = { id ->
                                    viewModel.deleteJournalEntry(id)
                                    viewModel.closeSubScreen()
                                },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "doodle" -> {
                            DoodleCanvasScreen(
                                onSaveDoodle = {
                                    viewModel.saveJournalEntry(
                                        JournalEntryEntity(
                                            title = "doodle sketch 🎨",
                                            content = "a creative visual page from my doodle book"
                                        )
                                    )
                                    viewModel.closeSubScreen()
                                },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "braindump" -> {
                            BrainDumpScreen(
                                notes = notes,
                                onAddNote = { text, col -> viewModel.addStickyNote(text, col) },
                                onUpdateNote = { viewModel.updateStickyNote(it) },
                                onDeleteNote = { viewModel.deleteStickyNote(it) },
                                onConvertToJournal = { note ->
                                    viewModel.openNewEntry(prompt = "from brain dump: ${note.text}")
                                },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "person_profile" -> {
                            val selectedPerson = people.firstOrNull { it.id == selectedPersonId }
                            PersonProfileScreen(
                                person = selectedPerson,
                                onSavePerson = {
                                    viewModel.savePerson(it) {
                                        viewModel.closeSubScreen()
                                    }
                                },
                                onDeletePerson = { id ->
                                    viewModel.deletePerson(id)
                                    viewModel.closeSubScreen()
                                },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "memory_vault" -> {
                            MemoryVaultScreen(
                                memories = memories,
                                onDeleteMemory = { viewModel.deleteMemory(it) },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "settings" -> {
                            if (userSettings != null) {
                                JournalSettingsScreen(
                                    userSettings = userSettings!!,
                                    totalEntriesCount = entries.size,
                                    totalMemoriesCount = memories.size,
                                    onSaveSettings = { viewModel.updateSettings(it) },
                                    onLockNow = { viewModel.lockJournal() },
                                    onBack = { viewModel.closeSubScreen() }
                                )
                            }
                        }
                        "mood" -> {
                            MoodTrackerScreen(
                                moodLogs = moodLogs,
                                onLogMood = { mood, note, col -> viewModel.logMood(mood, note, col) },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "people" -> {
                            PeopleScrapbookScreen(
                                people = people,
                                onSelectPerson = { id -> viewModel.openPersonProfile(id) },
                                onAddPerson = { viewModel.openNewPersonProfile() },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "memories" -> {
                            MemoryJarScreen(
                                memories = memories,
                                unfoldedMemory = unfoldedJarMemory,
                                onPickMemory = { viewModel.pickRandomJarMemory() },
                                onPutBack = { viewModel.dismissJarMemory() },
                                onAddMemory = { t, c, m -> viewModel.addMemory(t, c, m) },
                                onOpenVault = { viewModel.openSubScreen("memory_vault") },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "dreams" -> {
                            DreamsDashboardScreen(
                                wishlist = wishlist,
                                capsules = capsules,
                                onSaveWishlistItem = { viewModel.saveWishlistItem(it) },
                                onToggleWishlistAcquired = { viewModel.toggleWishlistAcquired(it) },
                                onDeleteWishlistItem = { viewModel.deleteWishlistItem(it) },
                                onSaveCapsule = { viewModel.saveFutureCapsule(it) },
                                initialSubTab = DreamsSubTab.WISHLIST,
                                isDedicatedPage = false,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "wishlist" -> {
                            DreamsDashboardScreen(
                                wishlist = wishlist,
                                capsules = capsules,
                                onSaveWishlistItem = { viewModel.saveWishlistItem(it) },
                                onToggleWishlistAcquired = { viewModel.toggleWishlistAcquired(it) },
                                onDeleteWishlistItem = { viewModel.deleteWishlistItem(it) },
                                onSaveCapsule = { viewModel.saveFutureCapsule(it) },
                                initialSubTab = DreamsSubTab.WISHLIST,
                                isDedicatedPage = true,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "visionboard" -> {
                            DreamsDashboardScreen(
                                wishlist = wishlist,
                                capsules = capsules,
                                onSaveWishlistItem = { viewModel.saveWishlistItem(it) },
                                onToggleWishlistAcquired = { viewModel.toggleWishlistAcquired(it) },
                                onDeleteWishlistItem = { viewModel.deleteWishlistItem(it) },
                                onSaveCapsule = { viewModel.saveFutureCapsule(it) },
                                initialSubTab = DreamsSubTab.VISION_BOARD,
                                isDedicatedPage = true,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "futureme" -> {
                            DreamsDashboardScreen(
                                wishlist = wishlist,
                                capsules = capsules,
                                onSaveWishlistItem = { viewModel.saveWishlistItem(it) },
                                onToggleWishlistAcquired = { viewModel.toggleWishlistAcquired(it) },
                                onDeleteWishlistItem = { viewModel.deleteWishlistItem(it) },
                                onSaveCapsule = { viewModel.saveFutureCapsule(it) },
                                initialSubTab = DreamsSubTab.FUTURE_ME,
                                isDedicatedPage = true,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "currently" -> {
                            MeSectionScreen(
                                habits = habits,
                                periodLogs = periodLogs,
                                letters = letters,
                                onCompleteHabit = { viewModel.waterHabit(it) },
                                onAddHabit = { t, p -> viewModel.addHabit(t, p) },
                                onLogPeriod = { s, f, sym, n -> viewModel.logPeriod(s, f, sym, n) },
                                onSaveLetter = { viewModel.saveLetter(it) },
                                onOpenDoodle = { viewModel.openSubScreen("doodle") },
                                onOpenBrainDump = { viewModel.openSubScreen("braindump") },
                                onOpenSettings = { viewModel.openSubScreen("settings") },
                                initialSubPage = MeSubPage.CURRENTLY,
                                isDedicatedPage = true,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "gratitude" -> {
                            MeSectionScreen(
                                habits = habits,
                                periodLogs = periodLogs,
                                letters = letters,
                                onCompleteHabit = { viewModel.waterHabit(it) },
                                onAddHabit = { t, p -> viewModel.addHabit(t, p) },
                                onLogPeriod = { s, f, sym, n -> viewModel.logPeriod(s, f, sym, n) },
                                onSaveLetter = { viewModel.saveLetter(it) },
                                onOpenDoodle = { viewModel.openSubScreen("doodle") },
                                onOpenBrainDump = { viewModel.openSubScreen("braindump") },
                                onOpenSettings = { viewModel.openSubScreen("settings") },
                                initialSubPage = MeSubPage.GRATITUDE,
                                isDedicatedPage = true,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "habits" -> {
                            MeSectionScreen(
                                habits = habits,
                                periodLogs = periodLogs,
                                letters = letters,
                                onCompleteHabit = { viewModel.waterHabit(it) },
                                onAddHabit = { t, p -> viewModel.addHabit(t, p) },
                                onLogPeriod = { s, f, sym, n -> viewModel.logPeriod(s, f, sym, n) },
                                onSaveLetter = { viewModel.saveLetter(it) },
                                onOpenDoodle = { viewModel.openSubScreen("doodle") },
                                onOpenBrainDump = { viewModel.openSubScreen("braindump") },
                                onOpenSettings = { viewModel.openSubScreen("settings") },
                                initialSubPage = MeSubPage.HABIT_GARDEN,
                                isDedicatedPage = true,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "letters" -> {
                            MeSectionScreen(
                                habits = habits,
                                periodLogs = periodLogs,
                                letters = letters,
                                onCompleteHabit = { viewModel.waterHabit(it) },
                                onAddHabit = { t, p -> viewModel.addHabit(t, p) },
                                onLogPeriod = { s, f, sym, n -> viewModel.logPeriod(s, f, sym, n) },
                                onSaveLetter = { viewModel.saveLetter(it) },
                                onOpenDoodle = { viewModel.openSubScreen("doodle") },
                                onOpenBrainDump = { viewModel.openSubScreen("braindump") },
                                onOpenSettings = { viewModel.openSubScreen("settings") },
                                initialSubPage = MeSubPage.STATIONERY_LETTERS,
                                isDedicatedPage = true,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        "aboutme" -> {
                            MeSectionScreen(
                                habits = habits,
                                periodLogs = periodLogs,
                                letters = letters,
                                onCompleteHabit = { viewModel.waterHabit(it) },
                                onAddHabit = { t, p -> viewModel.addHabit(t, p) },
                                onLogPeriod = { s, f, sym, n -> viewModel.logPeriod(s, f, sym, n) },
                                onSaveLetter = { viewModel.saveLetter(it) },
                                onOpenDoodle = { viewModel.openSubScreen("doodle") },
                                onOpenBrainDump = { viewModel.openSubScreen("braindump") },
                                onOpenSettings = { viewModel.openSubScreen("settings") },
                                initialSubPage = MeSubPage.PIECES_OF_ME,
                                isDedicatedPage = true,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }
                        else -> {
                            // Primary Tab Screen
                            when (tab) {
                                JournalTab.TODAY -> {
                                    TodayJournalScreen(
                                        currentPrompt = currentPrompt,
                                        dailyCornerNote = dailyCornerNote,
                                        recentMemory = memories.firstOrNull(),
                                        onRollPrompt = { viewModel.rollPromptDice() },
                                        onRotateDailyCorner = { viewModel.rotateDailyCorner() },
                                        onMoodSelected = { mood, color ->
                                            viewModel.logMood(mood, "", color.value.toLong())
                                        },
                                        onQuickCreate = { option ->
                                            when (option) {
                                                CreationOption.TEXT -> viewModel.openNewEntry()
                                                CreationOption.DOODLE -> viewModel.openSubScreen("doodle")
                                                CreationOption.STICKER -> viewModel.openSubScreen("braindump")
                                                CreationOption.PHOTO -> viewModel.openNewPersonProfile()
                                                CreationOption.SONG -> viewModel.setTab(JournalTab.ME)
                                                CreationOption.VOICE -> viewModel.setTab(JournalTab.MEMORIES)
                                            }
                                        },
                                        onWriteJournalEntry = { viewModel.openNewEntry() },
                                        onViewMemory = { mem -> viewModel.openSubScreen("memory_vault") }
                                    )
                                }
                                JournalTab.PAGES -> {
                                    val hiddenChapters = remember(userSettings?.enabledSections) {
                                        val enabled = userSettings?.enabledSections?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()
                                        if (!enabled.contains("period")) setOf("period") else emptySet()
                                    }
                                    JournalEntriesScreen(
                                        entries = entries,
                                        onSelectEntry = { viewModel.openEditEntry(it) },
                                        onCreateNewEntry = { viewModel.openNewEntry() },
                                        onDeleteEntry = { viewModel.deleteJournalEntry(it) },
                                        hiddenChapters = hiddenChapters,
                                        onNavigateChapter = { chapter ->
                                            when (chapter) {
                                                "journal" -> viewModel.openNewEntry()
                                                "period" -> viewModel.openSubScreen("period_tracker")
                                                else -> viewModel.openSubScreen(chapter)
                                            }
                                        }
                                    )
                                }
                                JournalTab.MOOD -> {
                                    MoodTrackerScreen(
                                        moodLogs = moodLogs,
                                        onLogMood = { mood, note, col -> viewModel.logMood(mood, note, col) }
                                    )
                                }
                                JournalTab.PEOPLE -> {
                                    PeopleScrapbookScreen(
                                        people = people,
                                        onSelectPerson = { id -> viewModel.openPersonProfile(id) },
                                        onAddPerson = { viewModel.openNewPersonProfile() }
                                    )
                                }
                                JournalTab.MEMORIES -> {
                                    MemoryJarScreen(
                                        memories = memories,
                                        unfoldedMemory = unfoldedJarMemory,
                                        onPickMemory = { viewModel.pickRandomJarMemory() },
                                        onPutBack = { viewModel.dismissJarMemory() },
                                        onAddMemory = { t, c, m -> viewModel.addMemory(t, c, m) },
                                        onOpenVault = { viewModel.openSubScreen("memory_vault") }
                                    )
                                }
                                JournalTab.DREAMS -> {
                                    DreamsDashboardScreen(
                                        wishlist = wishlist,
                                        capsules = capsules,
                                        onSaveWishlistItem = { viewModel.saveWishlistItem(it) },
                                        onToggleWishlistAcquired = { viewModel.toggleWishlistAcquired(it) },
                                        onDeleteWishlistItem = { viewModel.deleteWishlistItem(it) },
                                        onSaveCapsule = { viewModel.saveFutureCapsule(it) }
                                    )
                                }
                                JournalTab.ME -> {
                                    MeSectionScreen(
                                        habits = habits,
                                        periodLogs = periodLogs,
                                        letters = letters,
                                        onCompleteHabit = { viewModel.waterHabit(it) },
                                        onAddHabit = { t, p -> viewModel.addHabit(t, p) },
                                        onLogPeriod = { s, f, sym, n -> viewModel.logPeriod(s, f, sym, n) },
                                        onSaveLetter = { viewModel.saveLetter(it) },
                                        onOpenDoodle = { viewModel.openSubScreen("doodle") },
                                        onOpenBrainDump = { viewModel.openSubScreen("braindump") },
                                        onOpenSettings = { viewModel.openSubScreen("settings") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Scrapbook Navigation Bar
        if (activeSubScreen == null) {
            ScrapbookBottomNavBar(
                selectedTabName = when (currentTab) {
                    JournalTab.TODAY -> "today"
                    JournalTab.PAGES -> "pages"
                    JournalTab.MEMORIES -> "collections"
                    JournalTab.ME -> "me"
                    else -> ""
                },
                onTabSelect = { tabKey ->
                    when (tabKey) {
                        "today" -> viewModel.setTab(JournalTab.TODAY)
                        "pages" -> viewModel.setTab(JournalTab.PAGES)
                        "collections" -> viewModel.setTab(JournalTab.MEMORIES)
                        "me" -> viewModel.setTab(JournalTab.ME)
                    }
                },
                onActionClick = {
                    viewModel.openNewEntry()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            )
        }

        // Unfolding Paper Scraps Menu (Floating Scrap FAB in bottom corner)
        if (activeSubScreen == null) {
            UniversalCreationScraps(
                onOptionSelected = { option ->
                    when (option) {
                        CreationOption.TEXT -> viewModel.openNewEntry()
                        CreationOption.DOODLE -> viewModel.openSubScreen("doodle")
                        CreationOption.STICKER -> viewModel.openSubScreen("braindump")
                        CreationOption.PHOTO -> viewModel.openNewPersonProfile()
                        CreationOption.SONG -> viewModel.setTab(JournalTab.ME)
                        CreationOption.VOICE -> viewModel.setTab(JournalTab.MEMORIES)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 68.dp)
            )
        }

        // Bookmark Ribbon Search Dialog
        if (showSearchDialog) {
            SearchRibbonDialog(
                entries = entries,
                memories = memories,
                people = people,
                notes = notes,
                wishlist = wishlist,
                onSelectEntry = { viewModel.openEditEntry(it) },
                onSelectPerson = { viewModel.openPersonProfile(it) },
                onSelectMemory = { viewModel.openSubScreen("memory_vault") },
                onSelectNote = { viewModel.openSubScreen("braindump") },
                onSelectWishlist = { viewModel.openSubScreen("wishlist") },
                onDismiss = { showSearchDialog = false }
            )
        }

        // Table of Contents / Index Dialog
        if (showIndexDialog) {
            JournalIndexDialog(
                onDismiss = { showIndexDialog = false },
                onNavigate = { key ->
                    when (key) {
                        "today" -> viewModel.setTab(JournalTab.TODAY)
                        "pages" -> viewModel.setTab(JournalTab.PAGES)
                        "mood" -> viewModel.openSubScreen("mood")
                        "doodle" -> viewModel.openSubScreen("doodle")
                        "braindump" -> viewModel.openSubScreen("braindump")
                        "people" -> viewModel.openSubScreen("people")
                        "memoryjar" -> viewModel.openSubScreen("memories")
                        "memoryvault" -> viewModel.openSubScreen("memory_vault")
                        "dreams" -> viewModel.openSubScreen("dreams")
                        "futureme" -> viewModel.openSubScreen("futureme")
                        "wishlist" -> viewModel.openSubScreen("wishlist")
                        "visionboard" -> viewModel.openSubScreen("visionboard")
                        "habits" -> viewModel.openSubScreen("habits")
                        "currently" -> viewModel.openSubScreen("currently")
                        "gratitude" -> viewModel.openSubScreen("gratitude")
                        "period" -> viewModel.openSubScreen("period_tracker")
                        "letters" -> viewModel.openSubScreen("letters")
                        "aboutme" -> viewModel.openSubScreen("aboutme")
                        "settings" -> viewModel.openSubScreen("settings")
                    }
                }
            )
        }
    }
}

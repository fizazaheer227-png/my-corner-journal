package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.ui.components.JournalTab
import com.example.viewmodel.JournalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class JournalNavigationAndUnlockTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var app: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        app = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testUnlockNavigationAndSessionFlow() = runTest(testDispatcher) {
        val viewModel = JournalViewModel(app)
        advanceUntilIdle()

        // Setup user settings with passcode "1234"
        viewModel.completeOnboarding(
            name = "Fiza",
            passcode = "1234",
            themeName = "Vibrant Palette",
            coverQuote = "every little moment counts.",
            handwritingStyle = "Cursive",
            handwritingSampleUri = null,
            enabledSections = "journal,mood,doodle"
        )
        advanceUntilIdle()

        // 1. Launch app / post-onboarding: journal is locked, cover is closed
        assertTrue(viewModel.isLocked.value)
        assertFalse(viewModel.isJournalOpen.value)

        // 2 & 3. Enter an intentionally WRONG passcode -> access must be denied
        val wrongUnlock = viewModel.unlockJournalWithPasscode("9999")
        assertFalse(wrongUnlock)
        assertTrue(viewModel.isLocked.value)
        assertFalse(viewModel.isJournalOpen.value)

        // 4. Enter the CORRECT passcode -> access granted
        val correctUnlock = viewModel.unlockJournalWithPasscode("1234")
        assertTrue(correctUnlock)
        assertFalse(viewModel.isLocked.value) // Set to UNLOCKED for current session

        // 5 & 6. User reaches "with love" page and taps "turn to today"
        viewModel.openJournal()

        // 7. Today page MUST open: isJournalOpen is true, currentTab is TODAY, isLocked is false
        assertTrue(viewModel.isJournalOpen.value)
        assertFalse(viewModel.isLocked.value)
        assertEquals(JournalTab.TODAY, viewModel.currentTab.value)

        // 8. Navigate to another journal section (e.g. PAGES, MEMORIES, DREAMS, ME)
        viewModel.setTab(JournalTab.PAGES)
        advanceUntilIdle()
        assertEquals(JournalTab.PAGES, viewModel.currentTab.value)
        assertFalse(viewModel.isLocked.value)
        assertTrue(viewModel.isJournalOpen.value)

        viewModel.setTab(JournalTab.MEMORIES)
        advanceUntilIdle()
        assertEquals(JournalTab.MEMORIES, viewModel.currentTab.value)
        assertFalse(viewModel.isLocked.value)
        assertTrue(viewModel.isJournalOpen.value)

        // Subscreen navigation (e.g. settings, editor)
        viewModel.openSubScreen("settings")
        assertEquals("settings", viewModel.activeSubScreen.value)
        assertFalse(viewModel.isLocked.value)

        viewModel.closeSubScreen()
        assertEquals(null, viewModel.activeSubScreen.value)
        assertFalse(viewModel.isLocked.value)

        // 9. Return to Today
        viewModel.setTab(JournalTab.TODAY)
        advanceUntilIdle()
        assertEquals(JournalTab.TODAY, viewModel.currentTab.value)

        // 10. Passcode must NOT be requested again (remains unlocked)
        assertFalse(viewModel.isLocked.value)
        assertTrue(viewModel.isJournalOpen.value)

        // 11. Manually lock the journal
        viewModel.lockJournal()
        advanceUntilIdle()

        // 12 & 13. Try opening again -> Passcode MUST now be requested
        assertTrue(viewModel.isLocked.value)
        assertFalse(viewModel.isJournalOpen.value)
    }

    @Test
    fun testReopenAppAfterOnboarding() = runTest(testDispatcher) {
        val vm1 = JournalViewModel(app)
        advanceUntilIdle()
        vm1.completeOnboarding("TestUser", "1234", "Vibrant Palette", "Quote", "Cursive", null, "journal")
        advanceUntilIdle()

        val vm2 = JournalViewModel(app)
        advanceUntilIdle()
        val direct = AppDatabase.getInstance(app).userSettingsDao().getUserSettingsDirect()
        println("DEBUG DIRECT SETTINGS: $direct")
        println("DEBUG VM2 USER SETTINGS: ${vm2.userSettings.value}")
        assertTrue("Settings should be loaded", vm2.isSettingsLoaded.value)
        assertEquals(true, vm2.userSettings.value?.isOnboardingCompleted)
        assertTrue("Should be locked on reopen", vm2.isLocked.value)
        assertFalse("Journal should not be open", vm2.isJournalOpen.value)
    }
}

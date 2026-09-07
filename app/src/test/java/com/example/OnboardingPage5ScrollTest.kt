package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.example.ui.screens.onboarding.OnboardingScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OnboardingPage5ScrollTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPage5StructureAndLazyColumnScrolling() {
        var completed = false

        composeTestRule.setContent {
            OnboardingScreen(
                onComplete = { _, _, _, _, _, _, _ ->
                    completed = true
                }
            )
        }

        // Navigate from Page 1 to Page 5
        // Page 1: Type name and click next
        composeTestRule.onNodeWithTag("onboarding_name_input").performTextInput("Fiza")
        composeTestRule.onNodeWithTag("onboarding_next_button").performClick()

        // Page 2: Enter passcode 1234 and confirm 1234
        composeTestRule.onNodeWithTag("onboarding_passcode_input").performTextInput("1234")
        composeTestRule.onNodeWithTag("onboarding_passcode_confirm").performTextInput("1234")
        composeTestRule.onNodeWithTag("onboarding_next_button").performClick()

        // Page 3: Theme selection -> click next
        composeTestRule.onNodeWithTag("onboarding_next_button").performClick()

        // Page 4: Handwriting selection -> click next
        composeTestRule.onNodeWithTag("onboarding_next_button").performClick()

        // Now on Page 5: "what belongs here?"
        composeTestRule.onNodeWithText("what belongs here?").assertIsDisplayed()

        // Verify LazyColumn is displayed
        val lazyColumnNode = composeTestRule.onNodeWithTag("onboarding_sections_lazy_column")
        lazyColumnNode.assertIsDisplayed()

        // First item "Everyday Journal" is visible
        composeTestRule.onNodeWithText("Everyday Journal").assertIsDisplayed()

        // Scroll to the 15th item "Pieces of Me" to verify full vertical scrolling reachability
        lazyColumnNode.performScrollToNode(androidx.compose.ui.test.hasText("Pieces of Me"))
        composeTestRule.onNodeWithText("Pieces of Me").assertIsDisplayed()

        // Fixed bottom "open my corner ♡" button is displayed and enabled
        val finishButton = composeTestRule.onNodeWithTag("onboarding_finish_button")
        finishButton.assertIsDisplayed()
        finishButton.assertIsEnabled()

        // Click finish button
        finishButton.performClick()
        assertTrue("Onboarding should be completed after clicking finish button", completed)
    }
}

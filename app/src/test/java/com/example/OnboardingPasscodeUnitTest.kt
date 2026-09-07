package com.example

import com.example.viewmodel.JournalViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class OnboardingPasscodeUnitTest {

    @Test
    fun testPasscodeHashing() {
        val pin = "1234"
        val hash1 = JournalViewModel.hashPasscode(pin)
        val hash2 = JournalViewModel.hashPasscode(pin)
        assertEquals(hash1, hash2)
        assertNotEquals(pin, hash1)
        assertTrue(hash1.isNotEmpty())
    }

    @Test
    fun testDifferentPasscodesProduceDifferentHashes() {
        val hash1 = JournalViewModel.hashPasscode("1234")
        val hash2 = JournalViewModel.hashPasscode("1235")
        val hash3 = JournalViewModel.hashPasscode("123456")
        assertNotEquals(hash1, hash2)
        assertNotEquals(hash1, hash3)
    }

    @Test
    fun testPasscodeLengthValidation() {
        fun isValidPasscode(pass: String, confirm: String): Boolean {
            return pass.length in 4..6 && pass.all { it.isDigit() } && pass == confirm
        }

        assertFalse(isValidPasscode("", ""))
        assertFalse(isValidPasscode("123", "123"))
        assertTrue(isValidPasscode("1234", "1234"))
        assertTrue(isValidPasscode("12345", "12345"))
        assertTrue(isValidPasscode("123456", "123456"))
        assertFalse(isValidPasscode("1234567", "1234567"))
        assertFalse(isValidPasscode("1234", "4321"))
    }
}

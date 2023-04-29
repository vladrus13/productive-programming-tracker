package ru.productive.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LevenshteinTest {

    @Test
    fun testEqualsStrings() {
        val expectedDist = 0
        val actualDist = LevenshteinDistance.calculate("abc", "abc")
        assertEquals(expectedDist, actualDist)
    }

    @Test
    fun testOneWordStrings() {
        val expectedDist = 1
        val actualDist = LevenshteinDistance.calculate("Lukonin", "Lukonina")
        assertEquals(expectedDist, actualDist)
    }

    @Test
    fun testMultiwordStrings() {
        val expectedDist = 7
        val actualDist = LevenshteinDistance.calculate("Skazhenik Taras", "Skazhenik Ekaterina")
        assertEquals(expectedDist, actualDist)
    }

    @Test
    fun testMultiwordRussianStrings() {
        val expectedDist = 1
        val actualDist = LevenshteinDistance.calculate("Петров Петр Сергеевич", "Петров Петя Сергеевич")
        assertEquals(expectedDist, actualDist)
    }
}
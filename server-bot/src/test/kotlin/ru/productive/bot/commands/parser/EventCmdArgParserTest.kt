package ru.productive.bot.commands.parser

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventCmdArgParserTest {

    @Test
    fun testParseAddEventTitle_NullArg() {
        val result = parseAddEventTitle(null)
        Assertions.assertTrue(result.isFailure)
    }

    @Test
    fun testParseAddEventTitle_OkArg() {
        val title = "Event title"
        val result = parseAddEventTitle("/addEvent $title")

        assertEquals(title, result.getOrNull())
    }
}
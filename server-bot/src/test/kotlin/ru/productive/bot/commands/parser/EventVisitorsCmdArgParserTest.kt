package ru.productive.bot.commands.parser;

import org.junit.jupiter.api.Test;
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EventVisitorsCmdArgParserTest {

    @Test
    fun testParseGetEventVisitorEventId_IncorrectNumber() {
        val result = parseGetEventVisitorEventId("/getEventVisitor 1234i")

        assertTrue(result.isFailure)
        assertIs<NumberFormatException>(result.exceptionOrNull())
    }

    @Test
    fun testParseGetEventVisitorEventId_OkId() {
        val eventId: Long = 123
        val result = parseGetEventVisitorEventId("/getEventVisitor $eventId")

        assertEquals(eventId, result.getOrNull())
    }

    @Test
    fun testParseAddEventVisitorArguments_MissingFullName() {
        val result = parseAddEventVisitorArguments("/addEventVisitor 123")

        assertTrue(result.isFailure)
        assertIs<IllegalArgumentException>(result.exceptionOrNull())
    }

    @Test
    fun testParseAddEventVisitorArguments_IncorrectId() {
        val result = parseAddEventVisitorArguments("/addEventVisitor 12.3 Ivanov Ivan")

        assertTrue(result.isFailure)
        assertIs<NumberFormatException>(result.exceptionOrNull())
    }

    @Test
    fun testParseAddEventVisitorArguments_OkArgs() {
        val eventId: Long = 123
        val fullName = "Ivanov Ivan Ivanovich"
        val result = parseAddEventVisitorArguments("/addEventVisitor $eventId $fullName")

        val args = result.getOrNull()
        assertNotNull(args)
        assertEquals(eventId, args.eventId)
        assertEquals(fullName, args.fullName)
    }
}

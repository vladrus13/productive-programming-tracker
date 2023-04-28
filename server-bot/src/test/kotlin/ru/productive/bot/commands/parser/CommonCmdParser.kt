package ru.productive.bot.commands.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class CommonCmdParser {

    @Test
    fun testParseNotNullMessage_NullCase() {
        val result = parseNotNullMessage(null)
        assertTrue(result.isFailure)
    }

    @Test
    fun testParseNotNullMessage_OkCase() {
        val text = "/cmd a b c"
        val result = parseNotNullMessage(text)
        assertEquals(text, result.getOrNull())
    }

    @Test
    fun textParseArgumentsWithoutCmd_NullCase() {
        val result = parseArgumentsWithoutCmd(null)
        assertTrue(result.isFailure)
    }

    @Test
    fun textParseArgumentsWithoutCmd_ZeroArguments() {
        val result = parseArgumentsWithoutCmd("/cmd")
        assertTrue(result.isFailure)
    }

    @Test
    fun textParseArgumentsWithoutCmd_ManyArguments() {
        val expectedArgs = listOf("a", "abc", "arg_arg", "argArg123")
        val result = parseArgumentsWithoutCmd("/cmd ${expectedArgs.joinToString(" ")}")

        val actualArgs = result.getOrNull()
        assertNotNull(actualArgs)
        assertEquals(expectedArgs.size, actualArgs.size)
        assertEquals(expectedArgs, actualArgs)
    }
}
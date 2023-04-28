package ru.productive.bot.commands.parser

fun parseGetEventVisitorEventId(text: String?): Result<Long> {
    return parseArgumentsWithoutCmd(text).mapCatching { rawArg ->
        return@mapCatching rawArg[0].toLong()
    }
}


data class EventVisitorsArguments(val eventId: Long, val fullName: String)

fun parseAddEventVisitorArguments(text: String?): Result<EventVisitorsArguments> {
    return parseArgumentsWithoutCmd(text).mapCatching { rawArgs ->
        val eventId = rawArgs[0].toLong()

        if (rawArgs.size < 2) {
            throw IllegalArgumentException("Missing full name")
        }
        val fullName = rawArgs.drop(1).joinToString(" ")

        return@mapCatching EventVisitorsArguments(eventId, fullName)
    }
}
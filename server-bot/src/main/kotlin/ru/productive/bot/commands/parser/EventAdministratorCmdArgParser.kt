package ru.productive.bot.commands.parser

import com.github.kotlintelegrambot.entities.Message

data class EventAdministratorArguments(val eventId: Long, val userName: String)

fun parseEventAdministratorArguments(message: Message) : Result<EventAdministratorArguments> {
    return parseArgumentsWithoutCmd(message.text).mapCatching { rawArgs ->
        val eventId = rawArgs[0].toLong()

        if (rawArgs.size < 2) {
            throw IllegalArgumentException("Missing username")
        }
        if (!rawArgs[1].startsWith("@") || (rawArgs[1].length < 2)) {
            throw IllegalArgumentException("Incorrect username, use @username")
        }

        return@mapCatching EventAdministratorArguments(eventId, rawArgs[1].drop(1))
    }
}
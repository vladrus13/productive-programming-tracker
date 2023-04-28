package ru.productive.bot.commands.parser

fun parseAddEventTitle(text: String?): Result<String> {
    return parseArgumentsWithoutCmd(text).mapCatching { rawArgs ->
        return@mapCatching rawArgs.joinToString(ARGUMENT_SEPARATOR)
    }
}
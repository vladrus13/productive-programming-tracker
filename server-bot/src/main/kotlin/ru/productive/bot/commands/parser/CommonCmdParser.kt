package ru.productive.bot.commands.parser

fun parseNotNullMessage(text: String?): Result<String> {
    if (text == null) {
        return Result.failure(NullPointerException("Missing arguments"))
    } else {
        return Result.success(text)
    }
}

fun parseArgumentsWithoutCmd(text: String?): Result<List<String>> {
    return parseNotNullMessage(text).mapCatching { args ->
        val arguments = args.split(" ")
        if (arguments.size < 2) {
            throw IllegalArgumentException("Few arguments")
        }

        return@mapCatching arguments.drop(1)
    }
}
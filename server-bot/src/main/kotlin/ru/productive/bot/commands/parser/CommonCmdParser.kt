package ru.productive.bot.commands.parser

const val ARGUMENT_SEPARATOR = " "

fun parseNotNullMessage(text: String?): Result<String> {
    return if (text == null) {
        Result.failure(NullPointerException("Missing arguments"))
    } else {
        Result.success(text)
    }
}

fun parseArgumentsWithoutCmd(text: String?): Result<List<String>> {
    return parseNotNullMessage(text).mapCatching { args ->
        val arguments = args.split(ARGUMENT_SEPARATOR)
        if (arguments.size < 2) {
            throw IllegalArgumentException("Few arguments")
        }

        return@mapCatching arguments.drop(1)
    }
}

fun parseIdArg(text: String?): Result<Long> {
    return parseArgumentsWithoutCmd(text).mapCatching { args ->
        if (args.size != 1) {
            throw IllegalArgumentException("One argument is expected")
        }

        return@mapCatching args[0].toLong()
    }
}
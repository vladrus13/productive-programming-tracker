package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import io.ktor.client.call.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import model.TextResponse
import ru.productive.api.client.ApiClient
import ru.productive.bot.botLogger
import ru.productive.bot.commands.parser.parseEventAdministratorArguments
import ru.productive.bot.replyToMessage
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addFailAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

fun Dispatcher.addEventAdministrator(apiClient: ApiClient) {
    command("addEventAdministrator") {
        runBlocking {
            botLogger.addUserMessage("addEventAdministrator", message)
            parseEventAdministratorArguments(message)
                .onSuccess { (eventId, userName) ->
                    val response: HttpResponse = apiClient.addEventAdministrator(eventId, userName)
                    val textResponse = try {
                        response.body<TextResponse>().message
                    } catch (
                        e: NoTransformationFoundException
                    ) {
                        response.bodyAsText()
                    }
                    botLogger.addAnswer("addEventAdministrator", message, textResponse)
                    bot.replyToMessage(message, text = textResponse)
                }.onFailure { e ->
                    botLogger.addFailAnswer("addEventAdministrator", message, e.stackTrace.joinToString(separator = "\n"))
                    bot.replyToMessage(message, text = e.message ?: "Error")
                }
        }
    }
}
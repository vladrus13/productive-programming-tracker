package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import ru.productive.api.client.ApiClient
import ru.productive.bot.botLogger
import ru.productive.bot.commands.parser.parseAddEventVisitorArguments
import ru.productive.bot.commands.parser.parseGetEventVisitorEventId
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addFailAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

fun Dispatcher.getEventVisitors(apiClient: ApiClient) {
    command("getEventVisitors") {
        runBlocking {
            botLogger.addUserMessage("getEventVisitors", message)
            parseGetEventVisitorEventId(message.text)
                .onSuccess { eventId ->
                    val response: HttpResponse = apiClient.getVisitors(eventId)
                    val textResponse = response.bodyAsText()
                    botLogger.fine("Answer to ${message.from?.id}: $textResponse")
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = textResponse)
                }
                .onFailure { e ->
                    botLogger.addFailAnswer("addEvent", message, e.stackTrace.toString())
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
                }
        }
    }
}

fun Dispatcher.addEventVisitor(apiClient: ApiClient) {

    command("addEventVisitor") {
        runBlocking {
            botLogger.addUserMessage("addEventVisitors", message)
            parseAddEventVisitorArguments(message.text)
                .onSuccess { (eventId, fullName) ->
                    val response: HttpResponse = apiClient.addVisitor(eventId, fullName)
                    val textResponse = response.bodyAsText()
                    botLogger.addAnswer("addEventVisitors", message, textResponse)
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = textResponse)
                }
                .onFailure { e ->
                    botLogger.addFailAnswer("addEventVisitors", message, e.stackTrace.toString())
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
                }
        }
    }
}
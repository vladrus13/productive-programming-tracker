package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import io.ktor.client.call.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import model.TextResponse
import ru.productive.api.client.ApiClient
import ru.productive.bot.botLogger
import ru.productive.bot.commands.parser.parseAddEventTitle
import ru.productive.bot.replyToMessage
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addFailAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

fun Dispatcher.addEvent(apiClient: ApiClient) {
    command("addEvent") {
        runBlocking {
            botLogger.addUserMessage("addEvent", message)
            parseAddEventTitle(message.text)
                .onSuccess { title ->
                    val response: HttpResponse = apiClient.addEvent(title, message.chat.username!!)
                    val textResponse = try {
                        response.body<TextResponse>().message
                    } catch (
                        e: NoTransformationFoundException
                    ) {
                        response.bodyAsText()
                    }
                    botLogger.addAnswer("addEvent", message, textResponse)
                    bot.replyToMessage(message, text = textResponse)
                }.onFailure { e ->
                    botLogger.addFailAnswer("addEvent", message, e.stackTrace.joinToString(separator = "\n"))
                    bot.replyToMessage(message, text = e.message ?: "Error")
                }
        }
    }
}

fun Dispatcher.getEvents(apiClient: ApiClient) {
    command("getEvents") {
        runBlocking {
            botLogger.addUserMessage("getEvents", message)
            val resultText = try {
                val events = apiClient.getEvents(message.chat.username!!)
                botLogger.addAnswer("getEvents", message, "${events.size} Events")
                events.joinToString(separator = "\n") { event ->
                    "Event '${event.title}' with id ${event.id};"
                }
            } catch (e: ApiClient.BadResponseStatusException) {
                e.response.message.also { botLogger.addFailAnswer("getEvents", message, it) }
            }
            bot.replyToMessage(message, text = resultText)
        }
    }
}
package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import ru.productive.api.client.ApiClient
import ru.productive.bot.botLogger
import ru.productive.bot.commands.parser.parseAddEventTitle
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addFailAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

fun Dispatcher.addEvent(apiClient: ApiClient) {
    command("addEvent") {
        runBlocking {
            botLogger.addUserMessage("addEvent", message)
            parseAddEventTitle(message.text)
                .onSuccess { title ->
                    val response: HttpResponse = apiClient.addEvent(title)
                    val textResponse = response.bodyAsText()
                    botLogger.addAnswer("addEvent", message, textResponse)
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = textResponse)
                }.onFailure { e ->
                    botLogger.addFailAnswer("addEvent", message, e.stackTrace.toString())
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
                }
        }
    }
}
package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import ru.productive.api.client.ApiClient
import ru.productive.bot.commands.parser.parseAddEventVisitorArguments
import ru.productive.bot.commands.parser.parseGetEventVisitorEventId

fun Dispatcher.getEventVisitors(apiClient: ApiClient) {
    command("getEventVisitors") {
        runBlocking {
            parseGetEventVisitorEventId(message.text)
                .onSuccess { eventId ->
                    val response: HttpResponse = apiClient.getVisitors(eventId)
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = response.bodyAsText())
                }
                .onFailure { e ->
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
                }
        }
    }
}

fun Dispatcher.addEventVisitor(apiClient: ApiClient) {

    command("addEventVisitor") {
        runBlocking {
            parseAddEventVisitorArguments(message.text)
                .onSuccess { (eventId, fullName) ->
                    val response: HttpResponse = apiClient.addVisitor(eventId, fullName)
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = response.bodyAsText())
                }
                .onFailure { e ->
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
                }
        }
    }
}
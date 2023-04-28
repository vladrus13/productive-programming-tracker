package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import ru.productive.api.client.ApiClient

fun Dispatcher.getEventVisitors(apiClient: ApiClient) {
    fun messageToEventId(messageText: String?): Long {
        // TODO: avoid exception
        return messageText?.split(" ")?.drop(1)?.firstOrNull()?.toLong()
            ?: throw NoSuchElementException("Missing id")
    }

    command("getEventVisitors") {
        runBlocking {
            val response: HttpResponse = apiClient.getVisitors(messageToEventId(message.text))
            bot.sendMessage(ChatId.fromId(message.chat.id), text = response.bodyAsText())
        }
    }
}

fun Dispatcher.addEventVisitor(apiClient: ApiClient) {
    // TODO: replace with more generic type
    fun messageToParamsPair(messageText: String?): Pair<Long, String> {
        // TODO: avoid exception
        val params = messageText?.split(" ")?.drop(1)?.toList()
        if (params == null || params.size != 2) {
            throw IllegalArgumentException("Incorrect params")
        }
        return params[0].toLong() to params[1]
    }

    command("addEventVisitor") {
        runBlocking {
            val (eventId, fullName) = messageToParamsPair(message.text)
            val response: HttpResponse = apiClient.addVisitor(eventId, fullName)
            bot.sendMessage(ChatId.fromId(message.chat.id), text = response.bodyAsText())
        }
    }
}
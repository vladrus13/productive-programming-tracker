package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import ru.productive.api.client.ApiClient
import ru.productive.bot.commands.parser.parseAddEventTitle

fun Dispatcher.addEvent(apiClient: ApiClient) {
    command("addEvent") {
        runBlocking {
            parseAddEventTitle(message.text)
                .onSuccess { title ->
                    val response: HttpResponse = apiClient.addEvent(title)
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = response.bodyAsText())
                }.onFailure { e ->
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
                }
        }
    }
}
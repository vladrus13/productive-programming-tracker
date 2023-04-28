package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import ru.productive.api.client.ApiClient

fun Dispatcher.addEvent(apiClient: ApiClient) {
    command("addEvent") {
        runBlocking {
            val response: HttpResponse = apiClient.addEvent(message.text ?: "DEFAULT_EVENT")
            bot.sendMessage(ChatId.fromId(message.chat.id), text = response.bodyAsText())
        }
    }
}
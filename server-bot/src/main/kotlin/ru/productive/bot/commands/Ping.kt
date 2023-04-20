package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId

fun Dispatcher.ping() {
  command("ping") {
    bot.sendMessage(ChatId.fromId(message.chat.id), text = "PONG!")
  }
}
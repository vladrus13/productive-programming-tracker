package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.coroutines.runBlocking
import ru.productive.bot.botLogger
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

fun Dispatcher.ping() {
  command("ping") {
    runBlocking {
      botLogger.addUserMessage("ping", message)
      botLogger.addAnswer("addEvent", message, "PONG!")
      bot.sendMessage(ChatId.fromId(message.chat.id), text = "PONG!")
    }
  }
}
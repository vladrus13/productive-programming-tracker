package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import kotlinx.coroutines.runBlocking
import ru.productive.bot.botLogger
import ru.productive.bot.replyToMessage
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

fun Dispatcher.ping() {
  command("ping") {
    runBlocking {
      botLogger.addUserMessage("ping", message)
      botLogger.addAnswer("ping", message, "PONG!")
      bot.replyToMessage(message, text = "PONG!")
    }
  }
}
package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter
import kotlinx.coroutines.runBlocking
import ru.productive.bot.botLogger
import ru.productive.utils.LevenshteinDistance
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

class LevenshteinDistanceFilter : Filter {
  override fun Message.predicate(): Boolean =
    this.text?.startsWith("levenshtein") == true
}

fun Dispatcher.levenshteinDistance() {
  message(LevenshteinDistanceFilter()) {
    runBlocking {
      botLogger.addUserMessage("levenshtein", message)
      val messageText = message.text ?: return@runBlocking
      val rows = messageText.split("\n")
      val text = if (rows.size == 3) {
        LevenshteinDistance.calculate(rows[1].split(" "), rows[2].split(" ")).toString()
      } else {
        "Please, enter valid names"
      }
      botLogger.addAnswer("addEvent", message, text)
      bot.sendMessage(
        ChatId.fromId(message.chat.id),
        text = text
      )
    }
  }
}
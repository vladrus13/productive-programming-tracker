package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter
import ru.productive.utils.LevenshteinDistance

class LevenshteinDistanceFilter : Filter {
  override fun Message.predicate(): Boolean =
    this.text?.startsWith("levenshtein") == true
}

fun Dispatcher.levenshteinDistance() {
  message(LevenshteinDistanceFilter()) {
    val messageText = this.message.text ?: return@message
    val rows = messageText.split("\n")
    val text = if (rows.size == 3) {
      LevenshteinDistance.calculate(rows[1].split(" "), rows[2].split(" ")).toString()
    } else {
      "Please, enter valid names"
    }
    bot.sendMessage(
      ChatId.fromId(message.chat.id),
      text = text
    )
  }
}
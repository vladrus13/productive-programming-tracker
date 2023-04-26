package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter
import ru.productive.database.MockDatabase
import ru.productive.utils.LevenshteinDistance

class GetStudentFilter : Filter {
  override fun Message.predicate(): Boolean = text?.startsWith("get ") == true

}

fun Dispatcher.getStudent() {
  message(GetStudentFilter()) {
    val student = this.message.text?.split(" ")?.drop(1) ?: return@message
    val text = MockDatabase().getUsers()
      .sortedBy { LevenshteinDistance.calculate(it.split(" "), student) }
      .take(5)
      .joinToString(separator = "\n")
    bot.sendMessage(
      ChatId.fromId(this.message.chat.id),
      text = text
    )
  }
}
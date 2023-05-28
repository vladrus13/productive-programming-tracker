package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter
import kotlinx.coroutines.runBlocking
import ru.productive.bot.botLogger
import ru.productive.bot.replyToMessage
import ru.productive.database.MockDatabase
import ru.productive.utils.LevenshteinDistance
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

class GetStudentFilter : Filter {
  override fun Message.predicate(): Boolean = text?.startsWith("get ") == true

}

fun Dispatcher.getStudent() {
  message(GetStudentFilter()) {
    runBlocking {
      botLogger.addUserMessage("getStudent", message)
      val student = message.text?.split(" ")?.drop(1) ?: return@runBlocking
      val text = MockDatabase().getUsers()
        .sortedBy { LevenshteinDistance.calculate(it.split(" "), student) }
        .take(5)
        .joinToString(separator = "\n")
      botLogger.addAnswer("getStudent", message, text)
      bot.replyToMessage(
        message,
        text = text
      )
    }
  }
}
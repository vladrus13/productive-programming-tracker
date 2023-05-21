package ru.productive.bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.types.TelegramBotResult
import ru.productive.api.client.ApiClient
import ru.productive.bot.commands.*
import ru.productive.config.BotConfig
import java.util.logging.Logger

val Bot = bot {
  token = BotConfig.TOKEN

  val apiClient = ApiClient(BotConfig.SERVER_API_URL)

  dispatch {
    ping()
    addEvent(apiClient)
    getEvents(apiClient)
    addEventAdministrator(apiClient)
    getEventVisitors(apiClient)
    addEventVisitor(apiClient)
    markAsMissed(apiClient)
    markAsVisited(apiClient)

    levenshteinDistance()
    getStudent()
  }
}

val botLogger = Logger.getLogger(Bot.javaClass.name)

fun Bot.replyToMessage(
  messageFrom: Message,
  text: String,
  parseMode: ParseMode? = null,
  disableWebPagePreview: Boolean? = null,
  disableNotification: Boolean? = null,
  replyToMessageId: Long? = null,
  allowSendingWithoutReply: Boolean? = null,
  replyMarkup: ReplyMarkup? = null
): TelegramBotResult<Message> = sendMessage(
  ChatId.fromId(messageFrom.chat.id),
  text,
  parseMode,
  disableWebPagePreview,
  disableNotification,
  replyToMessageId,
  allowSendingWithoutReply,
  replyMarkup
)
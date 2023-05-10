package ru.productive.utils

import com.github.kotlintelegrambot.entities.Message
import java.util.logging.Logger

class LoggerUtils {
  companion object {
    fun Logger.addUserMessage(method: String, message: Message) =
      fine("Got $method message from ${message.from?.id}: ${message.text}")

    fun Logger.addAnswer(method: String, message: Message, response: String) =
      fine("Answer method $method to ${message.from?.id} with text ${message.text}: $response")

    fun Logger.addFailAnswer(method: String, message: Message, response: String) =
      severe("Failed to answer method $method to ${message.from?.id} with text ${message.text}: $response")
  }
}
package ru.productive.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import ru.productive.api.client.ApiClient
import ru.productive.bot.commands.*
import ru.productive.config.BotConfig

val Bot = bot {
  token = BotConfig.TOKEN

  val apiClient = ApiClient(BotConfig.SERVER_API_URL)

  dispatch {
    ping()
    addEvent(apiClient)
    getEventVisitors(apiClient)
    addEventVisitor(apiClient)
    levenshteinDistance()
    getStudent()
  }
}
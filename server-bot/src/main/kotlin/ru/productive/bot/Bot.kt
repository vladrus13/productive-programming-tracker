package ru.productive.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
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
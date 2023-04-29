package ru.productive.config

import java.util.Properties

object BotConfig : Properties() {

  val TOKEN : String
  val SERVER_API_URL : String

  init {
    load(BotConfig.javaClass.getResourceAsStream("/bot.properties"))
    TOKEN = getProperty("bot.token")
    SERVER_API_URL = getProperty("serverApi.url")
  }
}

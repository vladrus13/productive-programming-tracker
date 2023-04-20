package ru.productive.config

import java.util.Properties

object BotConfig : Properties() {

  val TOKEN : String

  init {
    load(BotConfig.javaClass.getResourceAsStream("/bot.properties"))
    TOKEN = getProperty("bot.token")
  }
}

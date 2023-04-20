package ru.productive.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import ru.productive.bot.commands.ping
import ru.productive.config.BotConfig

val Bot = bot {
  token = BotConfig.TOKEN

  dispatch {
    ping()
  }
}
package ru.productive

import com.github.kotlintelegrambot.Bot

class Launcher(
  val bot : Bot
) {
  fun run() {
    bot.startPolling()
  }
}
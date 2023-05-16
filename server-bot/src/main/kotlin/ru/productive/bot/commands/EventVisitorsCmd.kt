package ru.productive.bot.commands

import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import model.entity.EventVisitor
import ru.productive.api.client.ApiClient
import ru.productive.bot.botLogger
import ru.productive.bot.commands.parser.parseAddEventVisitorArguments
import ru.productive.bot.commands.parser.parseGetEventVisitorEventId
import ru.productive.bot.commands.parser.parseIdArg
import ru.productive.utils.LoggerUtils.Companion.addAnswer
import ru.productive.utils.LoggerUtils.Companion.addFailAnswer
import ru.productive.utils.LoggerUtils.Companion.addUserMessage

fun Dispatcher.getEventVisitors(apiClient: ApiClient) {
    command("getEventVisitors") {
        runBlocking {
            botLogger.addUserMessage("getEventVisitors", message)
            parseGetEventVisitorEventId(message.text)
                .onSuccess { eventId ->
                    val visitors = apiClient.getVisitors(eventId)

                    val visitorsText = visitors.joinToString(separator = "\n") { visitor ->
                        val statusEmoji = when (visitor.visitStatus) {
                            EventVisitor.VisitStatus.R -> "\uD83D\uDFE1"
                            EventVisitor.VisitStatus.M -> "\uD83D\uDD34"
                            EventVisitor.VisitStatus.V -> "\uD83D\uDFE2"
                        }
                        "$statusEmoji _${visitor.fullName}_ with id ${visitor.id}"
                    }
                    val withEventHeader = "Visitors for event with id \\= $eventId:\n$visitorsText"

                    botLogger.fine("Answer to ${message.from?.id}: $withEventHeader")

                    bot.sendMessage(ChatId.fromId(message.chat.id), text = withEventHeader, parseMode = ParseMode.MARKDOWN_V2)
                }
                .onFailure { e ->
                    botLogger.addFailAnswer("addEvent", message, e.stackTrace.toString())
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
                }
        }
    }
}

fun Dispatcher.addEventVisitor(apiClient: ApiClient) {

    command("addEventVisitor") {
        runBlocking {
            botLogger.addUserMessage("addEventVisitors", message)
            parseAddEventVisitorArguments(message.text)
                .onSuccess { (eventId, fullName) ->
                    val response: HttpResponse = apiClient.addVisitor(eventId, fullName)
                    val textResponse = response.bodyAsText()
                    botLogger.addAnswer("addEventVisitors", message, textResponse)
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = textResponse)
                }
                .onFailure { e ->
                    botLogger.addFailAnswer("addEventVisitors", message, e.stackTrace.toString())
                    bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
                }
        }
    }
}

fun CommandHandlerEnvironment.setVisitorStatus(cmdName: String,
                                               apiClient: ApiClient,
                                               visitorStatus: EventVisitor.VisitStatus) {
    runBlocking {
        botLogger.addUserMessage(cmdName, message)
        parseIdArg(message.text)
            .onSuccess { visitorId ->
                val responseText = apiClient.setVisitorStatus(visitorId, visitorStatus)

                botLogger.addAnswer(cmdName, message, responseText)

                bot.sendMessage(ChatId.fromId(message.chat.id), text = responseText)
            }
            .onFailure { e ->
                botLogger.addFailAnswer(cmdName, message, e.stackTrace.toString())
                bot.sendMessage(ChatId.fromId(message.chat.id), text = e.message ?: "Error")
            }
    }
}

fun Dispatcher.markAsMissed(apiClient: ApiClient) {

    command("markAsMissed") {
        this.setVisitorStatus("markAsMissed", apiClient, EventVisitor.VisitStatus.M)
    }
}

fun Dispatcher.markAsVisited(apiClient: ApiClient) {

    command("markAsVisited") {
        this.setVisitorStatus("markAsVisited", apiClient, EventVisitor.VisitStatus.V)
    }
}

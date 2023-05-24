package monitoring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

object YandexToken {

    private var lastTimeChangeToken = System.currentTimeMillis()
    private var getTokenEvery = 1L /* hours*/ * 60 /* minutes */ * 60 /* seconds */ * 1000 /* milliseconds */

    private var currentToken : String? = null
        set(value) {
            field = value
            lastTimeChangeToken = System.currentTimeMillis()
        }
        get() =
            if (field == null) null
            else if (lastTimeChangeToken + getTokenEvery < System.currentTimeMillis()) {
                runBlocking { getProcessToken() }
            } else {
                field
            }

    private var currentFolder : String? = null
    private val objectMapper = ObjectMapper()
    private var httpClient = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun <T> PipelineContext<Unit, ApplicationCall>.withSendTime(
        invoked : suspend PipelineContext<Unit, ApplicationCall>.() -> T
    ): T {
        val start = System.currentTimeMillis()
        val result = invoked()
        sendMonitoringEvent(call.request.path(), (System.currentTimeMillis() - start).toDouble())
        return result
    }

    suspend fun sendMonitoringEvent(label: String, value: Double) {
        if (currentToken == null || currentFolder == null) return
        val metric = objectMapper.createObjectNode()
        metric.put("name", label)
        metric.put("value", value)
        val metrics = objectMapper.createArrayNode()
        metrics.add(metric)
        val node = objectMapper.createObjectNode()
        node.replace("metrics", metrics)

        val answer = httpClient.post("https://monitoring.api.cloud.yandex.net/monitoring/v2/data/write?folderId=$currentFolder&service=custom"){
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $currentToken")
            }
            setBody(node.toPrettyString())
        }
        if (!answer.status.isSuccess()) {
            throw IllegalStateException("HTTP request is not success. Please, check your internet connection")
        }
        val answerJson = objectMapper.readTree(answer.bodyAsText())
        if (answerJson["errorMessage"] != null || answerJson["writtenMetricsCount"]?.asText()?.toIntOrNull() == null) {
            throw IllegalStateException("Request to API was incorrect")
        }
    }

    private fun createErrorYCMessage(command: String) = "Command `${command}` failed. You probably not login (run `yc init`) or don't have internet connection"

    private suspend fun runHelp() {
        val help = runCatching { process(
            "yc", "--help",
            stdout = Redirect.CAPTURE,
            stderr = Redirect.SILENT
        ) }.getOrNull()
        if (help?.resultCode != 0) {
            // TODO we can provide yandex cloud metrics install
            throw IllegalStateException("You should install Yandex Cloud CLI for this action. Please, see: https://cloud.yandex.ru/docs/cli/quickstart#install")
        }
    }

    private suspend fun getProcessToken() : String {
        val getTokenProcess = runCatching { process(
            "yc", "iam", "create-token",
            stdout = Redirect.CAPTURE,
            stderr = Redirect.SILENT
        ) }.getOrNull()
        if (getTokenProcess?.resultCode != 0) {
            throw IllegalStateException(createErrorYCMessage("yc iam create-token"))
        } else {
            return getTokenProcess.output[0].trim()
        }
    }

    private suspend fun getProcessFolderId() : String {
        val getFoldersProcess = runCatching { process(
            "yc", "resource-manager", "folder", "list", "--format=json",
            stdout = Redirect.CAPTURE,
            stderr = Redirect.SILENT
        ) }.getOrNull()
        if (getFoldersProcess?.resultCode != 0) {
            throw IllegalStateException(createErrorYCMessage("yc resource-manager folder list --format=json"))
        } else {
            val output = getFoldersProcess.output.joinToString(separator = "\n")
            val foldersArray = objectMapper.readTree(output) as ArrayNode
            val foldersIds = foldersArray.mapNotNull { it["id"]?.asText() }
            return when (foldersIds.size) {
                0 -> throw IllegalStateException("Yandex Cloud don't have folders. Please, create at least one (https://cloud.yandex.com/en/docs/resource-manager/operations/folder/create)")
                // 1 -> { foldersIds[0] }
                else -> {
                    println("Please, choose folder for use metrics (enter number of folder):\n" + foldersIds.mapIndexed { index, s -> "[$index]: $s" }.joinToString(separator = "\n"))
                    val answer = readln().toIntOrNull() ?: throw IllegalStateException("Use correct number")
                    val folderId = foldersIds.getOrNull(answer) ?: throw IllegalStateException("Id out of bound")
                    folderId
                }
            }
        }
    }

    suspend fun enableMetrics() {
        val properties = withContext(Dispatchers.IO) {
            Properties().apply { load(YandexToken::class.java.getResourceAsStream("/yandex-metrics.properties")) }
        }
        currentToken = properties.getProperty("TOKEN") ?: getProcessToken()
        currentFolder = properties.getProperty("FOLDER") ?: getProcessFolderId()
    }
}
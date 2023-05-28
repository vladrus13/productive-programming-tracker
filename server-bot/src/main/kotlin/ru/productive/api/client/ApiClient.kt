package ru.productive.api.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import model.TextResponse
import model.entity.Event
import model.entity.EventVisitor
import java.net.URI

class ApiClient(
    private val apiUri: URI,
    private val apiClient: HttpClient
) {
    constructor(apiUrl: String) : this(
        URI(apiUrl),
        HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.INFO
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                })
            }
        }
    )

    private val addEventEndpoint = apiUri.resolve("/api/event/add").toString()
    private val getEventsEndpoint = apiUri.resolve("/api/event/all").toString()

    private val addEventAdministratorEndpoint = apiUri.resolve("/api/event-administrators/add").toString()

    private val getVisitorsEndpoint = apiUri.resolve("/api/event-visitors").toString()
    private val addVisitorEndpoint = apiUri.resolve("/api/event-visitors/add").toString()
    private val setVisitorStatus = apiUri.resolve("/api/event-visitors/set-status").toString()

    suspend fun addEvent(title: String, userName: String): HttpResponse {
        return apiClient.post(addEventEndpoint) {
            parameter("title", title)
            parameter("userName", userName)
        }
    }

    suspend fun getEvents(userName: String): List<Event> {
        val response = apiClient.get(getEventsEndpoint) {
            parameter("userName", userName)
        }
        return getResponseBody(response)
    }

    suspend fun addEventAdministrator(eventId: Long, userName: String): HttpResponse {
        return apiClient.post(addEventAdministratorEndpoint) {
            parameter("eventId", eventId)
            parameter("userName", userName)
        }
    }

    suspend fun getVisitors(eventId: Long): List<EventVisitor> {
        val response = apiClient.get(getVisitorsEndpoint) {
            parameter("eventId", eventId)
        }
        return getResponseBody(response)
    }

    suspend fun addVisitor(eventId: Long, fullName: String, username: String): HttpResponse {
        return apiClient.post(addVisitorEndpoint) {
            parameter("eventId", eventId)
            parameter("fullName", fullName)
            parameter("username", username)
        }
    }

    suspend fun setVisitorStatus(visitorId: Long, visitorStatus: EventVisitor.VisitStatus): String {
        val response = apiClient.get(setVisitorStatus) {
            parameter("visitorId", visitorId)
            parameter("visitorStatus", visitorStatus)
        }
        return response.body<TextResponse>().message
    }

    private suspend inline fun <reified T> getResponseBody(response: HttpResponse): T {
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else {
            throw BadResponseStatusException(response.body())
        }
    }

    class BadResponseStatusException(val response: TextResponse) :
        Exception("Bad Response status: ${response.message}") {
    }
}
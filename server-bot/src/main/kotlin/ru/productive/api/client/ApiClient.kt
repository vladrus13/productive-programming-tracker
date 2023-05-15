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
import io.ktor.http.ContentType.Application.Json
import kotlinx.serialization.json.Json
import model.entity.Event
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

    suspend fun addEvent(title: String, userName: String): HttpResponse {
        return apiClient.post(addEventEndpoint) {
            parameter("title", title)
            parameter("userName", userName)
        }
    }

    suspend fun getEvents(userName: String): List<Event> {
        return apiClient.get(getEventsEndpoint) {
            parameter("userName", userName)
        }.body()
    }

    suspend fun addEventAdministrator(eventId: Long, userName: String): HttpResponse {
        return apiClient.post(addEventAdministratorEndpoint) {
            parameter("eventId", eventId)
            parameter("userName", userName)
        }
    }

    suspend fun getVisitors(eventId: Long): HttpResponse {
        return apiClient.get(getVisitorsEndpoint) {
            parameter("eventId", eventId)
        }
    }

    suspend fun addVisitor(eventId: Long, fullName: String): HttpResponse {
        return apiClient.post(addVisitorEndpoint) {
            parameter("eventId", eventId)
            parameter("fullName", fullName)
        }
    }
}
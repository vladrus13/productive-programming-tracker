package ru.productive.api.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.URI
import kotlin.io.path.toPath

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
        }
    )

    private val addEventEndpoint = apiUri.resolve("/api/event/add").toString()

    private val getVisitorsEndpoint = apiUri.resolve("/api/event-visitors").toString()
    private val addVisitorEndpoint = apiUri.resolve("/api/event-visitors/add").toString()

    suspend fun addEvent(title: String): HttpResponse {
        return apiClient.post(addEventEndpoint) {
            parameter("title", title)
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
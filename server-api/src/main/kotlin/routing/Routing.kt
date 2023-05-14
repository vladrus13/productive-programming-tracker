package routing

import dao.EventDAO
import dao.EventVisitorDAO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.entity.Event
import model.entity.EventVisitor
import model.TextResponse
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


fun Application.configureRouting() {
    routing {
        route("/api/event") {
            val eventDAO by closestDI().instance<EventDAO>()

            get("{id?}") {
                call.log()

                val id = call.parameters["id"]?.toLong() ?: return@get call.respondJSONText(
                    "Missing id",
                    HttpStatusCode.BadRequest
                )
                val event = eventDAO.findById(id) ?: return@get call.respondJSONText(
                    "No event with id $id",
                    HttpStatusCode.NotFound
                )
                call.respond(event)
            }

            post {
                call.log()

                val event = call.receive<Event>()
                val id = eventDAO.upsert(event) ?: return@post call.respondJSONText(
                    "Nonexistent id ${event.id}",
                    HttpStatusCode.BadRequest
                )
                call.respond(HttpStatusCode.Created, Event(id, event.title))
            }

            post("/add") {
                call.log()

                val title = call.parameters["title"] ?: return@post call.respondJSONText(
                    "Missing title", HttpStatusCode.BadRequest)

                val eventId = eventDAO.upsert(Event(null, title))
                return@post call.respondJSONText("Event is created with id $eventId", HttpStatusCode.OK)
            }

            delete("{id?}") {
                call.log()

                val id = call.parameters["id"]?.toLong() ?: return@delete call.respondJSONText(
                    "Missing id",
                    HttpStatusCode.BadRequest
                )
                if (eventDAO.delete(id)) {
                    call.response.status(HttpStatusCode.Accepted)
                } else {
                    return@delete call.respondText(
                        "No event with id $id",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }
        route("/api/event-visitors") {

            val eventVisitorsDAO by closestDI().instance<EventVisitorDAO>()

            get {
                call.log()

                val eventId: Long = call.parameters["eventId"]?.toLong() ?: return@get call.respondJSONText("E", HttpStatusCode.BadRequest)
                val visitors = eventVisitorsDAO.findAllByEventId(eventId)
                return@get call.respondJSONText(visitors.joinToString(",") { it.fullName }, HttpStatusCode.OK)
            }

            post("/add") {
                call.log()

                val parameters = call.parameters

                val eventId: Long = parameters["eventId"]?.toLong() ?: return@post call.respondJSONText("E", HttpStatusCode.BadRequest)
                val fullName: String = parameters["fullName"] ?: return@post call.respondJSONText("E", HttpStatusCode.BadRequest)

                val visitorId = eventVisitorsDAO.upsert(EventVisitor(null, eventId, fullName, EventVisitor.VisitStatus.R))
                return@post call.respondJSONText("Visitor is registered with id=$visitorId", HttpStatusCode.OK)
            }
        }
    }
}

private suspend fun ApplicationCall.respondJSONText(message: String, statusCode: HttpStatusCode) {
    return this.respond(statusCode, TextResponse(statusCode.value, message))
}

private fun ApplicationCall.log() {
    application.environment.log.debug("[${this.request.httpMethod.value}] ${this.request.uri}")
}

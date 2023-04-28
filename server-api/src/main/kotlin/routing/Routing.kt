package routing

import dao.EventDAO
import dao.EventVisitorDAO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Event
import model.EventVisitor
import model.TextResponse
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


fun Application.configureRouting() {
    routing {
        route("/api/event") {
            val eventDAO by closestDI().instance<EventDAO>()

            get("{id?}") {
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
                val event = call.receive<Event>()
                val id = eventDAO.upsert(event) ?: return@post call.respondJSONText(
                    "Nonexistent id ${event.id}",
                    HttpStatusCode.BadRequest
                )
                call.respond(HttpStatusCode.Created, Event(id, event.title))
            }

            post("/add") {
                val title = call.parameters["title"] ?: return@post call.respondJSONText(
                    "Missing title", HttpStatusCode.BadRequest)

                val eventId = eventDAO.upsert(Event(null, title))
                return@post call.respondJSONText("Event is created with id $eventId", HttpStatusCode.OK)
            }

            delete("{id?}") {
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
                // TODO: list of visitors
                return@get call.respondJSONText("Test", HttpStatusCode.OK)
            }

            post("/add") {
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

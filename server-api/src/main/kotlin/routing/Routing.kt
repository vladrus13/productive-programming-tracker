package routing

import dao.EventDAO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Event
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


fun Application.configureRouting() {
    routing {
        route("/api/event") {
            val eventDAO by closestDI().instance<EventDAO>()

            get("{id?}") {
                val id = call.parameters["id"]?.toLong() ?: return@get call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                val event = eventDAO.findById(id) ?: return@get call.respondText(
                    "No event with id $id",
                    status = HttpStatusCode.NotFound
                )
                call.respond(event)
            }

            post {
                val event = call.receive<Event>()
                val id = eventDAO.upsert(event) ?: return@post call.respondText(
                    "Nonexistent id ${event.id}",
                    status = HttpStatusCode.BadRequest
                )
                call.respond(HttpStatusCode.Created, Event(id, event.title))
            }

            delete("{id?}") {
                val id = call.parameters["id"]?.toLong() ?: return@delete call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
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
    }
}
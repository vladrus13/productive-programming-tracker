package routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Event

fun Application.configureRouting() {
    routing {
        get("/api/event/{id?}") {
            val id = call.parameters["id"]?.toLong() ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val event = Event(0, "")
            call.respond(event)
        }
        post("/api/event") {
            val event = call.receive<Event>()
            call.respond(HttpStatusCode.Created)
        }
    }
}
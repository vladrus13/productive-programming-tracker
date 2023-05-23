package routing

import dao.DatabaseFactory
import dao.impl.EventAdministratorDAO
import dao.impl.EventDAO
import dao.impl.EventVisitorDAO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.entity.Event
import model.entity.EventVisitor
import model.TextResponse
import model.entity.EventAdministrator
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


fun Application.configureRouting() {
    routing {
        val eventDAO by closestDI().instance<EventDAO>()
        val eventAdministratorsDAO by closestDI().instance<EventAdministratorDAO>()
        val eventVisitorsDAO by closestDI().instance<EventVisitorDAO>()

        route("/api/event") {

            get("{id?}") {
                call.log()

                val id = call.extractParameter("id")?.toLong() ?: return@get
                val event = eventDAO.findById(id) ?: return@get call.respondJsonText(
                    "No event with id $id",
                    HttpStatusCode.NotFound
                )
                call.respond(event)
            }

            get("/all") {
                call.log()

                val userName = call.extractParameter("userName") ?: return@get

                val eventIds = eventAdministratorsDAO.findAllByUserName(userName).map { admin -> admin.eventId }
                val events = eventDAO.findByIds(eventIds)

                call.respond(events)
            }

            post {
                call.log()

                val event = call.receive<Event>()
                val id = eventDAO.upsert(event) ?: return@post call.respondJsonText(
                    "Nonexistent id ${event.id}",
                    HttpStatusCode.BadRequest
                )
                call.respond(HttpStatusCode.Created, Event(id, event.title))
            }

            post("/add") {
                call.log()

                val title = call.extractParameter("title") ?: return@post
                val ownerUserName = call.extractParameter("userName") ?: return@post

                val eventId = DatabaseFactory.dbQuery {
                    val eventId = eventDAO.upsert(Event(null, title))!!
                    eventAdministratorsDAO.upsert(EventAdministrator(null, eventId, ownerUserName, EventAdministrator.Role.O))!!
                    eventId
                }
                call.respondJsonText("Event is created with id $eventId", HttpStatusCode.OK)
            }

            delete("{id?}") {
                call.log()

                val id = call.extractParameter("id")?.toLong() ?: return@delete
                if (eventDAO.delete(id)) {
                    call.response.status(HttpStatusCode.Accepted)
                } else {
                    return@delete call.respondJsonText(
                        "No event with id $id",
                        HttpStatusCode.NotFound
                    )
                }
            }
        }

        route("/api/event-administrators") {

            post("/add") {
                call.log()

                val eventId: Long = call.extractParameter("eventId")?.toLong() ?: return@post
                val userName: String = call.extractParameter("userName") ?: return@post

                val adminId = eventAdministratorsDAO.upsert(EventAdministrator(null, eventId, userName, EventAdministrator.Role.A))
                call.respondJsonText("Administrator is added with id=$adminId", HttpStatusCode.OK)
            }

        }

        route("/api/event-visitors") {

            get {
                call.log()

                val eventId: Long = call.extractParameter("eventId")?.toLong() ?: return@get
                val visitors = eventVisitorsDAO.findAllByEventId(eventId)

                call.respond(visitors)
            }

            get("/set-status") {
                call.log()

                val visitorId: Long = call.extractParameter("visitorId")?.toLong() ?: return@get
                val rawStatus = call.extractParameter("visitorStatus") ?: return@get
                val status = EventVisitor.VisitStatus.valueOf(rawStatus)

                if (eventVisitorsDAO.updateVisitStatus(visitorId, status)) {
                    call.respondJsonText("Update successfully", HttpStatusCode.OK)
                } else {
                    call.respondJsonText("No entity has been updated", HttpStatusCode.BadRequest)
                }
            }

            post("/add") {
                call.log()

                val eventId: Long = call.extractParameter("eventId")?.toLong() ?: return@post
                val fullName: String = call.extractParameter("fullName") ?: return@post

                val visitorId = eventVisitorsDAO.upsert(EventVisitor(null, eventId, fullName, EventVisitor.VisitStatus.R))
                call.respondJsonText("Visitor is registered with id=$visitorId", HttpStatusCode.OK)
            }
        }
    }
}

private suspend fun ApplicationCall.respondJsonText(message: String, statusCode: HttpStatusCode) {
    return respond(statusCode, TextResponse(statusCode.value, message))
}

private suspend fun ApplicationCall.extractParameter(parameterName: String): String? {
    return parameters[parameterName].also {
        it ?: respondJsonText("Missing parameter '$parameterName'.",  HttpStatusCode.BadRequest)
    }
}

private fun ApplicationCall.log() {
    application.environment.log.debug("[${this.request.httpMethod.value}] ${this.request.uri}")
}

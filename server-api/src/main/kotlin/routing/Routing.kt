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
import monitoring.YandexToken.withSendTime
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


fun Application.configureRouting() {
    routing {
        val eventDAO by closestDI().instance<EventDAO>()
        val eventAdministratorsDAO by closestDI().instance<EventAdministratorDAO>()
        val eventVisitorsDAO by closestDI().instance<EventVisitorDAO>()

        route("/api/event") {

            get("{id?}") {
                withSendTime {
                    call.log()

                    val id = call.extractParameter("id")?.toLong() ?: return@withSendTime
                    val event = eventDAO.findById(id) ?: return@withSendTime call.respondJsonText(
                        "No event with id $id",
                        HttpStatusCode.NotFound
                    )
                    call.respond(event)
                }
            }

            get("/all") {
                withSendTime {
                    call.log()

                    val userName = call.extractParameter("userName") ?: return@withSendTime

                    val eventIds = eventAdministratorsDAO.findAllByUserName(userName).map { admin -> admin.eventId }
                    val events = eventDAO.findByIds(eventIds)

                    call.respond(events)
                }
            }

            post {
                withSendTime {
                    call.log()

                    val event = call.receive<Event>()
                    val id = eventDAO.upsert(event) ?: return@withSendTime call.respondJsonText(
                        "Nonexistent id ${event.id}",
                        HttpStatusCode.BadRequest
                    )
                    call.respond(HttpStatusCode.Created, Event(id, event.title))
                }
            }

            post("/add") {
                withSendTime {
                    call.log()

                    val title = call.extractParameter("title") ?: return@withSendTime
                    val ownerUserName = call.extractParameter("userName") ?: return@withSendTime

                    val eventId = DatabaseFactory.dbQuery {
                        val eventId = eventDAO.upsert(Event(null, title))!!
                        eventAdministratorsDAO.upsert(EventAdministrator(null, eventId, ownerUserName, EventAdministrator.Role.O))!!
                        eventId
                    }
                    call.respondJsonText("Event is created with id $eventId", HttpStatusCode.OK)
                }
            }

            delete("/delete") {
                withSendTime {
                    call.log()

                    val eventId = call.extractParameter("eventId")?.toLong() ?: return@withSendTime
                    val ownerUserName = call.extractParameter("userName") ?: return@withSendTime

                    return@withSendTime DatabaseFactory.dbQuery {
                        if (!eventAdministratorsDAO.confirmOwnershipByEventIdAndUserName(eventId, ownerUserName)) {
                            call.respondJsonText(
                                "User is not an owner of event with id $eventId",
                                HttpStatusCode.Forbidden
                            )
                        }
                        if (eventDAO.delete(eventId)) {
                            call.response.status(HttpStatusCode.Accepted)
                        } else {
                            call.respondJsonText(
                                "No event with id $eventId",
                                HttpStatusCode.NotFound
                            )
                        }
                    }
                }
            }
        }

        route("/api/event-administrators") {

            post("/add") {
                withSendTime {
                    call.log()

                    val eventId: Long = call.extractParameter("eventId")?.toLong() ?: return@withSendTime
                    val userName: String = call.extractParameter("userName") ?: return@withSendTime

                    val adminId = eventAdministratorsDAO.upsert(
                        EventAdministrator(
                            null,
                            eventId,
                            userName,
                            EventAdministrator.Role.A
                        )
                    )
                    call.respondJsonText("Administrator is added with id=$adminId", HttpStatusCode.OK)
                }
            }

        }

        route("/api/event-visitors") {

            get {
                withSendTime {
                    call.log()

                    val eventId: Long = call.extractParameter("eventId")?.toLong() ?: return@withSendTime
                    val visitors = eventVisitorsDAO.findAllByEventId(eventId)

                    call.respond(visitors)
                }
            }

            get("/set-status") {
                withSendTime {
                    call.log()

                    val visitorId: Long = call.extractParameter("visitorId")?.toLong() ?: return@withSendTime
                    val rawStatus = call.extractParameter("visitorStatus") ?: return@withSendTime
                    val status = EventVisitor.VisitStatus.valueOf(rawStatus)

                    if (eventVisitorsDAO.updateVisitStatus(visitorId, status)) {
                        call.respondJsonText("Update successfully", HttpStatusCode.OK)
                    } else {
                        call.respondJsonText("No entity has been updated", HttpStatusCode.BadRequest)
                    }
                }
            }

            post("/add") {
                withSendTime {
                    call.log()

                    val eventId: Long = call.extractParameter("eventId")?.toLong() ?: return@withSendTime
                    val fullName: String = call.extractParameter("fullName") ?: return@withSendTime

                    val visitorId =
                        eventVisitorsDAO.upsert(EventVisitor(null, eventId, fullName, EventVisitor.VisitStatus.R))
                    call.respondJsonText("Visitor is registered with id=$visitorId", HttpStatusCode.OK)
                }
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

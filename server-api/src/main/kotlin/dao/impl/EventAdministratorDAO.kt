package dao.impl

import model.entity.EventAdministrator
import org.jetbrains.exposed.dao.id.LongIdTable

class EventAdministratorDAO : AbstractGenericDAO<EventAdministrator, EventAdministrators>(
    EventAdministrator.DBEntity, EventAdministrators
)

object EventAdministrators : LongIdTable() {
    val eventId = reference("event_id", Events.id)
    val userName = varchar("user_name", 64)
    val role = enumerationByName("role", 3, EventAdministrator.Role::class)
}
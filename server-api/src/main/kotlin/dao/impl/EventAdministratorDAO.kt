package dao.impl

import dao.DatabaseFactory
import model.entity.EventAdministrator
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.select

class EventAdministratorDAO : AbstractGenericDAO<EventAdministrator, EventAdministrators>(
    EventAdministrator.DBEntity, EventAdministrators
) {

    suspend fun findAllByUserName(userName: String): List<EventAdministrator> = DatabaseFactory.dbQuery {
        EventAdministrators
            .select { EventAdministrators.userName eq userName }
            .map(EventAdministrator::fromResultRow)
    }
}

object EventAdministrators : LongIdTable() {
    val eventId = reference("event_id", Events.id)
    val userName = varchar("user_name", 64)
    val role = enumerationByName("role", 3, EventAdministrator.Role::class)
}
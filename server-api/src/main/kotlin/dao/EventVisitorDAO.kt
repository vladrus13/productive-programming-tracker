package dao

import model.entity.EventVisitor
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*


class EventVisitorDAO : AbstractGenericDAO<EventVisitor, EventVisitors>(EventVisitor.DBEntity, EventVisitors) {

    suspend fun findAllByEventId(eventId: Long): List<EventVisitor> = DatabaseFactory.dbQuery {
        EventVisitors
            .select { EventVisitors.eventId eq eventId }
            .map(EventVisitor::fromResultRow)
    }

}

object EventVisitors : LongIdTable() {
    val eventId = reference("event_id", Events.id)
    val fullName = varchar("full_name", 256)
    val visitStatus = enumerationByName("visit_status", 3, EventVisitor.VisitStatus::class)
}

package dao.impl

import dao.DatabaseFactory
import model.entity.EventVisitor
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class EventVisitorDAO : AbstractGenericDAO<EventVisitor, EventVisitors>(EventVisitor.DBEntity, EventVisitors) {

    suspend fun findAllByEventId(eventId: Long): List<EventVisitor> = DatabaseFactory.dbQuery {
        EventVisitors
            .select { EventVisitors.eventId eq eventId }
            .map(EventVisitor::fromResultRow)
    }

    suspend fun updateVisitStatus(visitorId: Long,
                                  visitStatus: EventVisitor.VisitStatus): Boolean = DatabaseFactory.dbQuery {
        EventVisitors
            .update({ EventVisitors.id eq visitorId }) {
                it[EventVisitors.visitStatus] = visitStatus
            } > 0
    }

}

object EventVisitors : LongIdTable() {
    val eventId = reference("event_id", Events.id)
    val fullName = varchar("full_name", 256)
    val visitStatus = enumerationByName("visit_status", 3, EventVisitor.VisitStatus::class)
}

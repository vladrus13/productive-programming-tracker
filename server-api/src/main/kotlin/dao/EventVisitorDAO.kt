package dao

import model.EventVisitor
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

// TODO: move common part to abstract DAO
class EventVisitorDAO : GenericDAO<EventVisitor> {

    private fun rowToEvent(row: ResultRow): EventVisitor = EventVisitor(
        id = row[EventVisitors.id].value,
        eventId = row[EventVisitors.eventId].value,
        fullName = row[EventVisitors.fullName],
        visitStatus = row[EventVisitors.visitStatus]
    )

    override suspend fun findById(id: Long): EventVisitor? = DatabaseFactory.dbQuery {
        EventVisitors
            .select { Events.id eq id }
            .map(this::rowToEvent)
            .singleOrNull()
    }

    override suspend fun delete(id: Long): Boolean = DatabaseFactory.dbQuery {
        EventVisitors.deleteWhere { EventVisitors.id eq id } > 0
    }

    override suspend fun upsert(entity: EventVisitor): Long? = DatabaseFactory.dbQuery {
        val id = entity.id ?: return@dbQuery EventVisitors.insertAndGetId {
            it[eventId] = entity.eventId
            it[fullName] = entity.fullName
            it[visitStatus] = entity.visitStatus
        }.value
        val update = EventVisitors.update({ EventVisitors.id eq id }) {
            it[eventId] = entity.eventId
            it[fullName] = entity.fullName
            it[visitStatus] = entity.visitStatus
        }
        if (update == 0) null else id
    }

}

object EventVisitors : LongIdTable() {
    val eventId = reference("event_id", Events.id)
    val fullName = varchar("full_name", 256)
    val visitStatus = enumerationByName("visit_status", 3, EventVisitor.VisitStatus::class)
}

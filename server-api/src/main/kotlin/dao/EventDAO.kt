package dao

import model.Event
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class EventDAO : GenericDAO<Event> {
    private fun rowToEvent(row: ResultRow): Event = Event(
        id = row[Events.id].value,
        title = row[Events.title]
    )
    override suspend fun findById(id: Long): Event? = DatabaseFactory.dbQuery {
        Events
            .select { Events.id eq id }
            .map(::rowToEvent)
            .singleOrNull()
    }

    override suspend fun upsert(entity: Event): Long? = DatabaseFactory.dbQuery {
        val id = entity.id ?: return@dbQuery Events.insertAndGetId {
            it[title] = entity.title
        }.value
        val update = Events.update({ Events.id eq id }) {
            it[title] = entity.title
        }
        if (update == 0) null else id
    }

    override suspend fun delete(id: Long) = DatabaseFactory.dbQuery {
        Events.deleteWhere { Events.id eq id } > 0
    }
}
object Events : LongIdTable() {
    val title = varchar("title", 64)
}
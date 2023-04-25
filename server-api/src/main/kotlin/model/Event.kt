package model

import dao.DatabaseFactory.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

@Serializable
data class Event(val id: Long?, val title: String) {
    companion object {
        private fun rowToEvent(row: ResultRow): Event = Event(
            id = row[Events.id].value,
            title = row[Events.title]
        )

        suspend fun get(id: Long): Event? = dbQuery {
            Events
                .select { Events.id eq id }
                .map(::rowToEvent)
                .singleOrNull()
        }

        suspend fun add(event: Event): Long? = dbQuery {
            val id = event.id ?: return@dbQuery Events.insertAndGetId {
                it[title] = event.title
            }.value
            val update = Events.update({ Events.id eq id }) {
                it[title] = event.title
            }
            if (update == 0) null else id
        }

        suspend fun delete(id: Long) = dbQuery {
            Events.deleteWhere { Events.id eq id }
        }
    }
}

object Events : LongIdTable() {
    val title = varchar("title", 64)
}


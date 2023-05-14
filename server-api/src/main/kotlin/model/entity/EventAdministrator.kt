package model.entity

import dao.impl.EventAdministrators
import kotlinx.serialization.Serializable
import model.ConstructableFromRow
import model.ConvertableToDBBuilder
import model.LongIdEntity
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

@Serializable
data class EventAdministrator(
    override val id: Long?,
    val eventId: Long,
    val userName: String,
    val role: Role
) : LongIdEntity, ConvertableToDBBuilder {

    enum class Role(val code: String) {
        O("owner"),
        A("admin")
    }

    override fun <B : UpdateBuilder<Int>> fillUpdatedBuilder(builder: B): B {
        builder[EventAdministrators.eventId] = eventId
        builder[EventAdministrators.userName] = userName
        builder[EventAdministrators.role] = role

        return builder
    }

    companion object DBEntity : ConstructableFromRow<EventAdministrator> {

        override fun fromResultRow(row: ResultRow): EventAdministrator = EventAdministrator(
            id = row[EventAdministrators.id].value,
            eventId = row[EventAdministrators.eventId].value,
            userName = row[EventAdministrators.userName],
            role = row[EventAdministrators.role]
        )

    }

}
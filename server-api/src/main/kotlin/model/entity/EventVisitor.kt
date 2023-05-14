package model.entity

import dao.EventVisitors
import kotlinx.serialization.Serializable
import model.ConstructableFromRow
import model.ConvertableToDBBuilder
import model.LongIdEntity
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

@Serializable
data class EventVisitor(
    override val id: Long?,
    val eventId: Long,
    val fullName: String,
    val visitStatus: VisitStatus
) : LongIdEntity, ConvertableToDBBuilder {
    enum class VisitStatus(val code: String) {
        R("registered"),
        M("missed"),
        V("visited")
    }

    override fun <B : UpdateBuilder<Int>> fillUpdatedBuilder(builder: B): B {
        builder[EventVisitors.eventId] = eventId
        builder[EventVisitors.fullName] = fullName
        builder[EventVisitors.visitStatus] = visitStatus

        return builder
    }

    companion object DBEntity: ConstructableFromRow<EventVisitor> {

        override fun fromResultRow(row: ResultRow): EventVisitor = EventVisitor(
            id = row[EventVisitors.id].value,
            eventId = row[EventVisitors.eventId].value,
            fullName = row[EventVisitors.fullName],
            visitStatus = row[EventVisitors.visitStatus]
        )

    }
}

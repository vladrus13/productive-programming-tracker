package model.entity

import dao.impl.Events
import kotlinx.serialization.Serializable
import model.ConstructableFromRow
import model.ConvertableToDBBuilder
import model.LongIdEntity
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

@Serializable
data class Event(override val id: Long?, val title: String) : LongIdEntity, ConvertableToDBBuilder {

    override fun <B : UpdateBuilder<Int>> fillUpdatedBuilder(builder: B): B {
        builder[Events.title] = title

        return builder
    }

    companion object DBEntity: ConstructableFromRow<Event> {

        override fun fromResultRow(row: ResultRow): Event = Event(
            id = row[Events.id].value,
            title = row[Events.title]
        )
    }

}



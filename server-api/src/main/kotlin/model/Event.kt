package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Event(val id: Long?, val title: String)

object Events : Table() {
    val id = long("id").autoIncrement()
    val title = varchar("title", 64)

    override val primaryKey = PrimaryKey(id)
}

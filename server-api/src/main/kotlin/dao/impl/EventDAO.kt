package dao.impl


import model.entity.Event
import org.jetbrains.exposed.dao.id.LongIdTable

class EventDAO : AbstractGenericDAO<Event, Events>(Event.DBEntity, Events)

object Events : LongIdTable() {
    val title = varchar("title", 64)

    init {
        uniqueIndex(title)
    }
}
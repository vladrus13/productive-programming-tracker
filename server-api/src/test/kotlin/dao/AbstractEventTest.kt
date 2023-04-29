package dao

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update

abstract class AbstractEventTest(
    databaseName: String,
    tables: Array<Table> ,
) : DatabaseTest(databaseName, tables) {

    protected fun defaultEventTableSetup(eventCnt: Long = 11L) {
        runBlocking {
            (0L until eventCnt).forEach { _ ->
                DatabaseFactory.dbQuery {
                    val id = Events.insertAndGetId { it[title] = "" }.value
                    Events.update({ Events.id eq id }) {
                        it[title] = "Title $id"
                    }
                }
            }
        }
    }
}
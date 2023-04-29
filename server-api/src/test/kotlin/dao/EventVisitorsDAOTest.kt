package dao

import kotlinx.coroutines.runBlocking
import model.EventVisitor
import model.EventVisitor.VisitStatus
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EventVisitorsDAOTest: AbstractEventTest("test_db", arrayOf(Events, EventVisitors)) {
    private val dao = EventVisitorDAO()

    private val defaultEventCnt = 3L

    private fun idToEventId(id: Long): Long = 1 + id % defaultEventCnt

    private fun defaultEventVisitorsTableSetup(visitorsCnt: Long = 11L) {
        runBlocking {
            defaultEventTableSetup(defaultEventCnt)
            (0L until visitorsCnt).forEach { _ ->
                DatabaseFactory.dbQuery {
                    val id = EventVisitors.insertAndGetId {
                        it[eventId] = 1L
                        it[fullName] = ""
                        it[visitStatus] = VisitStatus.R
                    }.value
                    EventVisitors.update({ EventVisitors.id eq id }) {
                        it[eventId] = idToEventId(id)
                        it[fullName] = "FirstName LastName$id"
                    }
                }
            }
        }
    }

    @Test
    fun testFindById_NotExist() {
        runBlocking {
            val event = dao.findById(123456789L)
            Assertions.assertNull(event)
        }
    }

    @Test
    fun testFindById_Exist() {
        runBlocking {
            defaultEventVisitorsTableSetup()

            val event = dao.findById(1L)
            assertNotNull(event)
            assertEquals("FirstName LastName1", event.fullName)
            assertEquals(idToEventId(1L), event.eventId)
            assertEquals(VisitStatus.R, event.visitStatus)
        }
    }

    @Test
    fun testSaveNewEventVisitor() {
        runBlocking {
            defaultEventVisitorsTableSetup()

            val fullName = "Ivanov Ivan"
            val eventId = 1L
            val savedEventVisitorId = dao.upsert(EventVisitor(null, eventId, fullName, VisitStatus.R))
            assertNotNull(savedEventVisitorId)

            val actualEventVisitor = dao.findById(savedEventVisitorId)
            assertNotNull(actualEventVisitor)
            assertEquals(savedEventVisitorId, actualEventVisitor.id)
            assertEquals(eventId, actualEventVisitor.eventId)
            assertEquals(fullName, actualEventVisitor.fullName)
            assertEquals(VisitStatus.R, actualEventVisitor.visitStatus)
        }
    }
}
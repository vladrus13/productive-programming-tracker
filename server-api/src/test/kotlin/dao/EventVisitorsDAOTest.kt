package dao

import kotlinx.coroutines.runBlocking
import model.entity.EventVisitor
import model.entity.EventVisitor.VisitStatus
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
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

    @Test
    fun testUpdateExistingEventVisitor() {
        runBlocking {
            defaultEventVisitorsTableSetup()

            val initialEventVisitor1 = dao.findById(1L)
            assertNotNull(initialEventVisitor1)
            val newEventVisitor1 = EventVisitor(1L, initialEventVisitor1.eventId, initialEventVisitor1.fullName, VisitStatus.V)

            val savedEventVisitorId = dao.upsert(newEventVisitor1)
            assertNotNull(savedEventVisitorId)
            assertEquals(1L, savedEventVisitorId)

            val actualEventVisitor1 = dao.findById(1L)
            assertNotNull(actualEventVisitor1)
            assertEquals(initialEventVisitor1.id, actualEventVisitor1.id)
            assertEquals(initialEventVisitor1.eventId, actualEventVisitor1.eventId)
            assertEquals(VisitStatus.V, actualEventVisitor1.visitStatus)
        }
    }

    @Test
    fun testUpdateNonExistingEventVisitor() {
        runBlocking {
            defaultEventVisitorsTableSetup()

            val eventVisitorIdNotExist: Long = 12345
            val savedEventId = dao.upsert(EventVisitor(eventVisitorIdNotExist, 1L, "II", VisitStatus.M))
            assertNull(savedEventId)

            val actualEvent = dao.findById(eventVisitorIdNotExist)
            assertNull(actualEvent)
        }
    }

    @Test
    fun testDelete() {
        runBlocking {
            defaultEventVisitorsTableSetup()

            val initialEventVisitor3 = dao.findById(3)
            assertNotNull(initialEventVisitor3)

            assertTrue(dao.delete(3))
            assertNull(dao.findById(3))
            assertFalse(dao.delete(3))
        }
    }

    @Test
    fun testFindAllByEventId() {
        runBlocking {
            defaultEventVisitorsTableSetup(3 * defaultEventCnt) // 3 visitor per event

            val eventId = 1L
            val visitors = dao.findAllByEventId(eventId)

            assertEquals(3, visitors.size)
            assertTrue(visitors.all { it.eventId == eventId })
        }
    }

}
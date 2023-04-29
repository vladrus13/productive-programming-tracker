package dao

import kotlinx.coroutines.runBlocking
import model.Event
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventDAOTest : AbstractEventTest("test_db", arrayOf(Events)) {
    private val dao = EventDAO()

    @Test
    fun testFindById_NotExist() {
        runBlocking {
            val event = dao.findById(123456789L)
            assertNull(event)
        }
    }

    @Test
    fun testFindById_Exist() {
        runBlocking {
            defaultEventTableSetup()
            val event = dao.findById(1L)
            assertEquals("Title 1", event?.title)
        }
    }

    @Test
    fun testSaveNewEvent() {
        runBlocking {
            val savedEventId = dao.upsert(Event(null, "Title"))
            assertNotNull(savedEventId)

            val actualEvent = dao.findById(savedEventId!!)
            assertEquals(savedEventId, actualEvent?.id)
            assertEquals("Title", actualEvent?.title)
        }
    }

    @Test
    fun testUpdateExistingEvent() {
        runBlocking {
            defaultEventTableSetup()

            val initialEvent1 = dao.findById(1L)
            assertNotNull(initialEvent1)
            val newEvent1 = Event(1, "NewTitle")

            val savedEventId = dao.upsert(newEvent1)
            assertNotNull(savedEventId)
            assertEquals(1L, savedEventId)

            val actualEvent0 = dao.findById(1)
            assertNotNull(actualEvent0)
            assertEquals(initialEvent1!!.id, actualEvent0!!.id)
            assertEquals("NewTitle", actualEvent0.title)
        }
    }

    @Test
    fun testUpdateNonExistingEvent() {
        runBlocking {
            defaultEventTableSetup()

            val eventIdNotExist: Long = 12345
            val savedEventId = dao.upsert(Event(eventIdNotExist, "Not exist"))
            assertNull(savedEventId)

            val actualEvent = dao.findById(eventIdNotExist)
            assertNull(actualEvent)
        }
    }

    @Test
    fun testDelete() {
        runBlocking {
            defaultEventTableSetup()

            val initialEvent3 = dao.findById(3)
            assertNotNull(initialEvent3)

            assertTrue(dao.delete(3))
            assertNull(dao.findById(3))
            assertFalse(dao.delete(3))
        }
    }
}
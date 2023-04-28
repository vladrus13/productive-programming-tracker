import dao.DatabaseFactory
import dao.EventDAO
import dao.Events
import kotlinx.coroutines.runBlocking
import model.Event
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventDAOTest : DatabaseTest("test_db", arrayOf(Events)) {
    private val dao = EventDAO()

    private fun insertEvent(eventId: Long, title: String): InsertStatement<Number> {
        return Events.insert {
            it[id] = eventId
            it[Events.title] = title
        }
    }

    private fun defaultDatabaseSetup() {
        runBlocking {
            (0L..10L).forEach { ind ->
                DatabaseFactory.dbQuery {
                    insertEvent(ind, "Title $ind")
                }
            }
        }
    }

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
            defaultDatabaseSetup()
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
            defaultDatabaseSetup()

            val initialEvent0 = dao.findById(0)
            assertNotNull(initialEvent0)
            val newEvent0 = Event(0, "NewTitle")

            val savedEventId = dao.upsert(newEvent0)
            assertNotNull(savedEventId)
            assertEquals(0L, savedEventId)

            val actualEvent0 = dao.findById(0)
            assertNotNull(actualEvent0)
            assertEquals(initialEvent0!!.id, actualEvent0!!.id)
            assertEquals("NewTitle", actualEvent0.title)
        }
    }

    @Test
    fun testDelete() {
        runBlocking {
            defaultDatabaseSetup()

            val initialEvent3 = dao.findById(3)
            assertNotNull(initialEvent3)

            assertTrue(dao.delete(3))
            assertNull(dao.findById(3))
            assertFalse(dao.delete(3))
        }
    }
}
package dao

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Properties

object DatabaseFactory {
    fun init() {
        val properties = Properties()
        properties.load(
            DatabaseFactory.javaClass.getResourceAsStream("/database.properties")
        ) //throws IOExc
        val driverClassName = properties.getProperty("driver-class-name")
        val jdbcURL = properties.getProperty("url")!!
        val username = properties.getProperty("username")
        val password = properties.getProperty("password")
        val database = Database.connect(jdbcURL, driver = driverClassName, user = username, password = password)
        transaction(database) {
            SchemaUtils.create(Events)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
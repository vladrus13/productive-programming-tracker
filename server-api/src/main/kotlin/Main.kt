import dao.DatabaseFactory
import dao.bindDao
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import routing.configureRouting
import model.configureSerialization
import monitoring.YandexToken
import org.kodein.di.ktor.di

fun main(args: Array<String>) {
    if (args.contains("--enabled-metrics")) {
        runBlocking {
            YandexToken.enableMetrics()
        }
    }
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

fun Application.module() {
    DatabaseFactory.init()

    di {
        bindDao()
    }

    configureRouting()
    configureSerialization()
}

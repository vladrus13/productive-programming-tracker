package dao

import dao.impl.EventAdministratorDAO
import dao.impl.EventDAO
import dao.impl.EventVisitorDAO
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.bindDao() {
    bind<EventDAO>() with singleton { EventDAO() }
    bind<EventVisitorDAO>() with singleton { EventVisitorDAO() }
    bind<EventAdministratorDAO>() with singleton { EventAdministratorDAO() }
}
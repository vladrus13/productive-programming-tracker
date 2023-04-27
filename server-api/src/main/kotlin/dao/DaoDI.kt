package dao

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.bindDao() {
    bind<EventDAO>() with singleton { EventDAO() }
}
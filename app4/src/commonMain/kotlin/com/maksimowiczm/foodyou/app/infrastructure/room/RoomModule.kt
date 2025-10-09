package com.maksimowiczm.foodyou.app.infrastructure.room

import org.koin.core.scope.Scope
import org.koin.dsl.module

internal const val EVENT_STORE_DATABASE_NAME = "EventStore.db"

internal expect fun Scope.eventStoreDatabase(): EventStoreDatabase

val roomModule = module {
    single<EventStoreDatabase> { eventStoreDatabase() }
    factory { get<EventStoreDatabase>().eventStoreDao }
}

package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.ReadModelDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.extension.databaseBuilder
import com.maksimowiczm.foodyou.search.infrastructure.SearchDatabase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

private const val EVENT_STORE_DATABASE_NAME = "EventStore.db"

private const val READ_MODEL_DATABASE_NAME = "ReadModelDatabase.db"

val roomModule = module {
    single<EventStoreDatabase> {
        databaseBuilder<EventStoreDatabase>(EVENT_STORE_DATABASE_NAME).buildDatabase()
    }
    factoryOf(::RoomEventStore).bind<EventStore>()

    single<ReadModelDatabase> {
            databaseBuilder<ReadModelDatabase>(READ_MODEL_DATABASE_NAME).buildDatabase()
        }
        .binds(arrayOf(SearchDatabase::class))
}

package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDatabase
import com.maksimowiczm.foodyou.food.search.infrastructure.room.FoodSearchDatabase
import org.koin.core.scope.Scope
import org.koin.dsl.binds
import org.koin.dsl.module

internal const val EVENT_STORE_DATABASE_NAME = "EventStore.db"

internal expect fun Scope.eventStoreDatabase(): EventStoreDatabase

internal const val APP_DATABASE_NAME = "AppDatabase.db"

internal expect fun Scope.appDatabase(): AppDatabase

val roomModule = module {
    single<EventStoreDatabase> { eventStoreDatabase() }
    factory { get<EventStoreDatabase>().eventStoreDao }

    single<AppDatabase> { appDatabase() }
        .binds(arrayOf(AccountDatabase::class, FoodSearchDatabase::class))
}

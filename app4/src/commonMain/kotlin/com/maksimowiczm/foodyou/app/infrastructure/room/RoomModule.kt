package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.AppDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.foodsearch.infrastructure.room.FoodSearchDatabase
import org.koin.dsl.binds
import org.koin.dsl.module

private const val EVENT_STORE_DATABASE_NAME = "EventStore.db"

private const val APP_DATABASE_NAME = "AppDatabase.db"

val roomModule = module {
    single<EventStoreDatabase> {
        databaseBuilder<EventStoreDatabase>(EVENT_STORE_DATABASE_NAME).buildDatabase()
    }
    factory { get<EventStoreDatabase>().eventStoreDao }

    single<AppDatabase> { databaseBuilder<AppDatabase>(APP_DATABASE_NAME).buildDatabase() }
        .binds(arrayOf(AccountDatabase::class, FoodSearchDatabase::class))
}

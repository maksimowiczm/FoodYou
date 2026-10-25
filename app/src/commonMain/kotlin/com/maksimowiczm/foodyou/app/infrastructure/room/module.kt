package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room3.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.ReadModelDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryDatabase
import com.maksimowiczm.foodyou.userrecipe.infrastructure.room.UserRecipeDatabase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.path
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds

private const val EVENT_STORE_DATABASE_NAME = "EventStore.db"

private const val READ_MODEL_DATABASE_NAME = "ReadModelDatabase.db"

internal fun Module.room() {
    single {
        val dbFile = FileKit.databasesDir / EVENT_STORE_DATABASE_NAME
        Room.databaseBuilder<EventStoreDatabase>(dbFile.path)
            .setDriver(BundledSQLiteDriver())
            .buildDatabase()
            .eventStoreDao
    }
    factoryOf(::RoomEventStore).bind<EventStore>()

    single<ReadModelDatabase> {
            val dbFile = FileKit.databasesDir / READ_MODEL_DATABASE_NAME
            Room.databaseBuilder<ReadModelDatabase>(dbFile.path)
                .setDriver(BundledSQLiteDriver())
                .buildDatabase()
        }
        .binds(
            arrayOf(
                UserRecipeDatabase::class,
                FoodDiaryDatabase::class,
            )
        )

    single { get<ReadModelDatabase>().homeDao }
    single { get<ReadModelDatabase>().searchDao }
}

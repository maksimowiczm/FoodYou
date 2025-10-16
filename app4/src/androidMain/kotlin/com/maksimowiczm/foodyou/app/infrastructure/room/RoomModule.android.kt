package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.Room
import com.maksimowiczm.foodyou.app.infrastructure.room.AppDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.common.infrastructure.addHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.eventStoreDatabase(): EventStoreDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = EventStoreDatabase::class.java,
            name = EVENT_STORE_DATABASE_NAME,
        )
        .addHelper()
        .buildDatabase()

internal actual fun Scope.appDatabase(): AppDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = APP_DATABASE_NAME,
        )
        .addHelper()
        .buildDatabase()

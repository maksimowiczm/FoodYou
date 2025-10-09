package com.maksimowiczm.foodyou.app.infrastructure.room

import android.os.Build
import androidx.room.Room
import com.maksimowiczm.foodyou.app.infrastructure.room.AppDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase.Companion.buildDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.eventStoreDatabase(): EventStoreDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = EventStoreDatabase::class.java,
            name = EVENT_STORE_DATABASE_NAME,
        )
        .apply {
            // https://developer.android.com/reference/android/database/sqlite/package-summary
            // Require SQLite version >= 3.35
            if (Build.VERSION.SDK_INT < 34) {
                openHelperFactory(RequerySQLiteOpenHelperFactory())
            }
        }
        .buildDatabase()

internal actual fun Scope.appDatabase(): AppDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = APP_DATABASE_NAME,
        )
        .apply {
            // https://developer.android.com/reference/android/database/sqlite/package-summary
            // Require SQLite version >= 3.35
            if (Build.VERSION.SDK_INT < 34) {
                openHelperFactory(RequerySQLiteOpenHelperFactory())
            }
        }
        .buildDatabase()

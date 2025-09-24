package com.maksimowiczm.foodyou.app.infrastructure.room

import android.os.Build
import androidx.room.Room
import com.maksimowiczm.foodyou.app.infrastructure.room.FoodYouDatabase.Companion.buildDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.database(): FoodYouDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = FoodYouDatabase::class.java,
            name = DATABASE_NAME,
        )
        .apply {
            // https://developer.android.com/reference/android/database/sqlite/package-summary
            // Require SQLite version >= 3.35
            if (Build.VERSION.SDK_INT < 34) {
                openHelperFactory(RequerySQLiteOpenHelperFactory())
            }
        }
        .buildDatabase(mealsCallback = get())

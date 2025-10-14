package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import android.os.Build
import androidx.room.Room
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.openFoodFactsDatabase(): OpenFoodFactsDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = OpenFoodFactsDatabase::class.java,
            name = OPEN_FOOD_FACTS_DATABASE_NAME,
        )
        .apply {
            // https://developer.android.com/reference/android/database/sqlite/package-summary
            // Require SQLite version >= 3.35
            if (Build.VERSION.SDK_INT < 34) {
                openHelperFactory(RequerySQLiteOpenHelperFactory())
            }
        }
        .build()

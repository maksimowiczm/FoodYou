package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import androidx.room.Room
import com.maksimowiczm.foodyou.common.infrastructure.addHelper
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDatabase.Companion.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.openFoodFactsDatabase(): OpenFoodFactsDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = OpenFoodFactsDatabase::class.java,
            name = OPEN_FOOD_FACTS_DATABASE_NAME,
        )
        .addHelper()
        .buildDatabase()

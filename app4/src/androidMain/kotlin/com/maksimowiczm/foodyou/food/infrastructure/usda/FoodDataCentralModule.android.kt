package com.maksimowiczm.foodyou.food.infrastructure.usda

import androidx.room.Room
import com.maksimowiczm.foodyou.common.infrastructure.addHelper
import com.maksimowiczm.foodyou.food.infrastructure.usda.room.FoodDataCentralDatabase
import com.maksimowiczm.foodyou.food.infrastructure.usda.room.FoodDataCentralDatabase.Companion.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

actual fun Scope.foodDataCentralDatabase(): FoodDataCentralDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = FoodDataCentralDatabase::class.java,
            name = FOOD_DATA_CENTRAL_DATABASE_NAME,
        )
        .addHelper()
        .buildDatabase()

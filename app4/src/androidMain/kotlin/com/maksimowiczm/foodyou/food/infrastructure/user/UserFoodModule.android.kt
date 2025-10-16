package com.maksimowiczm.foodyou.food.infrastructure.user

import androidx.room.Room
import com.maksimowiczm.foodyou.common.infrastructure.addHelper
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodDatabase
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodDatabase.Companion.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.userFoodDatabase(): UserFoodDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = UserFoodDatabase::class.java,
            name = USER_FOOD_DATABASE_NAME,
        )
        .addHelper()
        .buildDatabase()

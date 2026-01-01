package com.maksimowiczm.foodyou.food.infrastructure.user

import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.food.domain.UserFoodRepository
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodDatabase
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodDatabase.Companion.buildDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

private const val USER_FOOD_DATABASE_NAME = "UserFoodDatabase.db"

internal fun Module.userFoodModule() {
    single { databaseBuilder<UserFoodDatabase>(USER_FOOD_DATABASE_NAME).buildDatabase() }
    factory { get<UserFoodDatabase>().dao }

    factoryOf(::UserFoodRepositoryImpl).bind<UserFoodRepository>()
}

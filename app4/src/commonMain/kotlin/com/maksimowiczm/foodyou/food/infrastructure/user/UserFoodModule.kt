package com.maksimowiczm.foodyou.food.infrastructure.user

import com.maksimowiczm.foodyou.food.domain.UserFoodProductRepository
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind

internal const val USER_FOOD_DATABASE_NAME = "UserFoodDatabase.db"

internal expect fun Scope.userFoodDatabase(): UserFoodDatabase

fun Module.userFoodModule() {
    single { userFoodDatabase() }
    factory { get<UserFoodDatabase>().dao }

    factoryOf(::UserFoodProductRepositoryImpl).bind<UserFoodProductRepository>()
}

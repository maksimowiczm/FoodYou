package com.maksimowiczm.foodyou.userfood.di

import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodRepositoryImpl
import com.maksimowiczm.foodyou.userfood.infrastructure.room.UserFoodDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.room.UserFoodDatabase.Companion.buildDatabase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val USER_FOOD_DATABASE_NAME = "UserFoodDatabase.db"

val userFoodModule = module {
    single { databaseBuilder<UserFoodDatabase>(USER_FOOD_DATABASE_NAME).buildDatabase() }
    factory { get<UserFoodDatabase>().dao }

    factoryOf(::UserFoodRepositoryImpl).bind<UserFoodRepository>()
}

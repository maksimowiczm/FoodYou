package com.maksimowiczm.foodyou.userfood.di

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.common.infrastructure.room.databaseBuilder
import com.maksimowiczm.foodyou.userfood.application.HandleRecipeDeletedEventHandler
import com.maksimowiczm.foodyou.userfood.application.HandleUserProductDeletedEventHandler
import com.maksimowiczm.foodyou.userfood.application.UserProductService
import com.maksimowiczm.foodyou.userfood.application.UserRecipeService
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductRepository
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeRepository
import com.maksimowiczm.foodyou.userfood.domain.search.UserFoodSearchRepository
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.product.UserProductRepositoryImpl
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.UserRecipeRepositoryImpl
import com.maksimowiczm.foodyou.userfood.infrastructure.search.UserFoodSearchRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val USER_FOOD_DATABASE_NAME = "UserFoodDatabase.db"

val userFoodModule = module {
    single { databaseBuilder<UserFoodDatabase>(USER_FOOD_DATABASE_NAME).buildDatabase() }
    factory { get<UserFoodDatabase>().productDao }
    factory { get<UserFoodDatabase>().recipeDao }
    factory { get<UserFoodDatabase>().searchDao }

    factory { UserProductRepositoryImpl(dao = get()) }.bind<UserProductRepository>()

    factoryOf(::UserProductService)

    factory { UserRecipeRepositoryImpl(database = get()) }.bind<UserRecipeRepository>()

    factoryOf(::UserRecipeService)

    factoryOf(::UserFoodSearchRepositoryImpl).bind<UserFoodSearchRepository>()

    // Event handlers
    eventHandlerOf(::HandleUserProductDeletedEventHandler)
    eventHandlerOf(::HandleRecipeDeletedEventHandler)
}

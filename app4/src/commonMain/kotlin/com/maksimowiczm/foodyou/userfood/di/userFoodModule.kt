package com.maksimowiczm.foodyou.userfood.di

import com.maksimowiczm.foodyou.common.event.di.integrationEventBus
import com.maksimowiczm.foodyou.common.event.di.integrationEventHandler
import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.userfood.application.HandleRecipeDeletedEventHandler
import com.maksimowiczm.foodyou.userfood.application.HandleUserFoodDeletedEventHandler
import com.maksimowiczm.foodyou.userfood.domain.product.UserFoodProductDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.product.UserFoodRepository
import com.maksimowiczm.foodyou.userfood.domain.recipe.RecipeDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.recipe.RecipeRepository
import com.maksimowiczm.foodyou.userfood.infrastructure.product.UserFoodRepositoryImpl
import com.maksimowiczm.foodyou.userfood.infrastructure.product.room.UserFoodDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.product.room.UserFoodDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.RecipeRepositoryImpl
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.room.RecipeDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.room.RecipeDatabase.Companion.buildDatabase
import org.koin.dsl.bind
import org.koin.dsl.module

private const val USER_FOOD_DATABASE_NAME = "UserFoodDatabase.db"

private const val RECIPE_DATABASE_NAME = "RecipeDatabase.db"

val userFoodModule = module {
    single { databaseBuilder<UserFoodDatabase>(USER_FOOD_DATABASE_NAME).buildDatabase() }
    factory { get<UserFoodDatabase>().dao }

    factory {
            UserFoodRepositoryImpl(
                dao = get(),
                nameSelector = get(),
                integrationEventBus = integrationEventBus(),
            )
        }
        .bind<UserFoodRepository>()

    single { databaseBuilder<RecipeDatabase>(RECIPE_DATABASE_NAME).buildDatabase() }
    factory { get<RecipeDatabase>().dao }

    factory { RecipeRepositoryImpl(database = get(), integrationEventBus = integrationEventBus()) }
        .bind<RecipeRepository>()

    // Event handlers
    integrationEventHandler<UserFoodProductDeletedEvent, HandleUserFoodDeletedEventHandler> {
        HandleUserFoodDeletedEventHandler(get())
    }

    integrationEventHandler<RecipeDeletedEvent, HandleRecipeDeletedEventHandler> {
        HandleRecipeDeletedEventHandler(get())
    }
}

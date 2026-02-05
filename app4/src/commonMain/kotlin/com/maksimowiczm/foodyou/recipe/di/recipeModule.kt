package com.maksimowiczm.foodyou.recipe.di

import com.maksimowiczm.foodyou.common.event.di.integrationEventBus
import com.maksimowiczm.foodyou.common.event.di.integrationEventHandler
import com.maksimowiczm.foodyou.common.infrastructure.databaseBuilder
import com.maksimowiczm.foodyou.recipe.application.HandleRecipeDeletedEventHandler
import com.maksimowiczm.foodyou.recipe.application.HandleUserFoodDeletedEventHandler
import com.maksimowiczm.foodyou.recipe.domain.RecipeDeletedEvent
import com.maksimowiczm.foodyou.recipe.domain.RecipeRepository
import com.maksimowiczm.foodyou.recipe.infrastructure.RecipeRepositoryImpl
import com.maksimowiczm.foodyou.recipe.infrastructure.room.RecipeDatabase
import com.maksimowiczm.foodyou.recipe.infrastructure.room.RecipeDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductDeletedEvent
import org.koin.dsl.bind
import org.koin.dsl.module

private const val RECIPE_DATABASE_NAME = "RecipeDatabase.db"

val recipeModule = module {
    single { databaseBuilder<RecipeDatabase>(RECIPE_DATABASE_NAME).buildDatabase() }
    factory { get<RecipeDatabase>().dao }

    factory { RecipeRepositoryImpl(dao = get(), integrationEventBus = integrationEventBus()) }
        .bind<RecipeRepository>()

    // Event handlers
    integrationEventHandler<UserFoodProductDeletedEvent, HandleUserFoodDeletedEventHandler> {
        HandleUserFoodDeletedEventHandler(get())
    }

    integrationEventHandler<RecipeDeletedEvent, HandleRecipeDeletedEventHandler> {
        HandleRecipeDeletedEventHandler(get())
    }
}

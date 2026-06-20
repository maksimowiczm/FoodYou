package com.maksimowiczm.foodyou.userrecipe.di

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.userrecipe.application.CompositionSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.FoodDataCentralSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.NestedRecipeSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.OpenFoodFactsSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.UserProductSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.infrastructure.room.RoomUserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.infrastructure.room.UserRecipeDatabase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val userRecipeModule = module {
    factoryOf(::RoomUserRecipeCompositionRepository).bind<UserRecipeCompositionRepository>()

    single { get<UserRecipeDatabase>().compositionDao }

    factoryOf(::UserRecipeService)
    eventHandlerOf(::UserProductSynchronizer)
    eventHandlerOf(::OpenFoodFactsSynchronizer)
    eventHandlerOf(::FoodDataCentralSynchronizer)
    eventHandlerOf(::NestedRecipeSynchronizer)
    eventHandlerOf(::CompositionSynchronizer)
}

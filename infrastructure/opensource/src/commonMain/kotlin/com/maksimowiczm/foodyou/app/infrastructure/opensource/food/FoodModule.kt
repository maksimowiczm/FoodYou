package com.maksimowiczm.foodyou.app.infrastructure.opensource.food

import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.network.RemoteProductMapper
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.network.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.repository.RoomFoodHistoryRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.repository.RoomFoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.repository.RoomProductRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.repository.RoomRecipeRepository
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.FoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.food.domain.repository.RecipeRepository
import com.maksimowiczm.foodyou.food.domain.repository.RemoteProductRequestFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.foodModule() {
    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()
    factoryOf(::RemoteProductMapper)

    factoryOf(::RoomFoodHistoryRepository).bind<FoodHistoryRepository>()
    factoryOf(::RoomFoodMeasurementSuggestionRepository).bind<FoodMeasurementSuggestionRepository>()
    factoryOf(::RoomProductRepository).bind<ProductRepository>()
    factoryOf(::RoomRecipeRepository).bind<RecipeRepository>()
}

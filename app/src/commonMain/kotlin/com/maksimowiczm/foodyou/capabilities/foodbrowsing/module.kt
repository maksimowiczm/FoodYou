package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import com.maksimowiczm.foodyou.capabilities.foodbrowsing.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.swissfoodcomposition.SwissFoodCompositionSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserFoodSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserProductSearchSynchronizer
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserRecipeSearchSynchronizer
import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModel

fun Module.foodBrowsing() {
    viewModel { params ->
        SearchViewModel(
            initialQuery = params.getOrNull(),
            avoidCircularDependencyWith = params.getOrNull(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    scope<SearchViewModel> {
        scopedOf(::OpenFoodFactsSearchExtension)
        scopedOf(::FoodDataCentralSearchExtension)
        scopedOf(::SwissFoodCompositionSearchExtension)
        scopedOf(::UserFoodSearchExtension)
        scopedOf(::FavoriteFoodSearchExtension)
    }

    eventHandlerOf(::UserRecipeSearchSynchronizer)
    eventHandlerOf(::UserProductSearchSynchronizer)
}

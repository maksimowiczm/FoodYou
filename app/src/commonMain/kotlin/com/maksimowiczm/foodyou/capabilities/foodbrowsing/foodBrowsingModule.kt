package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import com.maksimowiczm.foodyou.capabilities.foodbrowsing.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserFoodSearchExtension
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val foodBrowsingModule = module {
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
        scopedOf(::UserFoodSearchExtension)
        scopedOf(::FavoriteFoodSearchExtension)
    }
}

package com.maksimowiczm.foodyou.app.ui.food.search

import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foodSearchModule = module {
    viewModelOf(::UserFoodSearchViewModel)
    viewModelOf(::OpenFoodFactsSearchViewModel)
    viewModelOf(::FoodDataCentralSearchViewModel)
    viewModelOf(::FavoriteFoodSearchViewModel)
}

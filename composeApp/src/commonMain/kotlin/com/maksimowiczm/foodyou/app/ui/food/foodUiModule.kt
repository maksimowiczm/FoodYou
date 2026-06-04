package com.maksimowiczm.foodyou.app.ui.food

import com.maksimowiczm.foodyou.app.ui.food.details.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.ui.food.details.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.ui.food.details.fooddatacentral.FoodDataCentralDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.details.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.details.userproduct.UserProductDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.recipe.RecipeFormViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foodUiModule = module {
    factoryOf(::ObserveIsFavoriteFoodUseCase)
    factoryOf(::SetFavoriteFoodUseCase)

    viewModelOf(::OpenFoodFactsDetailsViewModel)
    viewModelOf(::FoodDataCentralDetailsViewModel)
    viewModelOf(::UserProductDetailsViewModel)

    viewModelOf(::UserFoodSearchViewModel)
    viewModelOf(::OpenFoodFactsSearchViewModel)
    viewModelOf(::FoodDataCentralSearchViewModel)
    viewModelOf(::FavoriteFoodSearchViewModel)

    viewModelOf(::RecipeFormViewModel)
}

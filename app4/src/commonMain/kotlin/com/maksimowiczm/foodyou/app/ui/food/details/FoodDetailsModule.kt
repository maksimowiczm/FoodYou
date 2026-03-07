package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.app.ui.food.details.fooddatacentral.FoodDataCentralDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.details.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.details.userproduct.UserProductDetailsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foodDetailsModule = module {
    factoryOf(::ObserveIsFavoriteFoodUseCase)
    factoryOf(::SetFavoriteFoodUseCase)

    viewModelOf(::OpenFoodFactsDetailsViewModel)
    viewModelOf(::FoodDataCentralDetailsViewModel)
    viewModelOf(::UserProductDetailsViewModel)
}

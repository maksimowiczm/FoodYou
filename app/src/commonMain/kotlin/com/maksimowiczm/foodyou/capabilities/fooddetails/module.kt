package com.maksimowiczm.foodyou.capabilities.fooddetails

import com.maksimowiczm.foodyou.capabilities.fooddetails.fooddatacentral.FoodDataCentralDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct.UserProductDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe.UserRecipeDetailsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf

fun Module.foodDetails() {
    viewModelOf(::OpenFoodFactsDetailsViewModel)
    viewModelOf(::FoodDataCentralDetailsViewModel)
    viewModel { params ->
        UserProductDetailsViewModel(
            id = params.get(),
            initialQuantity = params.getOrNull(),
            userProductService = get(),
            observeIsFavoriteFoodUseCase = get(),
            setFavoriteFoodUseCase = get(),
            savedStateHandle = get(),
        )
    }
    viewModel { params ->
        UserRecipeDetailsViewModel(
            id = params.get(),
            initialQuantity = params.getOrNull(),
            userRecipeService = get(),
            observeIsFavoriteFoodUseCase = get(),
            setFavoriteFoodUseCase = get(),
            savedStateHandle = get(),
        )
    }
}

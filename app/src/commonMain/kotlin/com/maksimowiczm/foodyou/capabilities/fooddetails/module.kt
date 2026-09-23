package com.maksimowiczm.foodyou.capabilities.fooddetails

import com.maksimowiczm.foodyou.capabilities.fooddetails.fooddatacentral.FoodDataCentralDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct.UserProductDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe.UserRecipeDetailsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

fun Module.foodDetails() {
    viewModel { params ->
        OpenFoodFactsDetailsViewModel(
            id = params.get(),
            initialQuantity = params.getOrNull(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel { params ->
        FoodDataCentralDetailsViewModel(
            id = params.get(),
            initialQuantity = params.getOrNull(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel { params ->
        UserProductDetailsViewModel(
            id = params.get(),
            initialQuantity = params.getOrNull(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel { params ->
        UserRecipeDetailsViewModel(
            id = params.get(),
            initialQuantity = params.getOrNull(),
            get(),
            get(),
            get(),
            get(),
        )
    }
}

package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val foodDetailsModule = module {
    viewModel { (identity: FoodProductIdentity) ->
        FoodDetailsViewModel(
            identity = identity,
            foodProductRepository = get(),
            accountManager = get(),
            logger = get(),
        )
    }
}

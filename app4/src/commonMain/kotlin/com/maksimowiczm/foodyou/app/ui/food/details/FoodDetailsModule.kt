package com.maksimowiczm.foodyou.app.ui.food.details

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val foodDetailsModule = module {
    viewModel { (identity: Any) ->
        FoodDetailsViewModel(identity = identity, get(), get(), get(), get(), get(), get(), get())
    }
}

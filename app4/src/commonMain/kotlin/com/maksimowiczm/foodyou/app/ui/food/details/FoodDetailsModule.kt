package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.app.ui.food.FoodIdentity
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val foodDetailsModule = module {
    viewModel { (identity: FoodIdentity) ->
        FoodDetailsViewModel(
            identity = identity,
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    factory { ObserveFoodUseCase(get(), get(), get()) }
}

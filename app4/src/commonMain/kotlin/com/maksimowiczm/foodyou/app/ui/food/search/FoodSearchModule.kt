package com.maksimowiczm.foodyou.app.ui.food.search

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val foodSearchModule = module {
    viewModel { (initialQuery: String?) ->
        FoodSearchViewModel(
            initialQuery = initialQuery,
            get(),
            get(),
            get(),
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
    factoryOf(::ObserveFavoriteFoodUseCase)
}

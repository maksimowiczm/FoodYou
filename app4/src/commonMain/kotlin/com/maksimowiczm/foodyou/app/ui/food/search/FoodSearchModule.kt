package com.maksimowiczm.foodyou.app.ui.food.search

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val foodSearchModule = module {
    viewModel { (query: String?) ->
        FoodSearchViewModel(
            query = query,
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
}

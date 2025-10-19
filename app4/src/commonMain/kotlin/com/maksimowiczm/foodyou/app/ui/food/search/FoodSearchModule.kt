package com.maksimowiczm.foodyou.app.ui.food.search

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val foodSearchModule = module {
    viewModel { (query: String?) ->
        FoodSearchViewModel(
            query = query,
            foodSearchPreferencesRepository = get(),
            searchHistoryRepository = get(),
            searchableFoodRepository = get(),
            clock = get(),
            searchQueryParser = get(),
            accountManager = get(),
        )
    }
}

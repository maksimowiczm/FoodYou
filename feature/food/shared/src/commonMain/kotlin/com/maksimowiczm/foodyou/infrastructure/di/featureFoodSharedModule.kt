package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.feature.food.shared.presentation.search.FoodSearchViewModel
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureFoodSharedModule = module {
    viewModel { (excluded: FoodId.Recipe?) ->
        FoodSearchViewModel(
            excludedRecipeId = excluded,
            foodSearchPreferencesRepository = userPreferencesRepository(),
            searchHistoryRepository = get(),
            foodSearchRepository = get(),
            foodSearchUseCase = get(),
            dateProvider = get(),
        )
    }
}

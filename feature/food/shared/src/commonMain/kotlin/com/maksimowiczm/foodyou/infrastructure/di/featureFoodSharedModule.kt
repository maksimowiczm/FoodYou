package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.feature.food.shared.presentation.search.FoodSearchViewModel
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureFoodSharedModule = module {
    viewModel { (excluded: FoodId.Recipe?) ->
        FoodSearchViewModel(
            excludedRecipeId = excluded,
            foodSearchPreferencesRepository =
                get(named(FoodSearchPreferences::class.qualifiedName!!)),
            searchHistoryRepository = get(),
            foodSearchRepository = get(),
            foodSearchUseCase = get(),
            dateProvider = get(),
        )
    }
}

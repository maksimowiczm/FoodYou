package com.maksimowiczm.foodyou.app.ui.food.shared

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.app.ui.food.shared.search.FoodSearchViewModel
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiFoodSharedModule = module {
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

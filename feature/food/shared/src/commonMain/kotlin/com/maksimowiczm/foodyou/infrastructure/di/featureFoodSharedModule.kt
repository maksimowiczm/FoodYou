package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.feature.food.shared.presentation.search.FoodSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureFoodSharedModule = module {
    viewModel { (excluded: FoodId.Recipe?) ->
        FoodSearchViewModel(excluded, get(), get(), get(), get(), get())
    }
}

package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.shared.presentation.search.FoodSearchViewModel
import com.maksimowiczm.foodyou.feature.food.shared.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.shared.usecase.ObserveFoodUseCaseImpl
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val featureFoodSharedModule = module {
    factoryOf(::ObserveFoodUseCaseImpl).bind<ObserveFoodUseCase>()
    viewModel { (excluded: FoodId.Recipe?) ->
        FoodSearchViewModel(queryBus = get(), excludedRecipeId = excluded)
    }
}

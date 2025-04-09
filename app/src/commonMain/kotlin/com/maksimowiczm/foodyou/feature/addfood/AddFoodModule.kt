package com.maksimowiczm.foodyou.feature.addfood

import com.maksimowiczm.foodyou.feature.addfood.domain.ObserveRecentQueriesUseCase
import com.maksimowiczm.foodyou.feature.addfood.domain.ObserveRecentQueriesUseCaseImpl
import com.maksimowiczm.foodyou.feature.addfood.domain.ObserveSearchFoodUseCase
import com.maksimowiczm.foodyou.feature.addfood.domain.ObserveSearchFoodUseCaseImpl
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val addFoodModule = module {
    viewModelOf(::SearchFoodViewModel)

    factoryOf(::ObserveRecentQueriesUseCaseImpl).bind<ObserveRecentQueriesUseCase>()

    factoryOf(::ObserveRecentQueriesUseCaseImpl).bind<ObserveRecentQueriesUseCase>()

    factoryOf(::ObserveSearchFoodUseCaseImpl).bind<ObserveSearchFoodUseCase>()
}

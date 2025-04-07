package com.maksimowiczm.foodyou.feature.diary.addfood.searchfood

import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain.ObserveRecentQueriesUseCase
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain.ObserveRecentQueriesUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain.ObserveSearchFoodUseCase
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain.ObserveSearchFoodUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.ui.SearchFoodViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

val searchFoodModuleDeclaration: ModuleDeclaration = {
    viewModelOf(::SearchFoodViewModel)

    factoryOf(::ObserveRecentQueriesUseCaseImpl).bind<ObserveRecentQueriesUseCase>()

    factoryOf(::ObserveRecentQueriesUseCaseImpl).bind<ObserveRecentQueriesUseCase>()

    factoryOf(::ObserveSearchFoodUseCaseImpl).bind<ObserveSearchFoodUseCase>()
}

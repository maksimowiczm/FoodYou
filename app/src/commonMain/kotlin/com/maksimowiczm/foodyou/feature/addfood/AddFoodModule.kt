package com.maksimowiczm.foodyou.feature.addfood

import com.maksimowiczm.foodyou.feature.addfood.data.SearchRepository
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val addFoodModule = module {
    viewModelOf(::SearchFoodViewModel)

    factory {
        SearchRepository(
            database = get(),
            remoteMediatorFactory = get()
        )
    }
}

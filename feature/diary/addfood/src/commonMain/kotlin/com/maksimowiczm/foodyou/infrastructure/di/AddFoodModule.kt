package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.diary.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.addfood.ui.search.SearchFoodViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val addFoodModule = module {
    viewModelOf(::SearchFoodViewModel)

    factory { AddFoodRepository(get(), get(), get(), get(), get()) }
}

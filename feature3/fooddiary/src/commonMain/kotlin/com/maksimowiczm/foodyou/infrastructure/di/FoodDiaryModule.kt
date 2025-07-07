package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.FoodSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foodDiaryModule = module {
    viewModelOf(::FoodSearchViewModel)
}

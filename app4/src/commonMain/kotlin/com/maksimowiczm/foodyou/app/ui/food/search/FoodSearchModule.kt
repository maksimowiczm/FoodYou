package com.maksimowiczm.foodyou.app.ui.food.search

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val foodSearchModule = module { viewModelOf(::FoodSearchViewModel) }

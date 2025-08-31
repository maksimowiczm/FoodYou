package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.goals.master.GoalsViewModel
import com.maksimowiczm.foodyou.feature.goals.setup.DailyGoalsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureGoalsModule = module {
    viewModelOf(::GoalsViewModel)
    viewModelOf(::DailyGoalsViewModel)
}

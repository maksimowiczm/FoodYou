package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.goals.presentation.GoalsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureGoalsModule = module { viewModelOf(::GoalsViewModel) }

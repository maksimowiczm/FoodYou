package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.settings.goals.presentation.DailyGoalsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureSettingsGoalsModule = module { viewModelOf(::DailyGoalsViewModel) }

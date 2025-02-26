package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.home.calendarcard.ui.CalendarViewModel
import com.maksimowiczm.foodyou.feature.settings.goalssettings.ui.GoalsSettingsViewModel
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.MealsSettingsViewModel
import com.maksimowiczm.foodyou.ui.DiaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureModule = module {
    viewModelOf(::CalendarViewModel)

    viewModelOf(::GoalsSettingsViewModel)
    viewModelOf(::MealsSettingsViewModel)

    viewModelOf(::DiaryViewModel)
}

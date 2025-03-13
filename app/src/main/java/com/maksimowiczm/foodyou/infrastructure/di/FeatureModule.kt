package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.settings.language.ui.AndroidLanguageViewModel
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageViewModel
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.MealsSettingsViewModel
import com.maksimowiczm.foodyou.ui.DiaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureModule = module {
    // TODO move somewhere else
    // -- Shared
    viewModelOf(::DiaryViewModel)

    // -- Meals Settings
    viewModelOf(::MealsSettingsViewModel)

    // -- Language
    viewModelOf(::AndroidLanguageViewModel).bind<LanguageViewModel>()
}

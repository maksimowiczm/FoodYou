package com.maksimowiczm.foodyou.preferences

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.preferences.application.RandomizeThemeOnAppLaunchHandler
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.infrastructure.EnergyUnitPreferenceProvider
import com.maksimowiczm.foodyou.preferences.infrastructure.FoodDiaryEntryTimestampsProvider
import com.maksimowiczm.foodyou.preferences.infrastructure.HideScreenProvider
import com.maksimowiczm.foodyou.preferences.infrastructure.LanguagePreferenceProvider
import com.maksimowiczm.foodyou.preferences.infrastructure.NutrientsOrderPreferenceProvider
import com.maksimowiczm.foodyou.preferences.infrastructure.ThemePreferenceProvider
import com.maksimowiczm.foodyou.preferences.infrastructure.UserPreferencesRepositoryImpl
import com.maksimowiczm.foodyou.preferences.infrastructure.languagePreferenceProvider
import org.koin.core.module.Module
import org.koin.dsl.bind

internal fun Module.preferences() {
    eventHandlerOf(::RandomizeThemeOnAppLaunchHandler)
    languagePreferenceProvider()
    factory {
        UserPreferencesRepositoryImpl(
            ThemePreferenceProvider(get()),
            HideScreenProvider(get()),
            get<LanguagePreferenceProvider>(),
            EnergyUnitPreferenceProvider(get()),
            NutrientsOrderPreferenceProvider(get()),
            FoodDiaryEntryTimestampsProvider(get()),
        )
    }
        .bind<UserPreferencesRepository>()
}

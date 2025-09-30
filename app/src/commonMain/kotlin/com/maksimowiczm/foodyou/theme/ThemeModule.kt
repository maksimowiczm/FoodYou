package com.maksimowiczm.foodyou.theme

import com.maksimowiczm.foodyou.common.infrastructure.koin.eventHandler
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepositoryOf
import org.koin.dsl.module

val themeModule = module {
    userPreferencesRepositoryOf(::DataStoreThemeSettingsRepository)
    userPreferencesRepositoryOf(::DataStoreNutrientsColorsRepository)
    factory { RandomizeThemeUseCase(userPreferencesRepository(), get()) }
    eventHandler {
        RandomizeThemeOnLaunchIntegrationEventHandler(userPreferencesRepository(), get())
    }
}

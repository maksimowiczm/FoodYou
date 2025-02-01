package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.settings.data.SettingsRepository
import com.maksimowiczm.foodyou.feature.settings.data.SettingsRepositoryImpl
import com.maksimowiczm.foodyou.feature.settings.ui.fooddatabase.FoodDatabaseSettingsViewModel
import com.maksimowiczm.foodyou.feature.settings.ui.fooddatabase.country.CountryFlag
import com.maksimowiczm.foodyou.feature.settings.ui.fooddatabase.country.flagCdnCountryFlag
import com.maksimowiczm.foodyou.feature.settings.ui.goals.GoalsSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::FoodDatabaseSettingsViewModel)
    viewModelOf(::GoalsSettingsViewModel)

    single { flagCdnCountryFlag }.bind<CountryFlag>()

    factoryOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
}

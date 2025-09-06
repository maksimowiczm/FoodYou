package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.feature.database.externaldatabases.presentation.ExternalDatabasesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureSettingsDatabaseExternalDatabasesModule = module {
    viewModel {
        ExternalDatabasesViewModel(
            foodSearchPreferencesRepository =
                get(named(FoodSearchPreferences::class.qualifiedName!!))
        )
    }
}

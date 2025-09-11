package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.app.business.opensource.di.userPreferencesRepository
import com.maksimowiczm.foodyou.feature.database.externaldatabases.presentation.ExternalDatabasesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureSettingsDatabaseExternalDatabasesModule = module {
    viewModel {
        ExternalDatabasesViewModel(foodSearchPreferencesRepository = userPreferencesRepository())
    }
}

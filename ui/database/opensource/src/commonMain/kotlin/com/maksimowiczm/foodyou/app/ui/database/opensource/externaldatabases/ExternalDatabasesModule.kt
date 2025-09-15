package com.maksimowiczm.foodyou.app.ui.database.opensource.externaldatabases

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

internal fun Module.externalDatabasesModule() {
    viewModel {
        ExternalDatabasesViewModel(foodSearchPreferencesRepository = userPreferencesRepository())
    }
}

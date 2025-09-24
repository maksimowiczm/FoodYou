package com.maksimowiczm.foodyou.app.ui.database.externaldatabases

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

internal fun Module.externalDatabasesModule() {
    viewModel {
        ExternalDatabasesViewModel(foodSearchPreferencesRepository = userPreferencesRepository())
    }
}

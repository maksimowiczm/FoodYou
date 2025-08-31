package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.database.externaldatabases.presentation.ExternalDatabasesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureSettingsDatabaseExternalDatabasesModule = module {
    viewModelOf(::ExternalDatabasesViewModel)
}

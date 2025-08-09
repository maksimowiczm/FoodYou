package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.settings.externaldatabases.presentation.ExternalDatabasesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureSettingsDatabaseModule = module { viewModelOf(::ExternalDatabasesViewModel) }

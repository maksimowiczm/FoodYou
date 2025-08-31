package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.database.databasedump.presentation.DatabaseDumpViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureSettingsDatabaseDatabaseDumpModule = module { viewModelOf(::DatabaseDumpViewModel) }
